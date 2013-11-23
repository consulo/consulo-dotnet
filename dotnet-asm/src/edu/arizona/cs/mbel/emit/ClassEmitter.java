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

/**
 * This class is used to take a Module and output the CLI Header and metadata portion of a
 * .NET module to a buffer. This class is used by Emitter to write out the .NET specific parts
 * of the PE/COFF file.
 *
 * @author Michael Stepp
 */
public class ClassEmitter
{
	private edu.arizona.cs.mbel.mbel.Module module;
	private StringsStreamGen stringsGen;
	private BlobStreamGen blobGen;
	private GUIDStreamGen guidGen;
	private USStreamGen usGen;
	private java.util.Vector[] tables;
	private edu.arizona.cs.mbel.metadata.TableConstants tc = null;
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
	private edu.arizona.cs.mbel.ByteBuffer localResources;   // ByteBuffer
	private java.util.Vector methodBodies;                   // vector of ByteBuffers
	private PatchList netPatches;

	/**
	 * Creates and initializes a ClassEmitter.
	 * Initializes the table arrays, streamgens, and buffers for local resources and method bodies.
	 */
	public ClassEmitter(edu.arizona.cs.mbel.mbel.Module mod)
	{
		module = mod;

		stringsGen = new StringsStreamGen();
		blobGen = new BlobStreamGen();
		stringsGen = new StringsStreamGen();
		blobGen = new BlobStreamGen();
		guidGen = new GUIDStreamGen();
		usGen = new USStreamGen();
		tables = new java.util.Vector[64];

		for(int i = 0; i < 64; i++)
		{
			tables[i] = new java.util.Vector(10);
		}
		////////////////////////////////////////////////

		localResources = new edu.arizona.cs.mbel.ByteBuffer(2000);
		methodBodies = new java.util.Vector(10);
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
			edu.arizona.cs.mbel.mbel.FileReference[] frs = module.getFileReferences();
			for(int i = 0; i < frs.length; i++)
			{
				addFile(frs[i]);
			}
		}
		buildManifestResources();

		{// add TypeDef tables and sub-tables
			edu.arizona.cs.mbel.mbel.TypeDef[] defs = module.getTypeDefs();
			for(int i = 0; i < defs.length; i++)
			{
				addTypeDef(defs[i]);
			}
			for(int i = 0; i < defs.length; i++)
			{
				buildEvents(defs[i]);
			}
			for(int i = 0; i < defs.length; i++)
			{
				buildProperties(defs[i]);
			}
			for(int i = 0; i < defs.length; i++)
			{
				buildFields(defs[i]);
			}
			for(int i = 0; i < defs.length; i++)
			{
				buildMethods(defs[i]);
			}
			for(int i = 0; i < defs.length; i++)
			{
				buildMethodBodies(defs[i]);
			}
			for(int i = 0; i < defs.length; i++)
			{
				buildMethodImpls(defs[i]);
			}
			for(int i = 0; i < defs.length; i++)
			{
				buildInterfaceImpls(defs[i]);
			}
		}

		buildEntryPointToken();
	}

	/**
	 * Writes out the CLI header and metadata to the given buffer.
	 * Not to be called until after buildTables.
	 *
	 * @param netBuffer a buffer corresponding to the .net section in a PE file, assumed to be at position 0
	 */
	public void emitMetadata(edu.arizona.cs.mbel.ByteBuffer netBuffer)
	{
		// netBuffer will be read-only
		// not to be called until after buildTables

		netPatches = new PatchList();

		edu.arizona.cs.mbel.metadata.Metadata metadata = module.getPEModule().metadata;
		edu.arizona.cs.mbel.parse.CLIHeader cliHeader = module.getPEModule().cliHeader;

		////// emit CLR Header /////////////////////////////
		netBuffer.pad(4);
		netBuffer.putDWORD(72);
		netBuffer.putWORD(cliHeader.MajorRuntimeVersion);
		netBuffer.putWORD(cliHeader.MinorRuntimeVersion);
		netPatches.addPatch(netBuffer.getPosition());
		long metadataRVAStart = netBuffer.getPosition();
		netBuffer.putDWORD(0);  // wrong value (Metadata RVA)
		netBuffer.putDWORD(0);  // wrong value (Metadata size (includes all streams/tables))
		netBuffer.putDWORD(cliHeader.Flags & (~cliHeader.COMIMAGE_FLAGS_STRONGNAMESIGNED) & (~cliHeader.COMIMAGE_FLAGS_TRACKDEBUGDATA));
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


		long[] bodyRVAStarts = new long[tables[tc.Method].size()];
		for(int i = 0; i < 64; i++)
		{
			if(tables[i].size() > 0)
			{
				if(i == tc.Method)
				{
					for(int j = 0; j < tables[i].size(); j++)
					{
						edu.arizona.cs.mbel.metadata.GenericTable methodTable = (edu.arizona.cs.mbel.metadata.GenericTable) tables[i].get(j);
						long RVA = methodTable.getConstant("RVA").longValue();
						int Flags = methodTable.getConstant("ImplFlags").intValue();

						if((RVA != 0) && (Flags & edu.arizona.cs.mbel.mbel.Method.CodeTypeMask) == edu.arizona.cs.mbel.mbel.Method.IL)
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
						((edu.arizona.cs.mbel.metadata.GenericTable) tables[i].get(j)).emit(netBuffer, this);
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
			edu.arizona.cs.mbel.ByteBuffer bodybuf = (edu.arizona.cs.mbel.ByteBuffer) methodBodies.get(i);
			netBuffer.pad(4);
			bodyStarts[i] = netBuffer.getPosition();
			netBuffer.concat(bodybuf);
		}
		// set RVAs (unbiased)
		end = netBuffer.getPosition();
		int count = 0;
		for(int i = 0; i < bodyRVAStarts.length; i++)
		{
			if(bodyRVAStarts[i] != -1L)
			{
				netBuffer.setPosition((int) bodyRVAStarts[i]);
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
		edu.arizona.cs.mbel.mbel.AssemblyInfo assemblyInfo = module.getAssemblyInfo();
		if(assemblyInfo != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable assemTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Assembly]);
			tables[tc.Assembly].add(assemTable);

			assemTable.setFieldValue("HashAlgID", new Long(assemblyInfo.getHashAlg()));
			assemTable.setFieldValue("MajorVersion", new Integer(assemblyInfo.getMajorVersion()));
			assemTable.setFieldValue("MinorVersion", new Integer(assemblyInfo.getMinorVersion()));
			assemTable.setFieldValue("BuildNumber", new Integer(assemblyInfo.getBuildNumber()));
			assemTable.setFieldValue("RevisionNumber", new Integer(assemblyInfo.getRevisionNumber() + 1));
			assemTable.setFieldValue("Flags", new Long(assemblyInfo.getFlags()));
			assemTable.setFieldValue("PublicKey", new Long(blobGen.addBlob(assemblyInfo.getPublicKey())));
			assemTable.setFieldValue("Name", new Long(stringsGen.addString(assemblyInfo.getName())));
			assemTable.setFieldValue("Culture", new Long(stringsGen.addString(assemblyInfo.getCulture())));

			// Make DeclSecurity
			edu.arizona.cs.mbel.mbel.DeclSecurity decl = assemblyInfo.getDeclSecurity();
			if(decl != null)
			{
				edu.arizona.cs.mbel.metadata.GenericTable declTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.DeclSecurity]);
				declTable.setFieldValue("Action", new Integer(decl.getAction()));
				long coded = tc.buildCodedIndex(tc.HasDeclSecurity, tc.Assembly, 1L);
				declTable.setFieldValue("Parent", new Long(coded));
				declTable.setFieldValue("PermissionSet", new Long(blobGen.addBlob(decl.getPermissionSet())));

				tables[tc.DeclSecurity].add(declTable);

				// Make CustomAttributes (on DeclSecurity)
				long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.DeclSecurity, tables[tc.DeclSecurity].size());
				addCustomAttributes(decl.getDeclSecurityAttributes(), codedparent);
			}

			// Make ExportedTypes
			edu.arizona.cs.mbel.mbel.ExportedTypeRef[] erefs = assemblyInfo.getExportedTypes();
			for(int i = 0; i < erefs.length; i++)
			{
				addExportedType(erefs[i]);
			}

			// Make CustomAttributes
			long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.Assembly, 1L);
			addCustomAttributes(assemblyInfo.getAssemblyAttributes(), codedparent);
		}
	}

	private void buildModule()
	{
		// DONE!
		edu.arizona.cs.mbel.metadata.GenericTable modTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Module]);
		modTable.setFieldValue("Generation", new Integer(module.getGeneration()));
		modTable.setFieldValue("Name", new Long(stringsGen.addString(module.getName())));
		modTable.setFieldValue("Mvid", new Long(guidGen.addGUID(module.getMvidGUID())));
		modTable.setFieldValue("EncID", new Long(guidGen.addGUID(module.getEncIdGUID())));
		modTable.setFieldValue("EncBaseID", new Long(guidGen.addGUID(module.getEncBaseIdGUID())));

		tables[tc.Module].add(modTable);

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.Module, 1L);
		addCustomAttributes(module.getModuleAttributes(), codedparent);
	}

	private long addFile(edu.arizona.cs.mbel.mbel.FileReference fr)
	{
		// returns a valid RID (1-based) DONE!!
		long index = fr.getFileRID();
		if(index == -1L)
		{
			edu.arizona.cs.mbel.metadata.GenericTable fileTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.File]);

			fr.setFileRID(FileCount++);
			tables[tc.File].add(fileTable);

			fileTable.setFieldValue("Flags", new Long(fr.getFlags()));
			fileTable.setFieldValue("Name", new Long(stringsGen.addString(fr.getFileName())));
			fileTable.setFieldValue("HashValue", new Long(blobGen.addBlob(fr.getHashValue())));
			index = fr.getFileRID();
		}

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.File, index);
		addCustomAttributes(fr.getFileAttributes(), codedparent);

		return index;
	}

	private void buildManifestResources()
	{
		// DONE!
		edu.arizona.cs.mbel.mbel.ManifestResource[] res = module.getManifestResources();
		edu.arizona.cs.mbel.metadata.GenericTable manTable = null;

		for(int i = 0; i < res.length; i++)
		{
			if(res[i] instanceof edu.arizona.cs.mbel.mbel.LocalManifestResource)
			{
				// DONE!!!
				edu.arizona.cs.mbel.mbel.LocalManifestResource lmr = (edu.arizona.cs.mbel.mbel.LocalManifestResource) res[i];

				manTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.ManifestResource]);
				manTable.setFieldValue("Offset", new Long(localResources.getPosition()));
				manTable.setFieldValue("Flags", new Long(lmr.getFlags()));
				manTable.setFieldValue("Name", new Long(stringsGen.addString(lmr.getName())));
				manTable.setFieldValue("Implementation", new Long(0));
				tables[tc.ManifestResource].add(manTable);

				byte[] data = lmr.getResourceData();
				localResources.putINT32(data.length);
				localResources.put(data);
				localResources.pad(4);
			}
			else if(res[i] instanceof edu.arizona.cs.mbel.mbel.FileManifestResource)
			{
				// DONE!
				edu.arizona.cs.mbel.mbel.FileManifestResource fmr = (edu.arizona.cs.mbel.mbel.FileManifestResource) res[i];
				manTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.ManifestResource]);
				manTable.setFieldValue("Offset", new Long(0));
				manTable.setFieldValue("Flags", new Long(res[i].getFlags()));
				manTable.setFieldValue("Name", new Long(stringsGen.addString(res[i].getName())));

				long rid = addFile(fmr.getFileReference());
				long coded = tc.buildCodedIndex(tc.Implementation, tc.File, rid);
				manTable.setFieldValue("Implementation", new Long(coded));
				tables[tc.ManifestResource].add(manTable);
			}
			else if(res[i] instanceof edu.arizona.cs.mbel.mbel.AssemblyManifestResource)
			{
				// DONE!!
				edu.arizona.cs.mbel.mbel.AssemblyManifestResource amr = (edu.arizona.cs.mbel.mbel.AssemblyManifestResource) res[i];

				long assemblyRef = addAssemblyRef(amr.getAssemblyRefInfo());

				manTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.ManifestResource]);
				manTable.setFieldValue("Offset", new Long(0));
				manTable.setFieldValue("Flags", new Long(res[i].getFlags()));
				manTable.setFieldValue("Name", new Long(stringsGen.addString(res[i].getName())));

				long coded = tc.buildCodedIndex(tc.Implementation, tc.AssemblyRef, assemblyRef);
				manTable.setFieldValue("Implementation", new Long(coded));
				tables[tc.ManifestResource].add(manTable);
			}

			// Make CustomAttributes
			long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.ManifestResource, tables[tc.ManifestResource].size());
			addCustomAttributes(res[i].getManifestResourceAttributes(), codedparent);
		}
	}

	private long addExportedType(edu.arizona.cs.mbel.mbel.ExportedTypeRef ref)
	{
		// adds this as an ExportedType, NOT as a TypeRef!!! returns an ExportedType RID
		// DONE!!!
		if(ref.getExportedTypeRID() != -1L)
		{
			return ref.getExportedTypeRID();
		}

		edu.arizona.cs.mbel.metadata.GenericTable exTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.ExportedType]);
		tables[tc.ExportedType].add(exTable);
		ref.setExportedTypeRID(ExportedTypeCount++);

		exTable.setFieldValue("Flags", new Long(ref.getFlags()));
		exTable.setFieldValue("TypeDefID", new Long(0));
		exTable.setFieldValue("TypeName", new Long(stringsGen.addString(ref.getName())));
		exTable.setFieldValue("TypeNamespace", new Long(stringsGen.addString(ref.getNamespace())));

		long coded = 0;
		if(ref.getFileReference() != null)
		{
			edu.arizona.cs.mbel.mbel.FileReference fr = ref.getFileReference();
			long rid = addFile(fr);
			coded = tc.buildCodedIndex(tc.Implementation, tc.File, rid);
		}
		else
		{
			long rid = addExportedType(ref.getExportedTypeRef());
			coded = tc.buildCodedIndex(tc.Implementation, tc.ExportedType, rid);
		}
		exTable.setFieldValue("Implementation", new Long(coded));

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.ExportedType, ref.getExportedTypeRID());
		addCustomAttributes(ref.getExportedTypeAttributes(), codedparent);

		return ref.getExportedTypeRID();
	}

	private long addTypeDef(edu.arizona.cs.mbel.mbel.TypeDef def)
	{
		// all TypeDefs must be registered with the Module, so any others found will be errors
		if(def.getTypeDefRID() != -1L)
		{
			return def.getTypeDefRID();
		}

		edu.arizona.cs.mbel.metadata.GenericTable defTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.TypeDef]);

		def.setTypeDefRID(TypeDefCount++);
		tables[tc.TypeDef].add(defTable);

		defTable.setFieldValue("Flags", new Long(def.getFlags()));
		defTable.setFieldValue("Name", new Long(stringsGen.addString(def.getName())));
		defTable.setFieldValue("Namespace", new Long(stringsGen.addString(def.getNamespace())));

		edu.arizona.cs.mbel.mbel.AbstractTypeReference parent = def.getSuperClass();
		if(parent == null)
		{
			defTable.setFieldValue("Extends", new Long(0));
		}
		else if(parent instanceof edu.arizona.cs.mbel.mbel.TypeDef)
		{
			long rid = addTypeDef((edu.arizona.cs.mbel.mbel.TypeDef) parent);
			long coded = tc.buildCodedIndex(tc.TypeDefOrRef, tc.TypeDef, rid);
			defTable.setFieldValue("Extends", new Long(coded));
		}
		else if(parent instanceof edu.arizona.cs.mbel.mbel.TypeRef)
		{
			long rid = addTypeRef((edu.arizona.cs.mbel.mbel.TypeRef) parent);
			long coded = tc.buildCodedIndex(tc.TypeDefOrRef, tc.TypeRef, rid);
			defTable.setFieldValue("Extends", new Long(coded));
		}
		else if(parent instanceof edu.arizona.cs.mbel.mbel.TypeSpec)
		{
			long rid = addTypeSpec((edu.arizona.cs.mbel.mbel.TypeSpec) parent);
			long coded = tc.buildCodedIndex(tc.TypeDefOrRef, tc.TypeSpec, rid);
			defTable.setFieldValue("Extends", new Long(coded));
		}

		//////////////////////////////////////////////////////////////////
		// Make NestedClass tables DONE
		edu.arizona.cs.mbel.mbel.TypeDef[] nested = def.getNestedClasses();
		for(int i = 0; i < nested.length; i++)
		{
			edu.arizona.cs.mbel.metadata.GenericTable nestTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.NestedClass]);
			long myIndex = addTypeDef(nested[i]);
			nestTable.setFieldValue("NestedClass", new Long(myIndex));
			nestTable.setFieldValue("EnclosingClass", new Long(def.getTypeDefRID()));
			tables[tc.NestedClass].add(nestTable);
		}

		// Make ClassLayouts DONE
		edu.arizona.cs.mbel.mbel.ClassLayout layout = def.getClassLayout();
		if(layout != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable layoutTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.ClassLayout]);
			layoutTable.setFieldValue("PackingSize", new Integer(layout.getPackingSize()));
			layoutTable.setFieldValue("ClassSize", new Long(layout.getClassSize()));
			layoutTable.setFieldValue("Parent", new Long(def.getTypeDefRID()));
			tables[tc.ClassLayout].add(layoutTable);
		}

		// Make DeclSecurity DONE
		edu.arizona.cs.mbel.mbel.DeclSecurity decl = def.getDeclSecurity();
		if(decl != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable declTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.DeclSecurity]);
			declTable.setFieldValue("Action", new Integer(decl.getAction()));
			long coded = tc.buildCodedIndex(tc.HasDeclSecurity, tc.TypeDef, def.getTypeDefRID());
			declTable.setFieldValue("Parent", new Long(coded));
			declTable.setFieldValue("PermissionSet", new Long(blobGen.addBlob(decl.getPermissionSet())));
			tables[tc.DeclSecurity].add(declTable);

			// Make CustomAttributes (on DeclSecurity)
			long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.DeclSecurity, tables[tc.DeclSecurity].size());
			addCustomAttributes(decl.getDeclSecurityAttributes(), codedparent);
		}

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.TypeDef, def.getTypeDefRID());
		addCustomAttributes(def.getTypeDefAttributes(), codedparent);
		//////////////////////////////////////////////////////////////////

		return def.getTypeDefRID();
	}

	private void buildFields(edu.arizona.cs.mbel.mbel.TypeDef def)
	{
		// Make Fields
		edu.arizona.cs.mbel.mbel.Field[] defFields = def.getFields();
		edu.arizona.cs.mbel.metadata.GenericTable defTable = (edu.arizona.cs.mbel.metadata.GenericTable) tables[tc.TypeDef].get((int) def.getTypeDefRID()
				- 1);
		if(defFields.length == 0)
		{
			defTable.setFieldValue("FieldList", new Long(FieldCount));
		}
		else
		{
			long fieldStart = FieldCount;
			for(int i = 0; i < defFields.length; i++)
			{
				addField(defFields[i]);
			}
			defTable.setFieldValue("FieldList", new Long(fieldStart));
		}
	}

	private void buildMethods(edu.arizona.cs.mbel.mbel.TypeDef def)
	{
		// Make Methods
		edu.arizona.cs.mbel.mbel.Method[] defMethods = def.getMethods();
		edu.arizona.cs.mbel.metadata.GenericTable defTable = (edu.arizona.cs.mbel.metadata.GenericTable) tables[tc.TypeDef].get((int) def.getTypeDefRID()
				- 1);

		if(defMethods.length == 0)
		{
			defTable.setFieldValue("MethodList", new Long(MethodCount));
		}
		else
		{
			long methodStart = MethodCount;
			for(int i = 0; i < defMethods.length; i++)
			{
				addMethod(defMethods[i]);
			}
			defTable.setFieldValue("MethodList", new Long(methodStart));
		}
	}

	private long addField(edu.arizona.cs.mbel.mbel.Field field)
	{
		// assume this field is not in the list yet DONE!
		if(field.getFieldRID() != -1L)
		{
			return field.getFieldRID();
		}

		edu.arizona.cs.mbel.metadata.GenericTable fieldTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Field]);
		tables[tc.Field].add(fieldTable);
		field.setFieldRID(FieldCount++);

		fieldTable.setFieldValue("Flags", new Integer(field.getFlags()));
		fieldTable.setFieldValue("Name", new Long(stringsGen.addString(field.getName())));
		edu.arizona.cs.mbel.ByteBuffer fieldbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
		field.getSignature().emit(fieldbuffer, this);
		byte[] blob = fieldbuffer.toByteArray();
		fieldTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));

		// Make FieldMarshal DONE!
		edu.arizona.cs.mbel.signature.MarshalSignature sig = field.getFieldMarshal();
		if(sig != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable marTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.FieldMarshal]);

			long coded = tc.buildCodedIndex(tc.HasFieldMarshal, tc.Field, field.getFieldRID());
			marTable.setFieldValue("Parent", new Long(coded));

			edu.arizona.cs.mbel.ByteBuffer marbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
			sig.emit(marbuffer, this);
			byte[] data = marbuffer.toByteArray();
			marTable.setFieldValue("NativeType", new Long(blobGen.addBlob(data)));

			tables[tc.FieldMarshal].add(marTable);
		}

		// Make Constant DONE!
		byte[] value = field.getDefaultValue();
		if(value != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable constTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Constant]);

			byte type = field.getSignature().getType().getType();
			constTable.setFieldValue("Type", new Integer(type & 0xFF));
			constTable.setFieldValue("Padding", new Integer(0));

			long coded = tc.buildCodedIndex(tc.HasConst, tc.Field, field.getFieldRID());
			constTable.setFieldValue("Parent", new Long(coded));
			constTable.setFieldValue("Value", new Long(blobGen.addBlob(value)));

			tables[tc.Constant].add(constTable);
		}

		// Make FieldLayout DONE!
		if(field.getOffset() != -1L)
		{
			edu.arizona.cs.mbel.metadata.GenericTable layoutTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.FieldLayout]);
			layoutTable.setFieldValue("Offset", new Long(field.getOffset()));
			layoutTable.setFieldValue("Field", new Long(field.getFieldRID()));

			tables[tc.FieldLayout].add(layoutTable);
		}

		// Make FieldRVA DONE
		if(field.getFieldRVA() != -1L)
		{
			edu.arizona.cs.mbel.metadata.GenericTable fieldRVATable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.FieldRVA]);
			fieldRVATable.setFieldValue("RVA", new Long(field.getFieldRVA()));
			fieldRVATable.setFieldValue("Field", new Long(field.getFieldRID()));

			tables[tc.FieldRVA].add(fieldRVATable);
		}

		// Make CustomAttributes DONE!
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.Field, field.getFieldRID());
		addCustomAttributes(field.getFieldAttributes(), codedparent);

		return field.getFieldRID();
	}

	private void addCustomAttributes(edu.arizona.cs.mbel.mbel.CustomAttribute[] cas, long codedparent)
	{
		// Make CustomAttributes
		if(cas == null)
		{
			return;
		}

		for(int i = 0; i < cas.length; i++)
		{
			edu.arizona.cs.mbel.metadata.GenericTable caTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.CustomAttribute]);
			caTable.setFieldValue("Parent", new Long(codedparent));
			long cons = getMethodRefToken(cas[i].getConstructor());
			long type = 0;
			if(((cons >> 24) & 0xFF) == tc.Method)
			{
				type = tc.buildCodedIndex(tc.CustomAttributeType, tc.Method, (cons & 0xFFFFFFL));
			}
			else
			{
				type = tc.buildCodedIndex(tc.CustomAttributeType, tc.MemberRef, (cons & 0xFFFFFFL));
			}
			caTable.setFieldValue("Type", new Long(type));
			caTable.setFieldValue("Value", new Long(blobGen.addBlob(cas[i].getSignature())));

			if(!tables[tc.CustomAttribute].contains(caTable))
			{
				tables[tc.CustomAttribute].add(caTable);
			}
		}
	}

	private long addAssemblyRef(edu.arizona.cs.mbel.mbel.AssemblyRefInfo info)
	{
		edu.arizona.cs.mbel.metadata.GenericTable assRefTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.AssemblyRef]);

		assRefTable.setFieldValue("MajorVersion", new Integer(info.getMajorVersion()));
		assRefTable.setFieldValue("MinorVersion", new Integer(info.getMinorVersion()));
		assRefTable.setFieldValue("BuildNumber", new Integer(info.getBuildNumber()));
		assRefTable.setFieldValue("RevisionNumber", new Integer(info.getRevisionNumber()));
		assRefTable.setFieldValue("Flags", new Long(info.getFlags()));
		assRefTable.setFieldValue("PublicKeyOrToken", new Long(blobGen.addBlob(info.getPublicKeyOrToken())));
		assRefTable.setFieldValue("Name", new Long(stringsGen.addString(info.getName())));
		assRefTable.setFieldValue("Culture", new Long(stringsGen.addString(info.getCulture())));
		assRefTable.setFieldValue("HashValue", new Long(blobGen.addBlob(info.getHashValue())));

		int index = tables[tc.AssemblyRef].indexOf(assRefTable);
		if(index == -1)
		{
			tables[tc.AssemblyRef].add(assRefTable);
			index = tables[tc.AssemblyRef].size();
		}
		else
		{
			index++;
		}

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.AssemblyRef, (long) index);
		addCustomAttributes(info.getAssemblyRefAttributes(), codedparent);

		return (long) index;
	}

	private long addModuleRef(edu.arizona.cs.mbel.mbel.ModuleRefInfo info)
	{
		edu.arizona.cs.mbel.metadata.GenericTable modRefTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.ModuleRef]);

		modRefTable.setFieldValue("Name", new Long(stringsGen.addString(info.getModuleName())));

		int index = tables[tc.ModuleRef].indexOf(modRefTable);
		if(index == -1)
		{
			tables[tc.ModuleRef].add(modRefTable);
			index = tables[tc.ModuleRef].size();
		}
		else
		{
			index++;
		}

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.ModuleRef, (long) index);
		addCustomAttributes(info.getModuleRefAttributes(), codedparent);

		return (long) index;
	}

	private long addMethod(edu.arizona.cs.mbel.mbel.Method method)
	{
		// DONE!
		if(method.getMethodRID() != -1L)
		{
			return method.getMethodRID();
		}

		edu.arizona.cs.mbel.metadata.GenericTable methodTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Method]);

		method.setMethodRID(MethodCount++);
		tables[tc.Method].add(methodTable);

		if(method.getMethodRVA() == -1L)
		{
			methodTable.setFieldValue("RVA", new Long(0));
		}
		else
		{
			methodTable.setFieldValue("RVA", new Long(method.getMethodRVA()));
		}
		methodTable.setFieldValue("ImplFlags", new Integer(method.getImplFlags()));
		methodTable.setFieldValue("Flags", new Integer(method.getFlags()));
		methodTable.setFieldValue("Name", new Long(stringsGen.addString(method.getName())));

		edu.arizona.cs.mbel.ByteBuffer sigbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
		method.getSignature().emit(sigbuffer, this);
		byte[] blob = sigbuffer.toByteArray();
		methodTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));

		edu.arizona.cs.mbel.signature.ParameterSignature[] parameters = method.getSignature().getParameters();
		if(parameters.length == 0)
		{
			methodTable.setFieldValue("ParamList", new Long(ParamCount));
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
			methodTable.setFieldValue("ParamList", new Long(paramStart));
		}

		// Make DeclSecurity
		edu.arizona.cs.mbel.mbel.DeclSecurity decl = method.getDeclSecurity();
		if(decl != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable declTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.DeclSecurity]);
			declTable.setFieldValue("Action", new Integer(decl.getAction()));
			long coded = tc.buildCodedIndex(tc.HasDeclSecurity, tc.Method, method.getMethodRID());
			declTable.setFieldValue("Parent", new Long(coded));
			declTable.setFieldValue("PermissionSet", new Long(blobGen.addBlob(decl.getPermissionSet())));

			tables[tc.DeclSecurity].add(declTable);

			// Make CustomAttribute (on DeclSecurity)
			long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.DeclSecurity, tables[tc.DeclSecurity].size());
			addCustomAttributes(decl.getDeclSecurityAttributes(), codedparent);
		}

		// Make MethodSemantics
		edu.arizona.cs.mbel.mbel.MethodSemantics sem = method.getMethodSemantics();
		if(sem != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable semTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.MethodSemantics]);
			semTable.setFieldValue("Semantics", new Integer(sem.getSemantics()));
			semTable.setFieldValue("Method", new Long(method.getMethodRID()));
			if(sem.getEvent() != null)
			{
				long coded = tc.buildCodedIndex(tc.HasSemantics, tc.Event, sem.getEvent().getEventRID());
				semTable.setFieldValue("Association", new Long(coded));
			}
			else
			{
				long coded = tc.buildCodedIndex(tc.HasSemantics, tc.Property, sem.getProperty().getPropertyRID());
				semTable.setFieldValue("Association", new Long(coded));
			}
			tables[tc.MethodSemantics].add(semTable);
		}

		// Make ImplMap
		edu.arizona.cs.mbel.mbel.ImplementationMap map = method.getImplementationMap();
		if(map != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable mapTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.ImplMap]);
			mapTable.setFieldValue("MappingFlags", new Integer(map.getFlags()));

			long coded = tc.buildCodedIndex(tc.MemberForwarded, tc.Method, method.getMethodRID());
			mapTable.setFieldValue("MemberForwarded", new Long(coded));
			mapTable.setFieldValue("ImportName", new Long(stringsGen.addString(map.getImportName())));

			long moduleRef = addModuleRef(map.getImportScope());
			mapTable.setFieldValue("ImportScope", new Long(moduleRef));

			tables[tc.ImplMap].add(mapTable);
		}

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.Method, method.getMethodRID());
		addCustomAttributes(method.getMethodAttributes(), codedparent);

		return method.getMethodRID();
	}

	private void buildMethodBodies(edu.arizona.cs.mbel.mbel.TypeDef def)
	{
		// Make method body
		edu.arizona.cs.mbel.mbel.Method[] methodlist = def.getMethods();

		for(int i = 0; i < methodlist.length; i++)
		{
			edu.arizona.cs.mbel.mbel.MethodBody body = methodlist[i].getMethodBody();
			if(body != null)
			{
				edu.arizona.cs.mbel.metadata.GenericTable methodTable = (edu.arizona.cs.mbel.metadata.GenericTable) tables[tc.Method].get((int) methodlist[i]
						.getMethodRID() - 1);

				edu.arizona.cs.mbel.ByteBuffer bodybuffer = new edu.arizona.cs.mbel.ByteBuffer(1000);
				body.emit(bodybuffer, this);
				methodBodies.add(bodybuffer);
				methodTable.setFieldValue("RVA", new Long(methodBodies.size()));
				// 'RVA' gets assigned a 1-based index into the methodBodies vector corresponding to its body
			}
		}
	}


	private void buildMethodImpls(edu.arizona.cs.mbel.mbel.TypeDef def)
	{
		// DONE!
		edu.arizona.cs.mbel.mbel.MethodMap[] maps = def.getMethodMaps();

		for(int i = 0; i < maps.length; i++)
		{
			edu.arizona.cs.mbel.metadata.GenericTable mapTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.MethodImpl]);
			mapTable.setFieldValue("Class", new Long(def.getTypeDefRID()));

			edu.arizona.cs.mbel.mbel.MethodDefOrRef body = maps[i].getMethodBody();
			edu.arizona.cs.mbel.mbel.MethodDefOrRef decl = maps[i].getMethodDeclaration();

			// DONE!
			if(body instanceof edu.arizona.cs.mbel.mbel.Method)
			{
				edu.arizona.cs.mbel.mbel.Method method = (edu.arizona.cs.mbel.mbel.Method) body;
				long coded = tc.buildCodedIndex(tc.MethodDefOrRef, tc.Method, method.getMethodRID());
				mapTable.setFieldValue("MethodBody", new Long(coded));
			}
			else
			{
				long rid = addMemberRef(body);
				long coded = tc.buildCodedIndex(tc.MethodDefOrRef, tc.MemberRef, rid);
				mapTable.setFieldValue("MethodBody", new Long(coded));
			}

			// DONE!
			if(decl instanceof edu.arizona.cs.mbel.mbel.Method)
			{
				edu.arizona.cs.mbel.mbel.Method method = (edu.arizona.cs.mbel.mbel.Method) decl;
				long coded = tc.buildCodedIndex(tc.MethodDefOrRef, tc.Method, method.getMethodRID());
				mapTable.setFieldValue("MethodDeclaration", new Long(coded));
			}
			else
			{
				long rid = addMemberRef(decl);
				long coded = tc.buildCodedIndex(tc.MethodDefOrRef, tc.MemberRef, rid);
				mapTable.setFieldValue("MethodDeclaration", new Long(coded));
			}

			tables[tc.MethodImpl].add(mapTable);
		}
	}

	private long addParam(edu.arizona.cs.mbel.signature.ParameterInfo parameter, int seq, byte type)
	{
		// DONE!
		if(parameter.getParamRID() != -1L)
		{
			return parameter.getParamRID();
		}

		edu.arizona.cs.mbel.metadata.GenericTable paramTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Param]);

		parameter.setParamRID(ParamCount++);
		tables[tc.Param].add(paramTable);

		paramTable.setFieldValue("Flags", new Integer(parameter.getFlags()));
		paramTable.setFieldValue("Sequence", new Integer(seq));
		paramTable.setFieldValue("Name", new Long(stringsGen.addString(parameter.getName())));

		// Make FieldMarshal
		edu.arizona.cs.mbel.signature.MarshalSignature sig = parameter.getFieldMarshal();
		if(sig != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable marshalTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.FieldMarshal]);
			long coded = tc.buildCodedIndex(tc.HasFieldMarshal, tc.Param, parameter.getParamRID());
			marshalTable.setFieldValue("Parent", new Long(coded));

			edu.arizona.cs.mbel.ByteBuffer marbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
			sig.emit(marbuffer, this);
			byte[] blob = marbuffer.toByteArray();
			marshalTable.setFieldValue("NativeType", new Long(blobGen.addBlob(blob)));
			tables[tc.FieldMarshal].add(marshalTable);
		}

		// Make Constant
		byte[] value = parameter.getDefaultValue();
		if(value != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable constTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Constant]);
			constTable.setFieldValue("Type", new Integer(type & 0xFF));
			constTable.setFieldValue("Padding", new Integer(0));
			long coded = tc.buildCodedIndex(tc.HasConst, tc.Param, parameter.getParamRID());
			constTable.setFieldValue("Parent", new Long(coded));
			constTable.setFieldValue("Value", new Long(blobGen.addBlob(value)));
			tables[tc.Constant].add(constTable);
		}

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.Param, parameter.getParamRID());
		addCustomAttributes(parameter.getParamAttributes(), codedparent);

		return parameter.getParamRID();
	}

	private void buildInterfaceImpls(edu.arizona.cs.mbel.mbel.TypeDef def)
	{
		// DONE!
		edu.arizona.cs.mbel.mbel.InterfaceImplementation[] interfaces = def.getInterfaceImplementations();

		for(int i = 0; i < interfaces.length; i++)
		{
			edu.arizona.cs.mbel.metadata.GenericTable implTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.InterfaceImpl]);

			implTable.setFieldValue("Class", new Long(def.getTypeDefRID()));

			if(interfaces[i].getInterface() instanceof edu.arizona.cs.mbel.mbel.TypeDef)
			{
				edu.arizona.cs.mbel.mbel.TypeDef interdef = (edu.arizona.cs.mbel.mbel.TypeDef) interfaces[i].getInterface();
				long coded = tc.buildCodedIndex(tc.TypeDefOrRef, tc.TypeDef, interdef.getTypeDefRID());
				implTable.setFieldValue("Interface", new Long(coded));
			}
			else
			{
				long rid = addTypeRef(interfaces[i].getInterface());
				long coded = tc.buildCodedIndex(tc.TypeDefOrRef, tc.TypeRef, rid);
				implTable.setFieldValue("Interface", new Long(coded));
			}

			tables[tc.InterfaceImpl].add(implTable);

			// Make CustomAttributes
			long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.InterfaceImpl, tables[tc.InterfaceImpl].size());
			addCustomAttributes(interfaces[i].getInterfaceImplAttributes(), codedparent);
		}
	}

	private void buildProperties(edu.arizona.cs.mbel.mbel.TypeDef def)
	{
		// DONE!!
		edu.arizona.cs.mbel.mbel.Property[] props = def.getProperties();
		if(props.length == 0)
		{
			return;
		}

		edu.arizona.cs.mbel.metadata.GenericTable mapTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.PropertyMap]);
		mapTable.setFieldValue("Parent", new Long(def.getTypeDefRID()));
		mapTable.setFieldValue("PropertyList", new Long(PropertyCount));
		tables[tc.PropertyMap].add(mapTable);

		for(int i = 0; i < props.length; i++)
		{
			edu.arizona.cs.mbel.metadata.GenericTable propTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Property]);
			props[i].setPropertyRID(PropertyCount++);
			tables[tc.Property].add(propTable);

			propTable.setFieldValue("Flags", new Integer(props[i].getFlags()));
			propTable.setFieldValue("Name", new Long(stringsGen.addString(props[i].getName())));
			edu.arizona.cs.mbel.ByteBuffer propBuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
			props[i].getSignature().emit(propBuffer, this);
			byte[] blob = propBuffer.toByteArray();
			propTable.setFieldValue("Type", new Long(blobGen.addBlob(blob)));

			// Make Constant
			byte[] value = props[i].getDefaultValue();
			if(value != null)
			{
				edu.arizona.cs.mbel.metadata.GenericTable constTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Constant]);
				constTable.setFieldValue("Type", new Integer(props[i].getSignature().getType().getType() & 0xFF));
				long coded = tc.buildCodedIndex(tc.HasConst, tc.Property, props[i].getPropertyRID());
				constTable.setFieldValue("Parent", new Long(coded));
				constTable.setFieldValue("Value", new Long(blobGen.addBlob(value)));

				tables[tc.Constant].add(constTable);
			}

			// Make CustomAttributes
			long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.Property, props[i].getPropertyRID());
			addCustomAttributes(props[i].getPropertyAttributes(), codedparent);
		}
	}

	private void buildEvents(edu.arizona.cs.mbel.mbel.TypeDef def)
	{
		// DONE!
		edu.arizona.cs.mbel.mbel.Event[] eventlist = def.getEvents();
		if(eventlist.length == 0)
		{
			return;
		}

		edu.arizona.cs.mbel.metadata.GenericTable mapTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.EventMap]);
		mapTable.setFieldValue("Parent", new Long(def.getTypeDefRID()));
		mapTable.setFieldValue("EventList", new Long(EventCount));
		tables[tc.EventMap].add(mapTable);

		for(int i = 0; i < eventlist.length; i++)
		{
			edu.arizona.cs.mbel.metadata.GenericTable eventTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.Event]);
			eventlist[i].setEventRID(EventCount++);
			tables[tc.Event].add(eventTable);

			eventTable.setFieldValue("EventFlags", new Integer(eventlist[i].getEventFlags()));
			eventTable.setFieldValue("Name", new Long(stringsGen.addString(eventlist[i].getName())));
			edu.arizona.cs.mbel.mbel.TypeRef ref = eventlist[i].getEventType();
			if(ref instanceof edu.arizona.cs.mbel.mbel.TypeDef)
			{
				edu.arizona.cs.mbel.mbel.TypeDef newdef = (edu.arizona.cs.mbel.mbel.TypeDef) ref;
				long coded = tc.buildCodedIndex(tc.TypeDefOrRef, tc.TypeDef, newdef.getTypeDefRID());
				eventTable.setFieldValue("EventType", new Long(coded));
			}
			else
			{
				long refrid = addTypeRef(ref);
				long coded = tc.buildCodedIndex(tc.TypeDefOrRef, tc.TypeRef, refrid);
				eventTable.setFieldValue("EventType", new Long(coded));
			}

			// Make CustomAttribute
			long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.Event, eventlist[i].getEventRID());
			addCustomAttributes(eventlist[i].getEventAttributes(), codedparent);
		}
	}

	private long addTypeSpec(edu.arizona.cs.mbel.mbel.TypeSpec spec)
	{
		// DONE!
		if(spec.getTypeSpecRID() != -1L)
		{
			return spec.getTypeSpecRID();
		}

		edu.arizona.cs.mbel.metadata.GenericTable specTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.TypeSpec]);
		spec.setTypeSpecRID(TypeSpecCount++);
		tables[tc.TypeSpec].add(specTable);

		edu.arizona.cs.mbel.ByteBuffer specBuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
		spec.getSignature().emit(specBuffer, this);
		byte[] blob = specBuffer.toByteArray();
		specTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.TypeSpec, spec.getTypeSpecRID());
		addCustomAttributes(spec.getTypeSpecAttributes(), codedparent);

		return spec.getTypeSpecRID();
	}

	private long addTypeRef(edu.arizona.cs.mbel.mbel.TypeRef ref)
	{
		if(ref.getTypeRefRID() != -1L)
		{
			return ref.getTypeRefRID();
		}

		edu.arizona.cs.mbel.metadata.GenericTable refTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.TypeRef]);
		ref.setTypeRefRID(TypeRefCount++);
		tables[tc.TypeRef].add(refTable);

		refTable.setFieldValue("Name", new Long(stringsGen.addString(ref.getName())));
		refTable.setFieldValue("Namespace", new Long(stringsGen.addString(ref.getNamespace())));
		if(ref instanceof edu.arizona.cs.mbel.mbel.TypeDef)
		{
			// DONE!
			edu.arizona.cs.mbel.mbel.TypeDef def = (edu.arizona.cs.mbel.mbel.TypeDef) ref;
			long coded = tc.buildCodedIndex(tc.ResolutionScope, tc.Module, 1L);
			refTable.setFieldValue("ResolutionScope", new Long(coded));
		}
		else if(ref instanceof edu.arizona.cs.mbel.mbel.ModuleTypeRef)
		{
			// DONE!
			edu.arizona.cs.mbel.mbel.ModuleTypeRef mref = (edu.arizona.cs.mbel.mbel.ModuleTypeRef) ref;

			long moduleRef = addModuleRef(mref.getModuleRefInfo());
			long coded = tc.buildCodedIndex(tc.ResolutionScope, tc.ModuleRef, moduleRef);
			refTable.setFieldValue("ResolutionScope", new Long(coded));
		}
		else if(ref instanceof edu.arizona.cs.mbel.mbel.AssemblyTypeRef)
		{
			// DONE!
			edu.arizona.cs.mbel.mbel.AssemblyTypeRef aref = (edu.arizona.cs.mbel.mbel.AssemblyTypeRef) ref;

			long assemblyRef = addAssemblyRef(aref.getAssemblyRefInfo());

			long coded = tc.buildCodedIndex(tc.ResolutionScope, tc.AssemblyRef, assemblyRef);
			refTable.setFieldValue("ResolutionScope", new Long(coded));
		}
		else if(ref instanceof edu.arizona.cs.mbel.mbel.NestedTypeRef)
		{
			// DONE!!
			edu.arizona.cs.mbel.mbel.NestedTypeRef nref = (edu.arizona.cs.mbel.mbel.NestedTypeRef) ref;

			long refRID = addTypeRef(nref.getEnclosingTypeRef());
			long coded = tc.buildCodedIndex(tc.ResolutionScope, tc.TypeRef, refRID);
			refTable.setFieldValue("ResolutionScope", new Long(coded));
		}
		else if(ref instanceof edu.arizona.cs.mbel.mbel.ExportedTypeRef)
		{
			// DONE!!
			edu.arizona.cs.mbel.mbel.ExportedTypeRef eref = (edu.arizona.cs.mbel.mbel.ExportedTypeRef) ref;

			addExportedType(eref);
			refTable.setFieldValue("ResolutionScope", new Long(0));
		}

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.TypeRef, ref.getTypeRefRID());
		addCustomAttributes(ref.getTypeRefAttributes(), codedparent);

		return ref.getTypeRefRID();
	}

	private long addMemberRef(edu.arizona.cs.mbel.mbel.MemberRef ref)
	{
		// DONE!
		// adds this as a MemberRef, not as a Field or Method!
		if(ref.getMemberRefRID() != -1L)
		{
			return ref.getMemberRefRID();
		}

		if((ref instanceof edu.arizona.cs.mbel.mbel.Field) || (ref instanceof edu.arizona.cs.mbel.mbel.Method))
		{
			return 0L;
		}

		edu.arizona.cs.mbel.metadata.GenericTable refTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.MemberRef]);
		tables[tc.MemberRef].add(refTable);
		ref.setMemberRefRID(MemberRefCount++);

		refTable.setFieldValue("Name", new Long(stringsGen.addString(ref.getName())));

		if(ref instanceof edu.arizona.cs.mbel.mbel.FieldRef)
		{
			// DONE!
			if(ref instanceof edu.arizona.cs.mbel.mbel.GlobalFieldRef)
			{
				// DONE!
				edu.arizona.cs.mbel.mbel.GlobalFieldRef fref = (edu.arizona.cs.mbel.mbel.GlobalFieldRef) ref;

				long moduleRef = addModuleRef(fref.getModuleRefInfo());
				long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.ModuleRef, moduleRef);
				refTable.setFieldValue("Class", new Long(coded));

				edu.arizona.cs.mbel.ByteBuffer refbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
				fref.getSignature().emit(refbuffer, this);
				byte[] blob = refbuffer.toByteArray();
				refTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));
			}
			else
			{
				// DONE!
				// normal FieldRef
				edu.arizona.cs.mbel.mbel.FieldRef fref = (edu.arizona.cs.mbel.mbel.FieldRef) ref;
				edu.arizona.cs.mbel.mbel.AbstractTypeReference parent = fref.getParent();
				if(parent instanceof edu.arizona.cs.mbel.mbel.TypeDef)
				{
					edu.arizona.cs.mbel.mbel.TypeDef parentdef = (edu.arizona.cs.mbel.mbel.TypeDef) parent;

					long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.TypeDef, parentdef.getTypeDefRID());
					refTable.setFieldValue("Class", new Long(coded));
				}
				else if(parent instanceof edu.arizona.cs.mbel.mbel.TypeRef)
				{
					edu.arizona.cs.mbel.mbel.TypeRef parentref = (edu.arizona.cs.mbel.mbel.TypeRef) parent;

					long rid = addTypeRef(parentref);
					long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.TypeRef, rid);
					refTable.setFieldValue("Class", new Long(coded));
				}
				else if(parent instanceof edu.arizona.cs.mbel.mbel.TypeSpec)
				{
					edu.arizona.cs.mbel.mbel.TypeSpec parentspec = (edu.arizona.cs.mbel.mbel.TypeSpec) parent;

					long rid = addTypeSpec(parentspec);
					long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.TypeSpec, rid);
					refTable.setFieldValue("Class", new Long(coded));
				}

				edu.arizona.cs.mbel.ByteBuffer refbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
				fref.getSignature().emit(refbuffer, this);
				byte[] blob = refbuffer.toByteArray();
				refTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));
			}


		}
		else if(ref instanceof edu.arizona.cs.mbel.mbel.MethodDefOrRef)
		{
			if(ref instanceof edu.arizona.cs.mbel.mbel.GlobalMethodRef)
			{
				// DONE!
				edu.arizona.cs.mbel.mbel.GlobalMethodRef gref = (edu.arizona.cs.mbel.mbel.GlobalMethodRef) ref;

				long moduleRef = addModuleRef(gref.getModuleRefInfo());
				long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.ModuleRef, moduleRef);
				refTable.setFieldValue("Class", new Long(coded));

				edu.arizona.cs.mbel.ByteBuffer sigbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
				gref.getCallsiteSignature().emit(sigbuffer, this);
				byte[] blob = sigbuffer.toByteArray();
				refTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));
			}
			else if(ref instanceof edu.arizona.cs.mbel.mbel.VarargsMethodRef)
			{
				// DONE!
				edu.arizona.cs.mbel.mbel.VarargsMethodRef vref = (edu.arizona.cs.mbel.mbel.VarargsMethodRef) ref;

				long rid = vref.getMethod().getMethodRID();
				long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.Method, rid);
				refTable.setFieldValue("Class", new Long(coded));

				edu.arizona.cs.mbel.ByteBuffer sigbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
				vref.getCallsiteSignature().emit(sigbuffer, this);
				byte[] blob = sigbuffer.toByteArray();
				refTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));
			}
			else
			{
				// DONE!
				// normal MethodRef
				edu.arizona.cs.mbel.mbel.MethodRef mref = (edu.arizona.cs.mbel.mbel.MethodRef) ref;
				edu.arizona.cs.mbel.mbel.AbstractTypeReference parent = mref.getParent();

				if(parent instanceof edu.arizona.cs.mbel.mbel.TypeDef)
				{
					edu.arizona.cs.mbel.mbel.TypeDef parentdef = (edu.arizona.cs.mbel.mbel.TypeDef) parent;

					long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.TypeDef, parentdef.getTypeDefRID());
					refTable.setFieldValue("Class", new Long(coded));
				}
				else if(parent instanceof edu.arizona.cs.mbel.mbel.TypeRef)
				{
					edu.arizona.cs.mbel.mbel.TypeRef parentref = (edu.arizona.cs.mbel.mbel.TypeRef) parent;

					long rid = addTypeRef(parentref);
					long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.TypeRef, rid);
					refTable.setFieldValue("Class", new Long(coded));
				}
				else if(parent instanceof edu.arizona.cs.mbel.mbel.TypeSpec)
				{
					edu.arizona.cs.mbel.mbel.TypeSpec parentspec = (edu.arizona.cs.mbel.mbel.TypeSpec) parent;

					long rid = addTypeSpec(parentspec);
					long coded = tc.buildCodedIndex(tc.MemberRefParent, tc.TypeSpec, rid);
					refTable.setFieldValue("Class", new Long(coded));
				}

				edu.arizona.cs.mbel.ByteBuffer sigbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
				mref.getCallsiteSignature().emit(sigbuffer, this);
				byte[] blob = sigbuffer.toByteArray();
				refTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));
			}
		}

		// Make CustomAttribute
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.MemberRef, ref.getMemberRefRID());
		addCustomAttributes(ref.getMemberRefAttributes(), codedparent);

		return ref.getMemberRefRID();
	}

	private void buildEntryPointToken()
	{
		edu.arizona.cs.mbel.mbel.EntryPoint entryPoint = module.getEntryPoint();
		if(entryPoint != null)
		{
			if(entryPoint.getEntryPointFile() != null)
			{
				EntryPointToken = addFile(entryPoint.getEntryPointFile());
				EntryPointToken |= (long) (tc.File << 24);
			}
			else
			{
				EntryPointToken = entryPoint.getEntryPointMethod().getMethodRID();
				EntryPointToken |= (long) (tc.Method << 24);
			}
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a StandAloneSig token for the given method callsite signature
	 */
	public long getStandAloneSigToken(edu.arizona.cs.mbel.signature.MethodSignature callsiteSig)
	{
		// to be called by calli instructions only (not LocalVarList in methodbodies)
		// returns a token, not an RID
		edu.arizona.cs.mbel.ByteBuffer sigbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
		callsiteSig.emit(sigbuffer, this);
		byte[] blob = sigbuffer.toByteArray();

		edu.arizona.cs.mbel.metadata.GenericTable sigTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.StandAloneSig]);
		sigTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));

		int index = tables[tc.StandAloneSig].indexOf(sigTable);
		if(index == -1)
		{
			tables[tc.StandAloneSig].add(sigTable);
			index = tables[tc.StandAloneSig].size();
		}
		else
		{
			index++;
		}
		long token = (index | (tc.StandAloneSig << 24)) & 0xFFFFFFFFL;

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.StandAloneSig, (long) index);
		addCustomAttributes(callsiteSig.getStandAloneSigAttributes(), codedparent);

		return token;
	}

	/**
	 * Returns a StandAloneSig token for the given local variable list
	 */
	public long getStandAloneSigToken(edu.arizona.cs.mbel.signature.LocalVarList localVars)
	{
		// to be called by method body
		// returns a token, not an RID
		edu.arizona.cs.mbel.metadata.GenericTable sigTable = new edu.arizona.cs.mbel.metadata.GenericTable(tc.GRAMMAR[tc.StandAloneSig]);
		edu.arizona.cs.mbel.ByteBuffer sigbuffer = new edu.arizona.cs.mbel.ByteBuffer(100);
		localVars.emit(sigbuffer, this);
		byte[] blob = sigbuffer.toByteArray();
		sigTable.setFieldValue("Signature", new Long(blobGen.addBlob(blob)));

		int index = tables[tc.StandAloneSig].indexOf(sigTable);
		if(index == -1)
		{
			tables[tc.StandAloneSig].add(sigTable);
			index = tables[tc.StandAloneSig].size();
		}
		else
		{
			index++;
		}

		long token = ((long) index | (long) (tc.StandAloneSig << 24));

		// Make CustomAttributes
		long codedparent = tc.buildCodedIndex(tc.HasCustomAttribute, tc.StandAloneSig, (long) index);
		addCustomAttributes(localVars.getStandAloneSigAttributes(), codedparent);

		return token;
	}

	/**
	 * Returns the MethodRef or MethodDef token for the given method
	 */
	public long getMethodRefToken(edu.arizona.cs.mbel.mbel.MethodDefOrRef method)
	{
		if(method instanceof edu.arizona.cs.mbel.mbel.Method)
		{
			edu.arizona.cs.mbel.mbel.Method meth = (edu.arizona.cs.mbel.mbel.Method) method;
			return meth.getMethodRID() | (long) (tc.Method << 24);
		}
		else
		{
			long rid = addMemberRef(method);
			return (rid | (long) (tc.MemberRef << 24));
		}
	}

	/**
	 * Returns the FieldRef or FieldDef token for the given field
	 */
	public long getFieldRefToken(edu.arizona.cs.mbel.mbel.FieldRef field)
	{
		if(field instanceof edu.arizona.cs.mbel.mbel.Field)
		{
			edu.arizona.cs.mbel.mbel.Field f = (edu.arizona.cs.mbel.mbel.Field) field;
			return (f.getFieldRID() | (long) (tc.Field << 24));
		}
		else
		{
			long rid = addMemberRef(field);
			return (rid | (long) (tc.MemberRef << 24));
		}
	}

	/**
	 * Returns the TypeDef, TypeRef, or TypeSpec token for the given type
	 */
	public long getTypeToken(edu.arizona.cs.mbel.mbel.AbstractTypeReference ref)
	{
		// DONE!
		if(ref instanceof edu.arizona.cs.mbel.mbel.TypeDef)
		{
			edu.arizona.cs.mbel.mbel.TypeDef def = (edu.arizona.cs.mbel.mbel.TypeDef) ref;
			long result = 0L;
			if(def.getTypeDefRID() != -1L)
			{
				result = def.getTypeDefRID();
				result |= (tc.TypeDef << 24);
			}
			return result;
		}
		else if(ref instanceof edu.arizona.cs.mbel.mbel.TypeRef)
		{
			edu.arizona.cs.mbel.mbel.TypeRef typeref = (edu.arizona.cs.mbel.mbel.TypeRef) ref;
			long result = addTypeRef(typeref);
			result |= (tc.TypeRef << 24);
			return result;
		}
		else if(ref instanceof edu.arizona.cs.mbel.mbel.TypeSpec)
		{
			edu.arizona.cs.mbel.mbel.TypeSpec spec = (edu.arizona.cs.mbel.mbel.TypeSpec) ref;
			long result = addTypeSpec(spec);
			result |= (tc.TypeSpec << 24);
			return result;
		}
		return 0L;
	}

	/**
	 * Returns the correct token for the given loadable type (from ldtoken instruction)
	 */
	public long getLoadableTypeToken(edu.arizona.cs.mbel.instructions.LoadableType type)
	{
		if(type instanceof edu.arizona.cs.mbel.mbel.AbstractTypeReference)
		{
			if(type instanceof edu.arizona.cs.mbel.mbel.TypeDef)
			{
				edu.arizona.cs.mbel.mbel.TypeDef def = (edu.arizona.cs.mbel.mbel.TypeDef) type;
				return (def.getTypeDefRID() | (long) (tc.TypeDef << 24));
			}
			else if(type instanceof edu.arizona.cs.mbel.mbel.TypeSpec)
			{
				edu.arizona.cs.mbel.mbel.TypeSpec spec = (edu.arizona.cs.mbel.mbel.TypeSpec) type;
				long rid = addTypeSpec(spec);
				return (rid | (long) (tc.TypeSpec << 24));
			}
			else
			{
				// TypeRef
				edu.arizona.cs.mbel.mbel.TypeRef ref = (edu.arizona.cs.mbel.mbel.TypeRef) type;
				long rid = addTypeRef(ref);
				return (rid | (long) (tc.TypeRef << 24));
			}
		}
		else
		{// MemberRef
			if(type instanceof edu.arizona.cs.mbel.mbel.MethodDefOrRef)
			{
				// MethodDefOrRef
				edu.arizona.cs.mbel.mbel.MethodDefOrRef method = (edu.arizona.cs.mbel.mbel.MethodDefOrRef) type;
				return getMethodRefToken(method);
			}
			else
			{
				// FieldRef
				edu.arizona.cs.mbel.mbel.FieldRef field = (edu.arizona.cs.mbel.mbel.FieldRef) type;
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
		return (token | (long) (tc.USString << 24));
	}

	/**
	 * Returns the list of metadata tables, indexed by token type number
	 */
	public java.util.Vector[] getTables()
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
	public java.util.Vector getMethodBodies()
	{
		return methodBodies;
	}

	/**
	 * Returns a ByteBuffer with all the managed resource data to be embedded in this file.
	 */
	public edu.arizona.cs.mbel.ByteBuffer getLocalResources()
	{
		return localResources;
	}

	/**
	 * Writes a Strings token to a buffer, in the appropriate size
	 *
	 * @param buffer the buffer to write to
	 * @param token  a Number containing the strings token (will be a Long)
	 */
	public void putStringsToken(edu.arizona.cs.mbel.ByteBuffer buffer, Number token)
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
	public void putBlobToken(edu.arizona.cs.mbel.ByteBuffer buffer, Number token)
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
	public void putGUIDToken(edu.arizona.cs.mbel.ByteBuffer buffer, Number token)
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
	public void putTableIndex(edu.arizona.cs.mbel.ByteBuffer buffer, int type, Number rid)
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
	public void putCodedIndex(edu.arizona.cs.mbel.ByteBuffer buffer, int type, Number coded)
	{
		int max = 0;

		for(int i = 0; i < tc.TABLE_OPTIONS[type].length; i++)
		{
			if(0 <= tc.TABLE_OPTIONS[type][i] && tc.TABLE_OPTIONS[type][i] < 64)
			{
				max = Math.max(max, tables[tc.TABLE_OPTIONS[type][i]].size());
			}
		}

		if((max << tc.BITS[type]) >= 65536)
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
