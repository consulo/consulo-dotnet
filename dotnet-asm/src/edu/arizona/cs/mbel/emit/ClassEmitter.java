/* MBEL: The Microsoft Bytecode Engineering Library
 * Copyright (C) 2003 The University of Arizona
 * http://www.cs.arizona.edu/mbel/license.html
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.arizona.cs.mbel.emit;

import java.util.ArrayList;
import java.util.List;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.instructions.LoadableType;
import edu.arizona.cs.mbel.mbel.*;
import edu.arizona.cs.mbel.metadata.GenericTable;
import edu.arizona.cs.mbel.metadata.Metadata;
import edu.arizona.cs.mbel.metadata.TableConstants;
import edu.arizona.cs.mbel.parse.CLIHeader;
import edu.arizona.cs.mbel.signature.CustomAttributeOwner;
import edu.arizona.cs.mbel.signature.LocalVarList;
import edu.arizona.cs.mbel.signature.MarshalSignature;
import edu.arizona.cs.mbel.signature.MethodSignature;
import edu.arizona.cs.mbel.signature.ParameterInfo;
import edu.arizona.cs.mbel.signature.ParameterSignature;

/**
 * This class is used to take a Module and output the CLI Header and metadata portion of a
 * .NET module to a buffer. This class is used by Emitter to write out the .NET specific parts
 * of the PE/COFF file.
 *
 * @author Michael Stepp
 */
public class ClassEmitter
{
	private Module module;
	private StringsStreamGen stringsGen;
	private BlobStreamGen blobGen;
	private GUIDStreamGen guidGen;
	private USStreamGen usGen;
	private List<GenericTable>[] tables;
	private TableConstants tc = null;
	/////////////////////////////////////////////////////
	private long TypeDefCount = 1;
	private long TypeRefCount = 1;
	private long TypeSpecCount = 1;
	private long MethodCount = 1;
	private long FieldCount = 1;
	private long EventCount = 1;
	private long ParamCount = 1;
	private long PropertyCount = 1;
	private long FileCount = 1;
	private long MemberRefCount = 1;
	private long ExportedTypeCount = 1;
	private long EntryPointToken = 0L;
	////////////////////////////////////
	private ByteBuffer localResources;   // ByteBuffer
	private List<ByteBuffer> methodBodies;                   // vector of ByteBuffers
	private PatchList netPatches;

	/**
	 * Creates and initializes a ClassEmitter.
	 * Initializes the table arrays, streamgens, and buffers for local resources and method bodies.
	 */
	public ClassEmitter(Module mod)
	{
		module = mod;

		stringsGen = new StringsStreamGen();
		blobGen = new BlobStreamGen();
		stringsGen = new StringsStreamGen();
		blobGen = new BlobStreamGen();
		guidGen = new GUIDStreamGen();
		usGen = new USStreamGen();
		tables = new ArrayList[64];

		for(int i = 0; i < 64; i++)
		{
			tables[i] = new ArrayList<GenericTable>(10);
		}
		////////////////////////////////////////////////

		localResources = new ByteBuffer(2000);
		methodBodies = new ArrayList<ByteBuffer>(10);
	}

	/**
	 * Builds the metadata tables from the Module given in the constructor.
	 * This method also builds the method bodies and local manifest resources,
	 * along with the entry point token.
	 */
	public void buildTables()
	{

		buildAssembly();
		buildModule();
		{// add file references from current module (more might come from elsewhere)
			List<FileReference> frs = module.getFileReferences();
			for(FileReference fr : frs)
			{
				addFile(fr);
			}
		}
		buildManifestResources();

		List<TypeDef> defs = module.getTypeDefs();
		for(TypeDef def7 : defs)
		{
			addTypeDef(def7);
		}
		for(TypeDef def6 : defs)
		{
			buildEvents(def6);
		}
		for(TypeDef def5 : defs)
		{
			buildProperties(def5);
		}
		for(TypeDef def4 : defs)
		{
			buildFields(def4);
		}
		for(TypeDef def3 : defs)
		{
			buildMethods(def3);
		}
		for(TypeDef def2 : defs)
		{
			buildMethodBodies(def2);
		}
		for(TypeDef def1 : defs)
		{
			buildMethodImpls(def1);
		}
		for(TypeDef def : defs)
		{
			buildInterfaceImpls(def);
		}

		buildEntryPointToken();
	}

	/**
	 * Writes out the CLI header and metadata to the given buffer.
	 * Not to be called until after buildTables.
	 *
	 * @param netBuffer a buffer corresponding to the .net section in a PE file, assumed to be at position 0
	 */
	public void emitMetadata(ByteBuffer netBuffer)
	{
		// netBuffer will be read-only
		// not to be called until after buildTables

		netPatches = new PatchList();

		Metadata metadata = module.getPEModule().metadata;
		CLIHeader cliHeader = module.getPEModule().cliHeader;

		////// emit CLR Header /////////////////////////////
		netBuffer.pad(4);
		netBuffer.putDWORD(72);
		netBuffer.putWORD(cliHeader.MajorRuntimeVersion);
		netBuffer.putWORD(cliHeader.MinorRuntimeVersion);
		netPatches.addPatch(netBuffer.getPosition());
		long metadataRVAStart = netBuffer.getPosition();
		netBuffer.putDWORD(0);  // wrong value (Metadata RVA)
		netBuffer.putDWORD(0);  // wrong value (Metadata size (includes all streams/tables))
		netBuffer.putDWORD(cliHeader.Flags & (~CLIHeader.COMIMAGE_FLAGS_STRONGNAMESIGNED) & (~CLIHeader.COMIMAGE_FLAGS_TRACKDEBUGDATA));
		netBuffer.putDWORD(EntryPointToken);
		long resourceRVAStart = netBuffer.getPosition();
		// remember to potentially add a patch here!
		netBuffer.putDWORD(0);  // wrong value (Resource RVA)
		netBuffer.putDWORD(0);  // wrong value (Resource size)
		netBuffer.putDWORD(0);  // StrongNameSignature RVA
		netBuffer.putDWORD(0);  // StrongNameSignature size
		netBuffer.putDWORD(0);  // CodeManagerTable RVA
		netBuffer.putDWORD(0);  // CodeManagerTable size
		long vtableRVAStart = netBuffer.getPosition();
		//netBuffer.putDWORD(0);  // wrong value (vtable RVA)
		//netBuffer.putDWORD(0);  // wrong value (vtable size)
		cliHeader.VTableFixups.emit(netBuffer);


		netBuffer.putDWORD(0);  // ExportAddressTableJumps RVA
		netBuffer.putDWORD(0);  // ExportAddressTableJumps size
		netBuffer.putDWORD(0);  // ManagedNativeHeader RVA
		netBuffer.putDWORD(0);  // ManagedNativeHeader size
		///// end of CLI header ////////////////////////////


		////// emit Metadata header ///////////////////////
		netBuffer.pad(4);
		long metadataStart = netBuffer.getPosition();
		netBuffer.putDWORD(0x424A5342L);
		netBuffer.putWORD(metadata.MajorVersion);
		netBuffer.putWORD(metadata.MinorVersion);
		netBuffer.putDWORD(0);
		netBuffer.putDWORD(metadata.Length);
		netBuffer.put(metadata.VersionString);
		netBuffer.pad(4);
		netBuffer.putWORD(0);
		netBuffer.putWORD(5);

		// emit stream headers
		// #~ header
		long compHeaderStart = netBuffer.getPosition();
		netBuffer.putDWORD(0);// wrong value (~Stream offset)
		netBuffer.putDWORD(0);// wrong value (~Stream size (mult of 4))
		netBuffer.put((byte) '#');
		netBuffer.put((byte) '~');
		netBuffer.put(0);
		netBuffer.pad(4);
		// #Strings header
		long stringsHeaderStart = netBuffer.getPosition();
		netBuffer.putDWORD(0);// wrong value (StringsStream offset)
		netBuffer.putDWORD(0);// wrong value (StringsStream size (mult of 4))
		netBuffer.put((byte) '#');
		netBuffer.put((byte) 'S');
		netBuffer.put((byte) 't');
		netBuffer.put((byte) 'r');
		netBuffer.put((byte) 'i');
		netBuffer.put((byte) 'n');
		netBuffer.put((byte) 'g');
		netBuffer.put((byte) 's');
		netBuffer.put(0);
		netBuffer.pad(4);
		// #US header
		long usHeaderStart = netBuffer.getPosition();
		netBuffer.putDWORD(0);// wrong value (USStream offset)
		netBuffer.putDWORD(0);// wrong value (USStream size (mult of 4))
		netBuffer.put((byte) '#');
		netBuffer.put((byte) 'U');
		netBuffer.put((byte) 'S');
		netBuffer.put(0);
		netBuffer.pad(4);
		// #GUID header
		long guidHeaderStart = netBuffer.getPosition();
		netBuffer.putDWORD(0);// wrong value (GUIDStream offset)
		netBuffer.putDWORD(0);// wrong value (GUIDStream size (mult of 4))
		netBuffer.put((byte) '#');
		netBuffer.put((byte) 'G');
		netBuffer.put((byte) 'U');
		netBuffer.put((byte) 'I');
		netBuffer.put((byte) 'D');
		netBuffer.put(0);
		netBuffer.pad(4);
		// #Blob header
		long blobHeaderStart = netBuffer.getPosition();
		netBuffer.putDWORD(0);// wrong value (BlobStream offset)
		netBuffer.putDWORD(0);// wrong value (BlobStream size (mult of 4))
		netBuffer.put((byte) '#');
		netBuffer.put((byte) 'B');
		netBuffer.put((byte) 'l');
		netBuffer.put((byte) 'o');
		netBuffer.put((byte) 'b');
		netBuffer.put(0);
		netBuffer.pad(4);

		// emit streams
		// #~ stream
		long compStart = netBuffer.getPosition();
		netBuffer.putDWORD(0);
		netBuffer.put(1);
		netBuffer.put(0);
		int heapsizes = (stringsGen.getLength() >= 65536 ? 0x01 : 0) |
				((guidGen.getNumGUIDS() * 16) >= 65536 ? 0x02 : 0) |
				(blobGen.getLength() >= 65536 ? 0x04 : 0);
		netBuffer.put(heapsizes);
		netBuffer.put(1);
		long valid = 0L;
		for(int i = 0; i < 64; i++)
		{
			if(tables[i].size() > 0)
			{
				valid |= (1L << i);
			}
		}
		netBuffer.putINT64(valid);
		netBuffer.putINT64(0);
		for(int i = 0; i < 64; i++)
		{
			if(tables[i].size() > 0)
			{
				netBuffer.putDWORD((long) tables[i].size());
			}
		}


		long[] bodyRVAStarts = new long[tables[TableConstants.Method].size()];
		for(int i = 0; i < 64; i++)
		{
			if(tables[i].size() > 0)
			{
				if(i == TableConstants.Method)
				{
					for(int j = 0; j < tables[i].size(); j++)
					{
						GenericTable methodTable = tables[i].get(j);
						long RVA = methodTable.getConstant("RVA").longValue();
						int Flags = methodTable.getConstant("ImplFlags").intValue();

						if((RVA != 0) && (Flags & MethodDef.CodeTypeMask) == MethodDef.IL)
						{
							netPatches.addPatch(netBuffer.getPosition());
							bodyRVAStarts[j] = netBuffer.getPosition();
						}
						else
						{
							bodyRVAStarts[j] = -1L;
						}

						methodTable.emit(netBuffer, this);
					}// now deal with method bodies later
				}
				else
				{
					for(int j = 0; j < tables[i].size(); j++)
					{
						tables[i].get(j).emit(netBuffer, this);
					}
				}
			}
		}
		netBuffer.pad(4);
		long compLength = netBuffer.getPosition() - compStart;

		// #Strings Stream
		long stringsStart = netBuffer.getPosition();
		stringsGen.emit(netBuffer);
		netBuffer.pad(4);
		long stringsLength = netBuffer.getPosition() - stringsStart;

		// #US stream
		long usStart = netBuffer.getPosition();
		usGen.emit(netBuffer);
		netBuffer.pad(4);
		long usLength = netBuffer.getPosition() - usStart;

		// #GUID stream
		long guidStart = netBuffer.getPosition();
		guidGen.emit(netBuffer);
		netBuffer.pad(4);
		long guidLength = netBuffer.getPosition() - guidStart;

		// #Blob stream
		long blobStart = netBuffer.getPosition();
		blobGen.emit(netBuffer);
		netBuffer.pad(4);
		long blobLength = netBuffer.getPosition() - blobStart;
		long metadataLength = netBuffer.getPosition() - metadataStart;

		/// patch metadata record in CLR header ////////
		long end = netBuffer.getPosition();
		netBuffer.setPosition((int) metadataRVAStart);
		netBuffer.putDWORD(metadataStart);
		netBuffer.putDWORD(metadataLength);
		netBuffer.setPosition((int) end);


		/////////////// patch up the offsets from above //////////////////////////////////
		end = netBuffer.getPosition();
		// fix #~
		netBuffer.setPosition((int) compHeaderStart);
		netBuffer.putDWORD(compStart - metadataStart);
		netBuffer.putDWORD(compLength);
		// fix #Strings header
		netBuffer.setPosition((int) stringsHeaderStart);
		netBuffer.putDWORD(stringsStart - metadataStart);
		netBuffer.putDWORD(stringsLength);
		// fix #US
		netBuffer.setPosition((int) usHeaderStart);
		netBuffer.putDWORD(usStart - metadataStart);
		netBuffer.putDWORD(usLength);
		// fix #GUID
		netBuffer.setPosition((int) guidHeaderStart);
		netBuffer.putDWORD(guidStart - metadataStart);
		netBuffer.putDWORD(guidLength);
		// fix #Blob
		netBuffer.setPosition((int) blobHeaderStart);
		netBuffer.putDWORD(blobStart - metadataStart);
		netBuffer.putDWORD(blobLength);

		netBuffer.setPosition((int) end);

		///////////// emit method bodies, then set the body offsets  /////////////
		long[] bodyStarts = new long[methodBodies.size()];
		for(int i = 0; i < methodBodies.size(); i++)
		{
			ByteBuffer bodybuf = methodBodies.get(i);
			netBuffer.pad(4);
			bodyStarts[i] = netBuffer.getPosition();
			netBuffer.concat(bodybuf);
		}
		// set RVAs (unbiased)
		end = netBuffer.getPosition();
		int count = 0;
		for(long bodyRVAStart : bodyRVAStarts)
		{
			if(bodyRVAStart != -1L)
			{
				netBuffer.setPosition((int) bodyRVAStart);
				netBuffer.putDWORD(bodyStarts[count++]);
			}
		}
		netBuffer.setPosition((int) end);

		//// emit CLI resources, then patch resources address //////////////////////
		netBuffer.pad(4);
		long resourceStart = netBuffer.getPosition();
		if(localResources.getPosition() > 0)
		{
			netPatches.addPatch(resourceRVAStart);

			netBuffer.concat(localResources);
			netBuffer.pad(4);
			long resourceLength = netBuffer.getPosition() - resourceStart;

			end = netBuffer.getPosition();
			netBuffer.setPosition((int) resourceRVAStart);
			netBuffer.putDWORD(resourceStart);
			netBuffer.putDWORD(resourceLength);
			netBuffer.setPosition((int) end);
		}


		/// emit VTableFixups FIX!!!
	}

	private void buildAssembly()
	{
		// DONE!
		AssemblyInfo assemblyInfo = module.getAssemblyInfo();
		if(assemblyInfo != null)
		{
			GenericTable assemTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Assembly]);
			tables[TableConstants.Assembly].add(assemTable);

			assemTable.setFieldValue("HashAlgID", assemblyInfo.getHashAlg());
			assemTable.setFieldValue("MajorVersion", assemblyInfo.getMajorVersion());
			assemTable.setFieldValue("MinorVersion", assemblyInfo.getMinorVersion());
			assemTable.setFieldValue("BuildNumber", assemblyInfo.getBuildNumber());
			assemTable.setFieldValue("RevisionNumber", assemblyInfo.getRevisionNumber() + 1);
			assemTable.setFieldValue("Flags", assemblyInfo.getFlags());
			assemTable.setFieldValue("PublicKey", blobGen.addBlob(assemblyInfo.getPublicKey()));
			assemTable.setFieldValue("Name", stringsGen.addString(assemblyInfo.getName()));
			assemTable.setFieldValue("Culture", stringsGen.addString(assemblyInfo.getCulture()));

			// Make DeclSecurity
			DeclSecurity decl = assemblyInfo.getDeclSecurity();
			if(decl != null)
			{
				GenericTable declTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.DeclSecurity]);
				declTable.setFieldValue("Action", decl.getAction());
				long coded = TableConstants.buildCodedIndex(TableConstants.HasDeclSecurity, TableConstants.Assembly, 1L);
				declTable.setFieldValue("Parent", coded);
				declTable.setFieldValue("PermissionSet", blobGen.addBlob(decl.getPermissionSet()));

				tables[TableConstants.DeclSecurity].add(declTable);

				// Make CustomAttributes (on DeclSecurity)
				long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.DeclSecurity,
						tables[TableConstants.DeclSecurity].size());
				addCustomAttributes(decl, codedparent);
			}

			// Make ExportedTypes
			ExportedTypeRef[] erefs = assemblyInfo.getExportedTypes();
			for(ExportedTypeRef eref : erefs)
			{
				addExportedType(eref);
			}

			// Make CustomAttributes
			long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.Assembly, 1L);
			addCustomAttributes(assemblyInfo, codedparent);
		}
	}

	private void buildModule()
	{
		// DONE!
		GenericTable modTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Module]);
		modTable.setFieldValue("Generation", module.getGeneration());
		modTable.setFieldValue("Name", stringsGen.addString(module.getName()));
		modTable.setFieldValue("Mvid", guidGen.addGUID(module.getMvidGUID()));
		modTable.setFieldValue("EncID", guidGen.addGUID(module.getEncIdGUID()));
		modTable.setFieldValue("EncBaseID", guidGen.addGUID(module.getEncBaseIdGUID()));

		tables[TableConstants.Module].add(modTable);

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.Module, 1L);
		addCustomAttributes(module, codedparent);
	}

	private long addFile(FileReference fr)
	{
		// returns a valid RID (1-based) DONE!!
		long index = fr.getFileRID();
		if(index == -1L)
		{
			GenericTable fileTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.File]);

			fr.setFileRID(FileCount++);
			tables[TableConstants.File].add(fileTable);

			fileTable.setFieldValue("Flags", fr.getFlags());
			fileTable.setFieldValue("Name", stringsGen.addString(fr.getFileName()));
			fileTable.setFieldValue("HashValue", blobGen.addBlob(fr.getHashValue()));
			index = fr.getFileRID();
		}

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.File, index);
		addCustomAttributes(fr, codedparent);

		return index;
	}

	private void buildManifestResources()
	{
		// DONE!
		ManifestResource[] res = module.getManifestResources();
		GenericTable manTable = null;

		for(ManifestResource re : res)
		{
			if(re instanceof LocalManifestResource)
			{
				// DONE!!!
				LocalManifestResource lmr = (LocalManifestResource) re;

				manTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.ManifestResource]);
				manTable.setFieldValue("Offset", (long) localResources.getPosition());
				manTable.setFieldValue("Flags", lmr.getFlags());
				manTable.setFieldValue("Name", stringsGen.addString(lmr.getName()));
				manTable.setFieldValue("Implementation", (long) 0);
				tables[TableConstants.ManifestResource].add(manTable);

				byte[] data = lmr.getResourceData();
				localResources.putINT32(data.length);
				localResources.put(data);
				localResources.pad(4);
			}
			else if(re instanceof FileManifestResource)
			{
				// DONE!
				FileManifestResource fmr = (FileManifestResource) re;
				manTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.ManifestResource]);
				manTable.setFieldValue("Offset", (long) 0);
				manTable.setFieldValue("Flags", re.getFlags());
				manTable.setFieldValue("Name", stringsGen.addString(re.getName()));

				long rid = addFile(fmr.getFileReference());
				long coded = TableConstants.buildCodedIndex(TableConstants.Implementation, TableConstants.File, rid);
				manTable.setFieldValue("Implementation", coded);
				tables[TableConstants.ManifestResource].add(manTable);
			}
			else if(re instanceof AssemblyManifestResource)
			{
				// DONE!!
				AssemblyManifestResource amr = (AssemblyManifestResource) re;

				long assemblyRef = addAssemblyRef(amr.getAssemblyRefInfo());

				manTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.ManifestResource]);
				manTable.setFieldValue("Offset", (long) 0);
				manTable.setFieldValue("Flags", re.getFlags());
				manTable.setFieldValue("Name", stringsGen.addString(re.getName()));

				long coded = TableConstants.buildCodedIndex(TableConstants.Implementation, TableConstants.AssemblyRef, assemblyRef);
				manTable.setFieldValue("Implementation", coded);
				tables[TableConstants.ManifestResource].add(manTable);
			}

			// Make CustomAttributes
			long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.ManifestResource,
					tables[TableConstants.ManifestResource].size());
			addCustomAttributes(re, codedparent);
		}
	}

	private long addExportedType(ExportedTypeRef ref)
	{
		// adds this as an ExportedType, NOT as a TypeRef!!! returns an ExportedType RID
		// DONE!!!
		if(ref.getExportedTypeRID() != -1L)
		{
			return ref.getExportedTypeRID();
		}

		GenericTable exTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.ExportedType]);
		tables[TableConstants.ExportedType].add(exTable);
		ref.setExportedTypeRID(ExportedTypeCount++);

		exTable.setFieldValue("Flags", ref.getFlags());
		exTable.setFieldValue("TypeDefID", (long) 0);
		exTable.setFieldValue("TypeName", stringsGen.addString(ref.getName()));
		exTable.setFieldValue("TypeNamespace", stringsGen.addString(ref.getNamespace()));

		long coded = 0;
		if(ref.getFileReference() != null)
		{
			FileReference fr = ref.getFileReference();
			long rid = addFile(fr);
			coded = TableConstants.buildCodedIndex(TableConstants.Implementation, TableConstants.File, rid);
		}
		else
		{
			long rid = addExportedType(ref.getExportedTypeRef());
			coded = TableConstants.buildCodedIndex(TableConstants.Implementation, TableConstants.ExportedType, rid);
		}
		exTable.setFieldValue("Implementation", coded);

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.ExportedType, ref.getExportedTypeRID());
		addCustomAttributes(ref, codedparent);

		return ref.getExportedTypeRID();
	}

	private long addTypeDef(TypeDef def)
	{
		// all TypeDefs must be registered with the Module, so any others found will be errors
		if(def.getTypeDefRID() != -1L)
		{
			return def.getTypeDefRID();
		}

		GenericTable defTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.TypeDef]);

		def.setTypeDefRID(TypeDefCount++);
		tables[TableConstants.TypeDef].add(defTable);

		defTable.setFieldValue("Flags", def.getFlags());
		defTable.setFieldValue("Name", stringsGen.addString(def.getName()));
		defTable.setFieldValue("Namespace", stringsGen.addString(def.getNamespace()));

		Object parent = def.getSuperClass();
		if(parent == null)
		{
			defTable.setFieldValue("Extends", (long) 0);
		}
		else if(parent instanceof TypeDef)
		{
			long rid = addTypeDef((TypeDef) parent);
			long coded = TableConstants.buildCodedIndex(TableConstants.TypeDefOrRefOrSpec, TableConstants.TypeDef, rid);
			defTable.setFieldValue("Extends", coded);
		}
		else if(parent instanceof TypeRef)
		{
			long rid = addTypeRef((TypeRef) parent);
			long coded = TableConstants.buildCodedIndex(TableConstants.TypeDefOrRefOrSpec, TableConstants.TypeRef, rid);
			defTable.setFieldValue("Extends", coded);
		}
		else if(parent instanceof TypeSpec)
		{
			long rid = addTypeSpec((TypeSpec) parent);
			long coded = TableConstants.buildCodedIndex(TableConstants.TypeDefOrRefOrSpec, TableConstants.TypeSpec, rid);
			defTable.setFieldValue("Extends", coded);
		}

		//////////////////////////////////////////////////////////////////
		// Make NestedClass tables DONE
		TypeDef[] nested = def.getNestedClasses();
		for(TypeDef aNested : nested)
		{
			GenericTable nestTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.NestedClass]);
			long myIndex = addTypeDef(aNested);
			nestTable.setFieldValue("NestedClass", myIndex);
			nestTable.setFieldValue("EnclosingClass", def.getTypeDefRID());
			tables[TableConstants.NestedClass].add(nestTable);
		}

		// Make ClassLayouts DONE
		ClassLayout layout = def.getClassLayout();
		if(layout != null)
		{
			GenericTable layoutTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.ClassLayout]);
			layoutTable.setFieldValue("PackingSize", layout.getPackingSize());
			layoutTable.setFieldValue("ClassSize", layout.getClassSize());
			layoutTable.setFieldValue("Parent", def.getTypeDefRID());
			tables[TableConstants.ClassLayout].add(layoutTable);
		}

		// Make DeclSecurity DONE
		DeclSecurity decl = def.getDeclSecurity();
		if(decl != null)
		{
			GenericTable declTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.DeclSecurity]);
			declTable.setFieldValue("Action", decl.getAction());
			long coded = TableConstants.buildCodedIndex(TableConstants.HasDeclSecurity, TableConstants.TypeDef, def.getTypeDefRID());
			declTable.setFieldValue("Parent", coded);
			declTable.setFieldValue("PermissionSet", blobGen.addBlob(decl.getPermissionSet()));
			tables[TableConstants.DeclSecurity].add(declTable);

			// Make CustomAttributes (on DeclSecurity)
			long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.DeclSecurity, tables[TableConstants
					.DeclSecurity].size());
			addCustomAttributes(decl, codedparent);
		}

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.TypeDef, def.getTypeDefRID());
		addCustomAttributes(def, codedparent);
		//////////////////////////////////////////////////////////////////

		return def.getTypeDefRID();
	}

	private void buildFields(TypeDef def)
	{
		// Make Fields
		List<Field> defFields = def.getFields();
		GenericTable defTable = tables[TableConstants.TypeDef].get((int) def.getTypeDefRID()
				- 1);
		if(defFields.isEmpty())
		{
			defTable.setFieldValue("FieldList", FieldCount);
		}
		else
		{
			long fieldStart = FieldCount;
			for(Field defField : defFields)
			{
				addField(defField);
			}
			defTable.setFieldValue("FieldList", fieldStart);
		}
	}

	private void buildMethods(TypeDef def)
	{
		// Make Methods
		List<MethodDef> defMethods = def.getMethods();
		GenericTable defTable = tables[TableConstants.TypeDef].get((int) def.getTypeDefRID()
				- 1);

		if(defMethods.isEmpty())
		{
			defTable.setFieldValue("MethodList", MethodCount);
		}
		else
		{
			long methodStart = MethodCount;
			for(MethodDef defMethod : defMethods)
			{
				addMethod(defMethod);
			}
			defTable.setFieldValue("MethodList", methodStart);
		}
	}

	private long addField(Field field)
	{
		// assume this field is not in the list yet DONE!
		if(field.getFieldRID() != -1L)
		{
			return field.getFieldRID();
		}

		GenericTable fieldTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Field]);
		tables[TableConstants.Field].add(fieldTable);
		field.setFieldRID(FieldCount++);

		fieldTable.setFieldValue("Flags", field.getFlags());
		fieldTable.setFieldValue("Name", stringsGen.addString(field.getName()));
		ByteBuffer fieldbuffer = new ByteBuffer(100);
		field.getSignature().emit(fieldbuffer, this);
		byte[] blob = fieldbuffer.toByteArray();
		fieldTable.setFieldValue("Signature", blobGen.addBlob(blob));

		// Make FieldMarshal DONE!
		MarshalSignature sig = field.getFieldMarshal();
		if(sig != null)
		{
			GenericTable marTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.FieldMarshal]);

			long coded = TableConstants.buildCodedIndex(TableConstants.HasFieldMarshal, TableConstants.Field, field.getFieldRID());
			marTable.setFieldValue("Parent", coded);

			ByteBuffer marbuffer = new ByteBuffer(100);
			sig.emit(marbuffer, this);
			byte[] data = marbuffer.toByteArray();
			marTable.setFieldValue("NativeType", blobGen.addBlob(data));

			tables[TableConstants.FieldMarshal].add(marTable);
		}

		// Make Constant DONE!
		byte[] value = field.getDefaultValue();
		if(value != null)
		{
			GenericTable constTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Constant]);

			byte type = field.getSignature().getType().getType();
			constTable.setFieldValue("Type", type & 0xFF);
			constTable.setFieldValue("Padding", 0);

			long coded = TableConstants.buildCodedIndex(TableConstants.HasConst, TableConstants.Field, field.getFieldRID());
			constTable.setFieldValue("Parent", coded);
			constTable.setFieldValue("Value", blobGen.addBlob(value));

			tables[TableConstants.Constant].add(constTable);
		}

		// Make FieldLayout DONE!
		if(field.getOffset() != -1L)
		{
			GenericTable layoutTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.FieldLayout]);
			layoutTable.setFieldValue("Offset", field.getOffset());
			layoutTable.setFieldValue("Field", field.getFieldRID());

			tables[TableConstants.FieldLayout].add(layoutTable);
		}

		// Make FieldRVA DONE
		if(field.getFieldRVA() != -1L)
		{
			GenericTable fieldRVATable = new GenericTable(TableConstants.GRAMMAR[TableConstants.FieldRVA]);
			fieldRVATable.setFieldValue("RVA", field.getFieldRVA());
			fieldRVATable.setFieldValue("Field", field.getFieldRID());

			tables[TableConstants.FieldRVA].add(fieldRVATable);
		}

		// Make CustomAttributes DONE!
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.Field, field.getFieldRID());
		addCustomAttributes(field, codedparent);

		return field.getFieldRID();
	}

	private void addCustomAttributes(CustomAttributeOwner cas, long codedparent)
	{
		// Make CustomAttributes
		if(cas == null)
		{
			return;
		}

		for(CustomAttribute ca : cas.getCustomAttributes())
		{
			GenericTable caTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.CustomAttribute]);
			caTable.setFieldValue("Parent", codedparent);
			long cons = getMethodRefToken(ca.getConstructor());
			long type = 0;
			if(((cons >> 24) & 0xFF) == TableConstants.Method)
			{
				type = TableConstants.buildCodedIndex(TableConstants.CustomAttributeType, TableConstants.Method, (cons & 0xFFFFFFL));
			}
			else
			{
				type = TableConstants.buildCodedIndex(TableConstants.CustomAttributeType, TableConstants.MemberRef, (cons & 0xFFFFFFL));
			}
			caTable.setFieldValue("Type", type);
			caTable.setFieldValue("Value", blobGen.addBlob(ca.getSignature()));

			if(!tables[TableConstants.CustomAttribute].contains(caTable))
			{
				tables[TableConstants.CustomAttribute].add(caTable);
			}
		}
	}

	private long addAssemblyRef(AssemblyRefInfo info)
	{
		GenericTable assRefTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.AssemblyRef]);

		assRefTable.setFieldValue("MajorVersion", info.getMajorVersion());
		assRefTable.setFieldValue("MinorVersion", info.getMinorVersion());
		assRefTable.setFieldValue("BuildNumber", info.getBuildNumber());
		assRefTable.setFieldValue("RevisionNumber", info.getRevisionNumber());
		assRefTable.setFieldValue("Flags", info.getFlags());
		assRefTable.setFieldValue("PublicKeyOrToken", blobGen.addBlob(info.getPublicKeyOrToken()));
		assRefTable.setFieldValue("Name", stringsGen.addString(info.getName()));
		assRefTable.setFieldValue("Culture", stringsGen.addString(info.getCulture()));
		assRefTable.setFieldValue("HashValue", blobGen.addBlob(info.getHashValue()));

		int index = tables[TableConstants.AssemblyRef].indexOf(assRefTable);
		if(index == -1)
		{
			tables[TableConstants.AssemblyRef].add(assRefTable);
			index = tables[TableConstants.AssemblyRef].size();
		}
		else
		{
			index++;
		}

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.AssemblyRef, (long) index);
		addCustomAttributes(info, codedparent);

		return (long) index;
	}

	private long addModuleRef(ModuleRefInfo info)
	{
		GenericTable modRefTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.ModuleRef]);

		modRefTable.setFieldValue("Name", stringsGen.addString(info.getModuleName()));

		int index = tables[TableConstants.ModuleRef].indexOf(modRefTable);
		if(index == -1)
		{
			tables[TableConstants.ModuleRef].add(modRefTable);
			index = tables[TableConstants.ModuleRef].size();
		}
		else
		{
			index++;
		}

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.ModuleRef, (long) index);
		addCustomAttributes(info, codedparent);

		return (long) index;
	}

	private long addMethod(MethodDef method)
	{
		// DONE!
		if(method.getMethodRID() != -1L)
		{
			return method.getMethodRID();
		}

		GenericTable methodTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Method]);

		method.setMethodRID(MethodCount++);
		tables[TableConstants.Method].add(methodTable);

		if(method.getMethodRVA() == -1L)
		{
			methodTable.setFieldValue("RVA", (long) 0);
		}
		else
		{
			methodTable.setFieldValue("RVA", method.getMethodRVA());
		}
		methodTable.setFieldValue("ImplFlags", method.getImplFlags());
		methodTable.setFieldValue("Flags", method.getFlags());
		methodTable.setFieldValue("Name", stringsGen.addString(method.getName()));

		ByteBuffer sigbuffer = new ByteBuffer(100);
		method.getSignature().emit(sigbuffer, this);
		byte[] blob = sigbuffer.toByteArray();
		methodTable.setFieldValue("Signature", blobGen.addBlob(blob));

		ParameterSignature[] parameters = method.getSignature().getParameters();
		if(parameters.length == 0)
		{
			methodTable.setFieldValue("ParamList", ParamCount);
		}
		else
		{
			long paramStart = ParamCount;
			if(method.getSignature().getReturnType().getParameterInfo() != null)
			{
				addParam(method.getSignature().getReturnType().getParameterInfo(), 0, method.getSignature().getReturnType().getType().getType());
			}
			for(int i = 0; i < parameters.length; i++)
			{
				addParam(parameters[i].getParameterInfo(), i + 1, parameters[i].getType().getType());
			}
			methodTable.setFieldValue("ParamList", paramStart);
		}

		// Make DeclSecurity
		DeclSecurity decl = method.getDeclSecurity();
		if(decl != null)
		{
			GenericTable declTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.DeclSecurity]);
			declTable.setFieldValue("Action", decl.getAction());
			long coded = TableConstants.buildCodedIndex(TableConstants.HasDeclSecurity, TableConstants.Method, method.getMethodRID());
			declTable.setFieldValue("Parent", coded);
			declTable.setFieldValue("PermissionSet", blobGen.addBlob(decl.getPermissionSet()));

			tables[TableConstants.DeclSecurity].add(declTable);

			// Make CustomAttribute (on DeclSecurity)
			long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.DeclSecurity, tables[TableConstants.DeclSecurity].size());
			addCustomAttributes(decl, codedparent);
		}

		// Make MethodSemantics
		MethodSemantics sem = method.getMethodSemantics();
		if(sem != null)
		{
			GenericTable semTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.MethodSemantics]);
			semTable.setFieldValue("Semantics", sem.getSemantics());
			semTable.setFieldValue("Method", method.getMethodRID());
			if(sem.getEvent() != null)
			{
				long coded = TableConstants.buildCodedIndex(TableConstants.HasSemantics, TableConstants.Event, sem.getEvent().getEventRID());
				semTable.setFieldValue("Association", coded);
			}
			else
			{
				long coded = TableConstants.buildCodedIndex(TableConstants.HasSemantics, TableConstants.Property, sem.getProperty().getPropertyRID());
				semTable.setFieldValue("Association", coded);
			}
			tables[TableConstants.MethodSemantics].add(semTable);
		}

		// Make ImplMap
		ImplementationMap map = method.getImplementationMap();
		if(map != null)
		{
			GenericTable mapTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.ImplMap]);
			mapTable.setFieldValue("MappingFlags", map.getFlags());

			long coded = TableConstants.buildCodedIndex(TableConstants.MemberForwarded, TableConstants.Method, method.getMethodRID());
			mapTable.setFieldValue("MemberForwarded", coded);
			mapTable.setFieldValue("ImportName", stringsGen.addString(map.getImportName()));

			long moduleRef = addModuleRef(map.getImportScope());
			mapTable.setFieldValue("ImportScope", moduleRef);

			tables[TableConstants.ImplMap].add(mapTable);
		}

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.Method, method.getMethodRID());
		addCustomAttributes(method, codedparent);

		return method.getMethodRID();
	}

	private void buildMethodBodies(TypeDef def)
	{
		// Make method body
		List<MethodDef> methodlist = def.getMethods();

		for(MethodDef aMethodlist : methodlist)
		{
			MethodBody body = aMethodlist.getMethodBody();
			if(body != null)
			{
				GenericTable methodTable = tables[TableConstants.Method].get((int) aMethodlist.getMethodRID() - 1);

				ByteBuffer bodybuffer = new ByteBuffer(1000);
				body.emit(bodybuffer, this);
				methodBodies.add(bodybuffer);
				methodTable.setFieldValue("RVA", (long) methodBodies.size());
				// 'RVA' gets assigned a 1-based index into the methodBodies vector corresponding to its body
			}
		}
	}


	private void buildMethodImpls(TypeDef def)
	{
		// DONE!
		MethodMap[] maps = def.getMethodMaps();

		for(MethodMap map : maps)
		{
			GenericTable mapTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.MethodImpl]);
			mapTable.setFieldValue("Class", def.getTypeDefRID());

			MethodDefOrRef body = map.getMethodBody();
			MethodDefOrRef decl = map.getMethodDeclaration();

			// DONE!
			if(body instanceof MethodDef)
			{
				MethodDef method = (MethodDef) body;
				long coded = TableConstants.buildCodedIndex(TableConstants.MethodDefOrRef, TableConstants.Method, method.getMethodRID());
				mapTable.setFieldValue("MethodBody", coded);
			}
			else
			{
				long rid = addMemberRef(body);
				long coded = TableConstants.buildCodedIndex(TableConstants.MethodDefOrRef, TableConstants.MemberRef, rid);
				mapTable.setFieldValue("MethodBody", coded);
			}

			// DONE!
			if(decl instanceof MethodDef)
			{
				MethodDef method = (MethodDef) decl;
				long coded = TableConstants.buildCodedIndex(TableConstants.MethodDefOrRef, TableConstants.Method, method.getMethodRID());
				mapTable.setFieldValue("MethodDeclaration", coded);
			}
			else
			{
				long rid = addMemberRef(decl);
				long coded = TableConstants.buildCodedIndex(TableConstants.MethodDefOrRef, TableConstants.MemberRef, rid);
				mapTable.setFieldValue("MethodDeclaration", coded);
			}

			tables[TableConstants.MethodImpl].add(mapTable);
		}
	}

	private long addParam(ParameterInfo parameter, int seq, byte type)
	{
		// DONE!
		if(parameter.getParamRID() != -1L)
		{
			return parameter.getParamRID();
		}

		GenericTable paramTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Param]);

		parameter.setParamRID(ParamCount++);
		tables[TableConstants.Param].add(paramTable);

		paramTable.setFieldValue("Flags", parameter.getFlags());
		paramTable.setFieldValue("Sequence", seq);
		paramTable.setFieldValue("Name", stringsGen.addString(parameter.getName()));

		// Make FieldMarshal
		MarshalSignature sig = parameter.getFieldMarshal();
		if(sig != null)
		{
			GenericTable marshalTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.FieldMarshal]);
			long coded = TableConstants.buildCodedIndex(TableConstants.HasFieldMarshal, TableConstants.Param, parameter.getParamRID());
			marshalTable.setFieldValue("Parent", coded);

			ByteBuffer marbuffer = new ByteBuffer(100);
			sig.emit(marbuffer, this);
			byte[] blob = marbuffer.toByteArray();
			marshalTable.setFieldValue("NativeType", blobGen.addBlob(blob));
			tables[TableConstants.FieldMarshal].add(marshalTable);
		}

		// Make Constant
		byte[] value = parameter.getDefaultValue();
		if(value != null)
		{
			GenericTable constTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Constant]);
			constTable.setFieldValue("Type", type & 0xFF);
			constTable.setFieldValue("Padding", 0);
			long coded = TableConstants.buildCodedIndex(TableConstants.HasConst, TableConstants.Param, parameter.getParamRID());
			constTable.setFieldValue("Parent", coded);
			constTable.setFieldValue("Value", blobGen.addBlob(value));
			tables[TableConstants.Constant].add(constTable);
		}

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.Param, parameter.getParamRID());
		addCustomAttributes(parameter, codedparent);

		return parameter.getParamRID();
	}

	private void buildInterfaceImpls(TypeDef def)
	{
		for(InterfaceImplementation anInterface : def.getInterfaceImplementations())
		{
			GenericTable implTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.InterfaceImpl]);

			implTable.setFieldValue("Class", def.getTypeDefRID());

			Object typeInfo = anInterface.getInterface();
			if(typeInfo instanceof TypeDef)
			{
				TypeDef interdef = (TypeDef) typeInfo;
				long coded = TableConstants.buildCodedIndex(TableConstants.TypeDefOrRefOrSpec, TableConstants.TypeDef, interdef.getTypeDefRID());
				implTable.setFieldValue("Interface", coded);
			}
			else if(typeInfo instanceof TypeRef)
			{
				long rid = addTypeRef((TypeRef) typeInfo);
				long coded = TableConstants.buildCodedIndex(TableConstants.TypeDefOrRefOrSpec, TableConstants.TypeRef, rid);
				implTable.setFieldValue("Interface", coded);
			}
			else if(typeInfo instanceof TypeSpec)
			{
				long rid = addTypeSpec((TypeSpec) typeInfo);
				long coded = TableConstants.buildCodedIndex(TableConstants.TypeDefOrRefOrSpec, TableConstants.TypeRef, rid);
				implTable.setFieldValue("Interface", coded);
			}

			tables[TableConstants.InterfaceImpl].add(implTable);

			// Make CustomAttributes
			long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.InterfaceImpl,
					tables[TableConstants.InterfaceImpl].size());
			addCustomAttributes(anInterface, codedparent);
		}
	}

	private void buildProperties(TypeDef def)
	{
		List<Property> props = def.getProperties();
		if(props.isEmpty())
		{
			return;
		}

		GenericTable mapTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.PropertyMap]);
		mapTable.setFieldValue("Parent", def.getTypeDefRID());
		mapTable.setFieldValue("PropertyList", PropertyCount);
		tables[TableConstants.PropertyMap].add(mapTable);

		for(Property prop : props)
		{
			GenericTable propTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Property]);
			prop.setPropertyRID(PropertyCount++);
			tables[TableConstants.Property].add(propTable);

			propTable.setFieldValue("Flags", prop.getFlags());
			propTable.setFieldValue("Name", stringsGen.addString(prop.getName()));
			ByteBuffer propBuffer = new ByteBuffer(100);
			prop.getSignature().emit(propBuffer, this);
			byte[] blob = propBuffer.toByteArray();
			propTable.setFieldValue("Type", blobGen.addBlob(blob));

			// Make Constant
			byte[] value = prop.getDefaultValue();
			if(value != null)
			{
				GenericTable constTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Constant]);
				constTable.setFieldValue("Type", prop.getSignature().getType().getType() & 0xFF);
				long coded = TableConstants.buildCodedIndex(TableConstants.HasConst, TableConstants.Property, prop.getPropertyRID());
				constTable.setFieldValue("Parent", coded);
				constTable.setFieldValue("Value", blobGen.addBlob(value));

				tables[TableConstants.Constant].add(constTable);
			}

			// Make CustomAttributes
			long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.Property, prop.getPropertyRID());
			addCustomAttributes(prop, codedparent);
		}
	}

	private void buildEvents(TypeDef def)
	{
		// DONE!
		List<Event> eventlist = def.getEvents();
		if(eventlist.isEmpty())
		{
			return;
		}

		GenericTable mapTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.EventMap]);
		mapTable.setFieldValue("Parent", def.getTypeDefRID());
		mapTable.setFieldValue("EventList", EventCount);
		tables[TableConstants.EventMap].add(mapTable);

		for(Event anEventlist : eventlist)
		{
			GenericTable eventTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.Event]);
			anEventlist.setEventRID(EventCount++);
			tables[TableConstants.Event].add(eventTable);

			eventTable.setFieldValue("EventFlags", anEventlist.getEventFlags());
			eventTable.setFieldValue("Name", stringsGen.addString(anEventlist.getName()));
			Object ref = anEventlist.getEventType();
			if(ref instanceof TypeDef)
			{
				TypeDef newdef = (TypeDef) ref;
				long coded = TableConstants.buildCodedIndex(TableConstants.TypeDefOrRefOrSpec, TableConstants.TypeDef, newdef.getTypeDefRID());
				eventTable.setFieldValue("EventType", coded);
			}
			else if(ref instanceof TypeRef)
			{
				long refrid = addTypeRef((TypeRef) ref);
				long coded = TableConstants.buildCodedIndex(TableConstants.TypeDefOrRefOrSpec, TableConstants.TypeRef, refrid);
				eventTable.setFieldValue("EventType", coded);
			}

			// Make CustomAttribute
			long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.Event, anEventlist.getEventRID());
			addCustomAttributes(anEventlist, codedparent);
		}
	}

	private long addTypeSpec(TypeSpec spec)
	{
		// DONE!
		if(spec.getTypeSpecRID() != -1L)
		{
			return spec.getTypeSpecRID();
		}

		GenericTable specTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.TypeSpec]);
		spec.setTypeSpecRID(TypeSpecCount++);
		tables[TableConstants.TypeSpec].add(specTable);

		ByteBuffer specBuffer = new ByteBuffer(100);
		spec.getSignature().emit(specBuffer, this);
		byte[] blob = specBuffer.toByteArray();
		specTable.setFieldValue("Signature", blobGen.addBlob(blob));

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.TypeSpec, spec.getTypeSpecRID());
		addCustomAttributes(spec, codedparent);

		return spec.getTypeSpecRID();
	}

	private long addTypeRef(TypeRef ref)
	{
		if(ref.getTypeRefRID() != -1L)
		{
			return ref.getTypeRefRID();
		}

		GenericTable refTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.TypeRef]);
		ref.setTypeRefRID(TypeRefCount++);
		tables[TableConstants.TypeRef].add(refTable);

		refTable.setFieldValue("Name", stringsGen.addString(ref.getName()));
		refTable.setFieldValue("Namespace", stringsGen.addString(ref.getNamespace()));
		if(ref instanceof TypeDef)
		{
			// DONE!
			TypeDef def = (TypeDef) ref;
			long coded = TableConstants.buildCodedIndex(TableConstants.ResolutionScope, TableConstants.Module, 1L);
			refTable.setFieldValue("ResolutionScope", coded);
		}
		else if(ref instanceof ModuleTypeRef)
		{
			// DONE!
			ModuleTypeRef mref = (ModuleTypeRef) ref;

			long moduleRef = addModuleRef(mref.getModuleRefInfo());
			long coded = TableConstants.buildCodedIndex(TableConstants.ResolutionScope, TableConstants.ModuleRef, moduleRef);
			refTable.setFieldValue("ResolutionScope", coded);
		}
		else if(ref instanceof AssemblyTypeRef)
		{
			// DONE!
			AssemblyTypeRef aref = (AssemblyTypeRef) ref;

			long assemblyRef = addAssemblyRef(aref.getAssemblyRefInfo());

			long coded = TableConstants.buildCodedIndex(TableConstants.ResolutionScope, TableConstants.AssemblyRef, assemblyRef);
			refTable.setFieldValue("ResolutionScope", coded);
		}
		else if(ref instanceof NestedTypeRef)
		{
			// DONE!!
			NestedTypeRef nref = (NestedTypeRef) ref;

			long refRID = addTypeRef(nref.getEnclosingTypeRef());
			long coded = TableConstants.buildCodedIndex(TableConstants.ResolutionScope, TableConstants.TypeRef, refRID);
			refTable.setFieldValue("ResolutionScope", coded);
		}
		else if(ref instanceof ExportedTypeRef)
		{
			// DONE!!
			ExportedTypeRef eref = (ExportedTypeRef) ref;

			addExportedType(eref);
			refTable.setFieldValue("ResolutionScope", (long) 0);
		}

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.TypeRef, ref.getTypeRefRID());
		addCustomAttributes(ref, codedparent);

		return ref.getTypeRefRID();
	}

	private long addMemberRef(MemberRef ref)
	{
		// DONE!
		// adds this as a MemberRef, not as a Field or Method!
		if(ref.getMemberRefRID() != -1L)
		{
			return ref.getMemberRefRID();
		}

		if((ref instanceof Field) || (ref instanceof MethodDef))
		{
			return 0L;
		}

		GenericTable refTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.MemberRef]);
		tables[TableConstants.MemberRef].add(refTable);
		ref.setMemberRefRID(MemberRefCount++);

		refTable.setFieldValue("Name", stringsGen.addString(ref.getName()));

		if(ref instanceof FieldRef)
		{
			// DONE!
			if(ref instanceof GlobalFieldRef)
			{
				// DONE!
				GlobalFieldRef fref = (GlobalFieldRef) ref;

				long moduleRef = addModuleRef(fref.getModuleRefInfo());
				long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.ModuleRef, moduleRef);
				refTable.setFieldValue("Class", coded);

				ByteBuffer refbuffer = new ByteBuffer(100);
				fref.getSignature().emit(refbuffer, this);
				byte[] blob = refbuffer.toByteArray();
				refTable.setFieldValue("Signature", blobGen.addBlob(blob));
			}
			else
			{
				// DONE!
				// normal FieldRef
				FieldRef fref = (FieldRef) ref;
				AbstractTypeReference parent = fref.getParent();
				if(parent instanceof TypeDef)
				{
					TypeDef parentdef = (TypeDef) parent;

					long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.TypeDef, parentdef.getTypeDefRID());
					refTable.setFieldValue("Class", coded);
				}
				else if(parent instanceof TypeRef)
				{
					TypeRef parentref = (TypeRef) parent;

					long rid = addTypeRef(parentref);
					long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.TypeRef, rid);
					refTable.setFieldValue("Class", coded);
				}
				else if(parent instanceof TypeSpec)
				{
					TypeSpec parentspec = (TypeSpec) parent;

					long rid = addTypeSpec(parentspec);
					long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.TypeSpec, rid);
					refTable.setFieldValue("Class", coded);
				}

				ByteBuffer refbuffer = new ByteBuffer(100);
				fref.getSignature().emit(refbuffer, this);
				byte[] blob = refbuffer.toByteArray();
				refTable.setFieldValue("Signature", blobGen.addBlob(blob));
			}


		}
		else if(ref instanceof MethodDefOrRef)
		{
			if(ref instanceof GlobalMethodRef)
			{
				// DONE!
				GlobalMethodRef gref = (GlobalMethodRef) ref;

				long moduleRef = addModuleRef(gref.getModuleRefInfo());
				long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.ModuleRef, moduleRef);
				refTable.setFieldValue("Class", coded);

				ByteBuffer sigbuffer = new ByteBuffer(100);
				gref.getCallsiteSignature().emit(sigbuffer, this);
				byte[] blob = sigbuffer.toByteArray();
				refTable.setFieldValue("Signature", blobGen.addBlob(blob));
			}
			else if(ref instanceof VarargsMethodRef)
			{
				// DONE!
				VarargsMethodRef vref = (VarargsMethodRef) ref;

				long rid = vref.getMethod().getMethodRID();
				long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.Method, rid);
				refTable.setFieldValue("Class", coded);

				ByteBuffer sigbuffer = new ByteBuffer(100);
				vref.getCallsiteSignature().emit(sigbuffer, this);
				byte[] blob = sigbuffer.toByteArray();
				refTable.setFieldValue("Signature", blobGen.addBlob(blob));
			}
			else
			{
				// DONE!
				// normal MethodRef
				MethodRef mref = (MethodRef) ref;
				AbstractTypeReference parent = mref.getParent();

				if(parent instanceof TypeDef)
				{
					TypeDef parentdef = (TypeDef) parent;

					long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.TypeDef, parentdef.getTypeDefRID());
					refTable.setFieldValue("Class", coded);
				}
				else if(parent instanceof TypeRef)
				{
					TypeRef parentref = (TypeRef) parent;

					long rid = addTypeRef(parentref);
					long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.TypeRef, rid);
					refTable.setFieldValue("Class", coded);
				}
				else if(parent instanceof TypeSpec)
				{
					TypeSpec parentspec = (TypeSpec) parent;

					long rid = addTypeSpec(parentspec);
					long coded = TableConstants.buildCodedIndex(TableConstants.MemberRefParent, TableConstants.TypeSpec, rid);
					refTable.setFieldValue("Class", coded);
				}

				ByteBuffer sigbuffer = new ByteBuffer(100);
				mref.getCallsiteSignature().emit(sigbuffer, this);
				byte[] blob = sigbuffer.toByteArray();
				refTable.setFieldValue("Signature", blobGen.addBlob(blob));
			}
		}

		// Make CustomAttribute
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.MemberRef, ref.getMemberRefRID());
		addCustomAttributes(ref, codedparent);

		return ref.getMemberRefRID();
	}

	private void buildEntryPointToken()
	{
		EntryPoint entryPoint = module.getEntryPoint();
		if(entryPoint != null)
		{
			if(entryPoint.getEntryPointFile() != null)
			{
				EntryPointToken = addFile(entryPoint.getEntryPointFile());
				EntryPointToken |= (long) (TableConstants.File << 24);
			}
			else
			{
				EntryPointToken = entryPoint.getEntryPointMethod().getMethodRID();
				EntryPointToken |= (long) (TableConstants.Method << 24);
			}
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a StandAloneSig token for the given method callsite signature
	 */
	public long getStandAloneSigToken(MethodSignature callsiteSig)
	{
		// to be called by calli instructions only (not LocalVarList in methodbodies)
		// returns a token, not an RID
		ByteBuffer sigbuffer = new ByteBuffer(100);
		callsiteSig.emit(sigbuffer, this);
		byte[] blob = sigbuffer.toByteArray();

		GenericTable sigTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.StandAloneSig]);
		sigTable.setFieldValue("Signature", blobGen.addBlob(blob));

		int index = tables[TableConstants.StandAloneSig].indexOf(sigTable);
		if(index == -1)
		{
			tables[TableConstants.StandAloneSig].add(sigTable);
			index = tables[TableConstants.StandAloneSig].size();
		}
		else
		{
			index++;
		}
		long token = (index | (TableConstants.StandAloneSig << 24)) & 0xFFFFFFFFL;

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.StandAloneSig, (long) index);
		addCustomAttributes(callsiteSig, codedparent);

		return token;
	}

	/**
	 * Returns a StandAloneSig token for the given local variable list
	 */
	public long getStandAloneSigToken(LocalVarList localVars)
	{
		// to be called by method body
		// returns a token, not an RID
		GenericTable sigTable = new GenericTable(TableConstants.GRAMMAR[TableConstants.StandAloneSig]);
		ByteBuffer sigbuffer = new ByteBuffer(100);
		localVars.emit(sigbuffer, this);
		byte[] blob = sigbuffer.toByteArray();
		sigTable.setFieldValue("Signature", blobGen.addBlob(blob));

		int index = tables[TableConstants.StandAloneSig].indexOf(sigTable);
		if(index == -1)
		{
			tables[TableConstants.StandAloneSig].add(sigTable);
			index = tables[TableConstants.StandAloneSig].size();
		}
		else
		{
			index++;
		}

		long token = ((long) index | (long) (TableConstants.StandAloneSig << 24));

		// Make CustomAttributes
		long codedparent = TableConstants.buildCodedIndex(TableConstants.HasCustomAttribute, TableConstants.StandAloneSig, (long) index);
		addCustomAttributes(localVars, codedparent);

		return token;
	}

	/**
	 * Returns the MethodRef or MethodDef token for the given method
	 */
	public long getMethodRefToken(MethodDefOrRef method)
	{
		if(method instanceof MethodDef)
		{
			MethodDef meth = (MethodDef) method;
			return meth.getMethodRID() | (long) (TableConstants.Method << 24);
		}
		else
		{
			long rid = addMemberRef(method);
			return (rid | (long) (TableConstants.MemberRef << 24));
		}
	}

	/**
	 * Returns the FieldRef or FieldDef token for the given field
	 */
	public long getFieldRefToken(FieldRef field)
	{
		if(field instanceof Field)
		{
			Field f = (Field) field;
			return (f.getFieldRID() | (long) (TableConstants.Field << 24));
		}
		else
		{
			long rid = addMemberRef(field);
			return (rid | (long) (TableConstants.MemberRef << 24));
		}
	}

	/**
	 * Returns the TypeDef, TypeRef, or TypeSpec token for the given type
	 */
	public long getTypeToken(AbstractTypeReference ref)
	{
		// DONE!
		if(ref instanceof TypeDef)
		{
			TypeDef def = (TypeDef) ref;
			long result = 0L;
			if(def.getTypeDefRID() != -1L)
			{
				result = def.getTypeDefRID();
				result |= (TableConstants.TypeDef << 24);
			}
			return result;
		}
		else if(ref instanceof TypeRef)
		{
			TypeRef typeref = (TypeRef) ref;
			long result = addTypeRef(typeref);
			result |= (TableConstants.TypeRef << 24);
			return result;
		}
		else if(ref instanceof TypeSpec)
		{
			TypeSpec spec = (TypeSpec) ref;
			long result = addTypeSpec(spec);
			result |= (TableConstants.TypeSpec << 24);
			return result;
		}
		return 0L;
	}

	/**
	 * Returns the correct token for the given loadable type (from ldtoken instruction)
	 */
	public long getLoadableTypeToken(LoadableType type)
	{
		if(type instanceof AbstractTypeReference)
		{
			if(type instanceof TypeDef)
			{
				TypeDef def = (TypeDef) type;
				return (def.getTypeDefRID() | (long) (TableConstants.TypeDef << 24));
			}
			else if(type instanceof TypeSpec)
			{
				TypeSpec spec = (TypeSpec) type;
				long rid = addTypeSpec(spec);
				return (rid | (long) (TableConstants.TypeSpec << 24));
			}
			else
			{
				// TypeRef
				TypeRef ref = (TypeRef) type;
				long rid = addTypeRef(ref);
				return (rid | (long) (TableConstants.TypeRef << 24));
			}
		}
		else
		{// MemberRef
			if(type instanceof MethodDefOrRef)
			{
				// MethodDefOrRef
				MethodDefOrRef method = (MethodDefOrRef) type;
				return getMethodRefToken(method);
			}
			else
			{
				// FieldRef
				FieldRef field = (FieldRef) type;
				return getFieldRefToken(field);
			}
		}
	}

	/**
	 * Returns the #US stream token for the given user string (called by ldstr instructions)
	 */
	public long getUserStringToken(String str)
	{
		long token = usGen.addUserString(str);
		return (token | (long) (TableConstants.USString << 24));
	}

	/**
	 * Returns the list of metadata tables, indexed by token type number
	 */
	public List<GenericTable>[] getTables()
	{
		return tables;
	}

	/**
	 * Returns the StringsStreamGen made by this ClassEmitter
	 */
	public StringsStreamGen getStringsStreamGen()
	{
		return stringsGen;
	}

	/**
	 * Returns the BlobStreamGen made byt this ClassEmitter
	 */
	public BlobStreamGen getBlobStreamGen()
	{
		return blobGen;
	}

	/**
	 * Returns the GUIDStreamGen made by this ClassEmitter
	 */
	public GUIDStreamGen getGUIDStreamGen()
	{
		return guidGen;
	}

	/**
	 * Returns the USStreamGen made by this ClassEmitter
	 */
	public USStreamGen getUSStreamGen()
	{
		return usGen;
	}

	/**
	 * Returns a vector of ByteBuffers containing the method bodies defined in this Module
	 */
	public List<ByteBuffer> getMethodBodies()
	{
		return methodBodies;
	}

	/**
	 * Returns a ByteBuffer with all the managed resource data to be embedded in this file.
	 */
	public ByteBuffer getLocalResources()
	{
		return localResources;
	}

	/**
	 * Writes a Strings token to a buffer, in the appropriate size
	 *
	 * @param buffer the buffer to write to
	 * @param token  a Number containing the strings token (will be a Long)
	 */
	public void putStringsToken(ByteBuffer buffer, Number token)
	{
		if(stringsGen.getLength() >= 65536)
		{
			buffer.putDWORD(token.longValue());
		}
		else
		{
			buffer.putWORD(token.intValue());
		}
	}

	/**
	 * Writes a blob token to a buffer, in the appropriate size
	 *
	 * @param buffer the buffer to write to
	 * @param token  a Number containing the blob token (will be a Long)
	 */
	public void putBlobToken(ByteBuffer buffer, Number token)
	{
		if(blobGen.getLength() >= 65536)
		{
			buffer.putDWORD(token.longValue());
		}
		else
		{
			buffer.putWORD(token.intValue());
		}
	}

	/**
	 * Writes a GUID token to a buffer, in the appropriate size
	 *
	 * @param buffer the buffer to write to
	 * @param token  a Number containing the GUID token (will be a Long)
	 */
	public void putGUIDToken(ByteBuffer buffer, Number token)
	{
		if(guidGen.getNumGUIDS() >= 65536)
		{
			buffer.putDWORD(token.longValue());
		}
		else
		{
			buffer.putWORD(token.intValue());
		}
	}

	/**
	 * Writes a table index to a buffer, in the appropriate size
	 *
	 * @param buffer the buffer to write to
	 * @param type   the token type of this token
	 * @param rid    a Number containing the table index (will be a Long)
	 */
	public void putTableIndex(ByteBuffer buffer, int type, Number rid)
	{
		if(tables[type].size() >= 65536)
		{
			buffer.putDWORD(rid.longValue());
		}
		else
		{
			buffer.putWORD(rid.intValue());
		}
	}

	/**
	 * Writes a coded index to a buffer, in the appropriate size
	 *
	 * @param buffer the buffer to write to
	 * @param type   the coded index type
	 * @param coded  a Number containing the coded index (will be a Long)
	 */
	public void putCodedIndex(ByteBuffer buffer, int type, Number coded)
	{
		int max = 0;

		for(int i = 0; i < TableConstants.TABLE_OPTIONS[type].length; i++)
		{
			if(0 <= TableConstants.TABLE_OPTIONS[type][i] && TableConstants.TABLE_OPTIONS[type][i] < 64)
			{
				max = Math.max(max, tables[TableConstants.TABLE_OPTIONS[type][i]].size());
			}
		}

		if((max << TableConstants.BITS[type]) >= 65536)
		{
			buffer.putDWORD(coded.longValue() & 0xFFFFFFFFL);
		}
		else
		{
			buffer.putWORD(coded.intValue() & 0xFFFF);
		}
	}

	/**
	 * Returns the entrypoint token for this module
	 */
	public long getEntryPointToken()
	{
		return EntryPointToken;
	}

	/**
	 * Returns the list of patches generated for the .net section
	 */
	public PatchList getNetPatches()
	{
		return netPatches;
	}
}
