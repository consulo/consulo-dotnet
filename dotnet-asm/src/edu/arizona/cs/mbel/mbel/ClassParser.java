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

package edu.arizona.cs.mbel.mbel;

/**
 * This class is all that is needed to parse a Module from a file.
 * The user will construct a ClassParser, then call parseMetadata and get back an MBEL Module.
 * The ClassParser uses lots of memory, so it should be discarded after the call to parseMetadata.
 *
 * @author Michael Stepp
 */
public class ClassParser
{
	private edu.arizona.cs.mbel.parse.PEModule pe_module;
	private edu.arizona.cs.mbel.metadata.GenericTable[][] tables;
	private edu.arizona.cs.mbel.metadata.TableConstants tc;
	private edu.arizona.cs.mbel.metadata.GenericTable[] row;
	private edu.arizona.cs.mbel.MSILInputStream in;
	/////////////////////////////////////////////
	private TypeDef[] typeDefs = null;
	private TypeRef[] typeRefs = null;
	private TypeSpec[] typeSpecs = null;
	private Method[] methods = null;
	private Field[] fields = null;
	private edu.arizona.cs.mbel.signature.ParameterInfo[] params = null;
	private Property[] properties = null;
	private Event[] events = null;
	private FileReference[] fileReferences = null;
	private ExportedTypeRef[] exportedTypes = null;
	private TypeGroup group = null;
	private ManifestResource[] mresources = null;
	private AssemblyInfo assemblyInfo = null;
	private Module module = null;
	private MemberRef[] memberRefs = null;
	private EntryPoint entryPoint = null;
	private AssemblyRefInfo[] assemblyRefs = null;
	private InterfaceImplementation[] interfaceImpls = null;
	private ModuleRefInfo[] moduleRefs = null;
	private DeclSecurity[] declSecurities = null;
	private edu.arizona.cs.mbel.signature.StandAloneSignature[] standAloneSigs = null;

	/**
	 * Makes a ClassParser that uses the given input stream.
	 * It is assumed that the input stream is open to the beginning of a .NET module file.
	 */
	public ClassParser(java.io.InputStream instream) throws java.io.IOException, edu.arizona.cs.mbel.parse.MSILParseException
	{
		in = new edu.arizona.cs.mbel.MSILInputStream(instream);
		pe_module = new edu.arizona.cs.mbel.parse.PEModule(in);
		tc = pe_module.metadata.parseTableConstants(in);
		tables = tc.getTables();
	}

	/**
	 * Returns the MSILInputStream that this parser is using.
	 * This method is used as a callback in other classes.
	 */
	public edu.arizona.cs.mbel.MSILInputStream getMSILInputStream()
	{
		return in;
	}

	/**
	 * Returns a method signature given the metadata token for a StandAloneSig table
	 */
	public edu.arizona.cs.mbel.signature.MethodSignature getStandAloneSignature(long token)
	{
		long type = (token >> 24) & 0xFFL;
		long tokrow = token & 0xFFFFFFL;

		if(type == tc.StandAloneSig)
		{
			return (edu.arizona.cs.mbel.signature.MethodSignature) standAloneSigs[(int) tokrow - 1];
		}
		return null;
	}

	/**
	 * Returns an implementer of LoadableType given the token from a ldtoken instruction
	 */
	public edu.arizona.cs.mbel.instructions.LoadableType getLoadableType(long token)
	{
		long type = (token >> 24) & 0xFFL;
		long tokrow = (token & 0xFFFFFFL);

		if(type == tc.TypeDef)
		{
			return typeDefs[(int) tokrow - 1];
		}
		else if(type == tc.TypeRef)
		{
			return typeRefs[(int) tokrow - 1];
		}
		else if(type == tc.TypeSpec)
		{
			return typeSpecs[(int) tokrow - 1];
		}
		else if(type == tc.Method)
		{
			return methods[(int) tokrow - 1];
		}
		else if(type == tc.Field)
		{
			return fields[(int) tokrow - 1];
		}
		else if(type == tc.MemberRef)
		{
			return memberRefs[(int) tokrow - 1];
		}
		return null;
	}

	/**
	 * Returns the method corresponding to the given Method token.
	 * To be used by VTableFixups as a callback.
	 */
	public Method getVTableFixup(long methodToken)
	{
		long toktype = (methodToken >> 24) & 0xFFL;
		long tokrow = (methodToken & 0xFFFFFFL);

		if(toktype != tc.Method)
		{
			return null;
		}
		return methods[(int) (tokrow - 1L)];
	}

	/**
	 * Returns the Method or MethodRef corresponding to the given token.
	 * This is called by instructions that deal with methods.
	 */
	public MethodDefOrRef getMethodDefOrRef(long token)
	{
		long type = (token >> 24) & 0xFFL;
		long tokrow = token & 0xFFFFFFL;

		if(type == tc.Method)
		{
			return methods[(int) getMethod(tokrow) - 1];
		}
		else if(type == tc.MemberRef)
		{
			return (MethodDefOrRef) memberRefs[(int) tokrow - 1];
		}
		return null;
	}

	/**
	 * Returns the FieldRef corresponding to the given token.
	 * This is called by instructions that deal with fields.
	 */
	public FieldRef getFieldRef(long token)
	{
		long type = (token >> 24) & 0xFFL;
		long tokrow = (token & 0xFFFFFFL);

		if(type == tc.Field)
		{
			return fields[(int) getField(tokrow) - 1];
		}
		else if(type == tc.MemberRef)
		{
			return (FieldRef) memberRefs[(int) tokrow - 1];
		}
		return null;
	}

	/**
	 * Returns the User String corresponding to the given token.
	 * This is called by LDSTR instructions.
	 */
	public String getUserString(long token)
	{
		long type = (token >> 24) & 0xFFL;
		long tokrow = token & 0xFFFFFFL;
		if(type != tc.USString)
		{
			return null;
		}
		return tc.getUSString(tokrow);
	}

	/**
	 * Returns a local variable list, given the token of a StandAloneSig table
	 */
	public edu.arizona.cs.mbel.signature.LocalVarList getLocalVarList(long token)
	{
		long type = (token >> 24) & 0xFFL;
		long tokrow = (token & 0xFFFFFFL);

		if(type == tc.StandAloneSig)
		{
			return (edu.arizona.cs.mbel.signature.LocalVarList) standAloneSigs[(int) tokrow - 1];
		}
		return null;
	}

	/**
	 * Returns an AbstractTypeReference corresponding to the given token.
	 * This is called by instructions that deal with types.
	 */
	public AbstractTypeReference getClassRef(long token)
	{
		long type = (token >> 24) & 0xFFL;
		long tokrow = token & 0xFFFFFFL;

		if(type == tc.TypeDef)
		{
			return typeDefs[(int) tokrow - 1];
		}
		else if(type == tc.TypeRef)
		{
			return typeRefs[(int) tokrow - 1];
		}
		else if(type == tc.TypeSpec)
		{
			return typeSpecs[(int) tokrow - 1];
		}

		return null;
	}

	/**
	 * Returns the TypeGroup of all defined types in this module (once made)
	 */
	public TypeGroup getTypeGroup()
	{
		return group;
	}

	/**
	 * This is the big workhorse that calls all the other private methods in this class.
	 * It will parse the various structures in the most convenient order possible, all of which
	 * are either accessible from the Module, or are unimportant and discarded.
	 */
	public Module parseModule() throws java.io.IOException, edu.arizona.cs.mbel.parse.MSILParseException
	{
		// REMEMBER!!! ALL TOKEN VALUES ARE 1-BASED INDICES!!!!

		buildModule();
		buildModuleRefs();
		buildAssemblyInfo();
		buildAssemblyRefs();
		buildFileReferences();
		buildManifestResources();
		buildExportedTypes();
		buildTypeDefs();
		buildTypeRefs();
		buildTypeSpecs();
		setSuperClasses();

		group = new TypeGroup(typeDefs, typeRefs, typeSpecs);

		setInterfaceImpls();

		buildFields();
		setFieldLayouts();
		setFieldRVAs();
		if(tables[tc.Param] != null)
		{
			params = new edu.arizona.cs.mbel.signature.ParameterInfo[tables[tc.Param].length];
		}
		buildMethods();
		setImplMaps();
		setDeclSecurity();

		setFieldsAndMethods();

		buildProperties();
		setPropertyMaps();
		setNestedClasses();
		setClassLayouts();
		buildEvents();
		setEventMaps();
		setFieldMarshals();
		setMethodSemantics();
		setDefaultValues();

		buildMemberRefs();
		buildEntryPoint();
		buildStandAloneSigs();
		buildMethodBodies();

		setCustomAttributes();

		buildVTableFixups();

		pe_module.bufferSections(in);

		return module;
	}
   
   /*
   public void output(){
      pe_module.output();
      
      tc.output();
      
      if (module!=null){
         module.output();
         System.out.println();
      }
      if (typeDefs!=null){
         for (int i=0;i<typeDefs.length;i++){
            typeDefs[i].output();
            System.out.println();
         }
      }
      if (typeRefs!=null){
         for (int i=0;i<typeRefs.length;i++){
            typeRefs[i].output();
            System.out.println();
         }
      }
      if (typeSpecs!=null){
         for (int i=0;i<typeSpecs.length;i++){
            typeSpecs[i].output();
            System.out.println();
         }
      }

      if (methods!=null){
         for (int i=0;i<methods.length;i++){
            methods[i].output();
            System.out.println();
            MethodBody body = methods[i].getMethodBody();
            if (body!=null){
               body.output();
               System.out.println();
            }
         }
      }

      if (fields!=null){
         for (int i=0;i<fields.length;i++){
            fields[i].output();
            System.out.println();
         }
      }
      if (properties!=null){
         for (int i=0;i<properties.length;i++){
            properties[i].output();
            System.out.println();
         }
      }
      if (events!=null){
         for (int i=0;i<events.length;i++){
            events[i].output();
            System.out.println();
         }
      }
      if (fileReferences!=null){
         for (int i=0;i<fileReferences.length;i++){
            fileReferences[i].output();
            System.out.println();
         }
      }
      if (exportedTypes!=null){
         for (int i=0;i<exportedTypes.length;i++){
            exportedTypes[i].output();
            System.out.println();
         }
      }
      if (mresources!=null){
         for (int i=0;i<mresources.length;i++){
            mresources[i].output();
            System.out.println();
         }
      }
      if (memberRefs!=null){
         for (int i=0;i<memberRefs.length;i++){
            System.out.print((i+1)+". ");
            if (memberRefs[i]!=null){
               memberRefs[i].output();
               System.out.println();
            }else
               System.out.println("NULL");
         }
      }
   }
   */
	//////////////////////////////////////////////////////////////////////////////////

	private long getMethod(long token)
	{
		// maps tokens through MethodPtrs, if necessary
		if(tables[tc.MethodPtr] != null)
		{
			long newtok = tables[tc.MethodPtr][(int) token - 1].getTableIndex("Method").longValue();
			return newtok;
		}
		else
		{
			return token;
		}
	}

	private long getField(long token)
	{
		if(tables[tc.FieldPtr] != null)
		{
			long newtok = tables[tc.FieldPtr][(int) token - 1].getTableIndex("Field").longValue();
			return newtok;
		}
		else
		{
			return token;
		}
	}

	private long getEvent(long token)
	{
		if(tables[tc.EventPtr] != null)
		{
			long newtok = tables[tc.EventPtr][(int) token - 1].getTableIndex("Event").longValue();
			return newtok;
		}
		else
		{
			return token;
		}
	}

	private long getParam(long token)
	{
		if(tables[tc.ParamPtr] != null)
		{
			long newtok = tables[tc.ParamPtr][(int) token - 1].getTableIndex("Param").longValue();
			return newtok;
		}
		else
		{
			return token;
		}
	}

	private long getProperty(long token)
	{
		if(tables[tc.PropertyPtr] != null)
		{
			long newtok = tables[tc.PropertyPtr][(int) token - 1].getTableIndex("Property").longValue();
			return newtok;
		}
		else
		{
			return token;
		}
	}

	//////////////////////////////////////////////////////////////////

	private void buildAssemblyInfo()
	{
		// build Assembly table (after Module) DONE!
		if(tables[tc.Assembly] != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable ass = tables[tc.Assembly][0];

			long hash = ass.getConstant("HashAlgID").longValue();
			int maj = ass.getConstant("MajorVersion").intValue();
			int min = ass.getConstant("MinorVersion").intValue();
			int bn = ass.getConstant("BuildNumber").intValue();
			int rn = ass.getConstant("RevisionNumber").intValue();
			String name = ass.getString("Name");
			String culture = ass.getString("Culture");
			byte[] publicKey = ass.getBlob("PublicKey");
			long flags = ass.getConstant("Flags").longValue();

			assemblyInfo = new AssemblyInfo(hash, maj, min, bn, rn, flags, publicKey, name, culture);

			module.setAssemblyInfo(assemblyInfo);
		}
	}

	private void buildAssemblyRefs()
	{
		// build AssemblyRef table DONE!
		if(tables[tc.AssemblyRef] != null)
		{
			row = tables[tc.AssemblyRef];
			assemblyRefs = new AssemblyRefInfo[row.length];
			for(int i = 0; i < row.length; i++)
			{
				int Maj = row[i].getConstant("MajorVersion").intValue();
				int Min = row[i].getConstant("MinorVersion").intValue();
				int BN = row[i].getConstant("BuildNumber").intValue();
				int RN = row[i].getConstant("RevisionNumber").intValue();
				long flags = row[i].getConstant("Flags").longValue();
				byte[] pb = row[i].getBlob("PublicKeyOrToken");
				String name = row[i].getString("Name");
				String cult = row[i].getString("Culture");
				byte[] hash = row[i].getBlob("HashValue");

				assemblyRefs[i] = new AssemblyRefInfo(Maj, Min, BN, RN, flags, pb, name, cult, hash);
			}
		}
	}

	private void buildModule()
	{
		// build Module (after Assembly) DONE!
		if(tables[tc.Module] != null)
		{
			edu.arizona.cs.mbel.metadata.GenericTable mod = tables[tc.Module][0];
			String name = mod.getString("Name");
			int generation = mod.getConstant("Generation").intValue();
			byte[] mvid = mod.getGUID("Mvid");
			byte[] encid = mod.getGUID("EncID");
			byte[] encbaseid = mod.getGUID("EncBaseID");

			module = new Module(pe_module, name);
			module.setGeneration(generation);
			module.setMvidGUID(mvid);
			module.setEncIdGUID(encid);
			module.setEncBaseIdGUID(encbaseid);
		}
	}

	private void buildModuleRefs()
	{
		// build ModuleRef tables DONE!
		if(tables[tc.ModuleRef] != null)
		{
			row = tables[tc.ModuleRef];
			moduleRefs = new ModuleRefInfo[row.length];
			for(int i = 0; i < row.length; i++)
			{
				String modName = row[i].getString("Name");
				moduleRefs[i] = new ModuleRefInfo(modName);
			}
		}
	}

	private void buildEntryPoint()
	{
		// build Entrypoint (after Module)
		long entrytoken = pe_module.cliHeader.EntryPointToken;
		long type = (entrytoken >> 24) & 0xFFL;
		long tokrow = (entrytoken & 0xFFFFFFL);

		if(type == tc.Method)
		{
			entryPoint = new EntryPoint(methods[(int) getMethod(tokrow) - 1]);
			module.setEntryPoint(entryPoint);
		}
		else if(type == tc.File)
		{
			entryPoint = new EntryPoint(fileReferences[(int) tokrow - 1]);
			module.setEntryPoint(entryPoint);
		}
	}

	private void buildFileReferences()
	{
		// build Files DONE!
		if(tables[tc.File] != null)
		{
			row = tables[tc.File];
			fileReferences = new FileReference[row.length];
			for(int i = 0; i < row.length; i++)
			{
				long flags = row[i].getConstant("Flags").longValue();
				String name = row[i].getString("Name");
				byte[] hashValue = row[i].getBlob("HashValue");

				fileReferences[i] = new FileReference(flags, name, hashValue);
				module.addFileReference(fileReferences[i]);
			}
		}
	}

	private void buildManifestResources() throws java.io.IOException
	{
		// build ManifestResources (after FileReferences) DONE!
		if(tables[tc.ManifestResource] != null)
		{
			row = tables[tc.ManifestResource];
			mresources = new ManifestResource[row.length];
			for(int i = 0; i < row.length; i++)
			{
				long coded = row[i].getCodedIndex("Implementation").longValue();
				String name = row[i].getString("Name");
				long flags = row[i].getConstant("Flags").longValue();

				if(coded == 0)
				{
					// LocalManifestResource
					long RVA = pe_module.cliHeader.Resources.VirtualAddress;
					long fp = in.getFilePointer(RVA);
					fp += row[i].getConstant("Offset").longValue();
					in.seek(fp);
					long size = in.readDWORD();
					byte[] data = new byte[(int) size];
					in.read(data);
					mresources[i] = new LocalManifestResource(name, flags, data);
				}
				else
				{
					long token[] = tc.parseCodedIndex(coded, tc.Implementation);
					if(token[0] == tc.File)
					{
						// FileManifestResource
						mresources[i] = new FileManifestResource(fileReferences[(int) token[1] - 1], flags);
					}
					else if(token[0] == tc.AssemblyRef)
					{
						// AssemblyManifestResource
						AssemblyManifestResource res = new AssemblyManifestResource(name, flags, assemblyRefs[(int) token[1] - 1]);
						mresources[i] = res;
					}
				}

				module.addManifestResource(mresources[i]);
			}
		}
	}

	private void buildExportedTypes()
	{
		// build ExportedTypes (after File) DONE!

		if(tables[tc.ExportedType] != null)
		{
			row = tables[tc.ExportedType];
			exportedTypes = new ExportedTypeRef[row.length];
			for(int i = 0; i < row.length; i++)
			{
				String ns = row[i].getString("TypeNamespace");
				String name = row[i].getString("TypeName");
				long flags = row[i].getConstant("Flags").longValue();

				exportedTypes[i] = new ExportedTypeRef(ns, name, flags);
				if(assemblyInfo != null)
				{
					assemblyInfo.addExportedType(exportedTypes[i]);
				}
			}
			for(int i = 0; i < row.length; i++)
			{
				long coded = row[i].getCodedIndex("Implementation").longValue();
				long[] token = tc.parseCodedIndex(coded, tc.Implementation);
				if(token[0] == tc.ExportedType)
				{
					exportedTypes[i].setExportedTypeRef(exportedTypes[(int) token[1] - 1]);
				}
				else
				{
					exportedTypes[i].setFileReference(fileReferences[(int) token[1] - 1]);
				}
			}
		}
	}

	private void buildFields()
	{
		// build Fields (after TypeGroup) DONE!
		if(tables[tc.Field] != null)
		{
			row = tables[tc.Field];
			fields = new Field[tables[tc.Field].length];
			for(int i = 0; i < row.length; i++)
			{
				int Flags = row[i].getConstant("Flags").intValue();
				String name = row[i].getString("Name");
				byte[] blob = row[i].getBlob("Signature");
				edu.arizona.cs.mbel.signature.FieldSignature sig = edu.arizona.cs.mbel.signature.FieldSignature.parse(new edu.arizona.cs.mbel.ByteBuffer(blob),
						group);
				fields[i] = new Field(name, sig);
				fields[i].setFlags(Flags);
				// does not set parent!
			}
			// decorate with FieldLayout, FieldMarshal, FieldRVA
		}
	}

	private void setFieldLayouts()
	{
		// build FieldLayouts (after Fields) DONE!
		if(tables[tc.FieldLayout] != null)
		{
			row = tables[tc.FieldLayout];
			for(int i = 0; i < row.length; i++)
			{
				long field = getField(row[i].getTableIndex("Field").longValue());
				long Offset = row[i].getConstant("Offset").longValue();
				fields[(int) field - 1].setOffset(Offset);
			}
		}
	}

	private void buildMethods()
	{
		// build Methods (after Params and TypeGroup)
		if(tables[tc.Method] != null)
		{
			row = tables[tc.Method];
			methods = new Method[row.length];
			for(int i = 0; i < row.length; i++)
			{
				long RVA = row[i].getConstant("RVA").longValue();
				String name = row[i].getString("Name");
				int implFlags = row[i].getConstant("ImplFlags").intValue();
				int flags = row[i].getConstant("Flags").intValue();
				byte[] blob = row[i].getBlob("Signature");

				edu.arizona.cs.mbel.signature.MethodSignature sig = edu.arizona.cs.mbel.signature.MethodSignature.parse(new edu.arizona.cs.mbel.ByteBuffer(blob)
						, group);

				methods[i] = new Method(name, implFlags, flags, sig);

				if((RVA != 0) && (implFlags & Method.CodeTypeMask) == Method.Native)
				{
					methods[i].setMethodRVA(RVA);
				}
			}

			// add params DONE!
			for(int i = 0; i < row.length; i++)
			{
				edu.arizona.cs.mbel.signature.ParameterSignature[] pSigs = methods[i].getSignature().getParameters();

				long startI = row[i].getTableIndex("ParamList").longValue();
				if(!(startI == 0 || tables[tc.Param] == null || startI > tables[tc.Param].length))
				{
					// valid start of paramlist
					long endI = tables[tc.Param].length + 1;
					if(i < row.length - 1)
					{
						endI = Math.min(endI, row[i + 1].getTableIndex("ParamList").longValue());
					}

					int count = 0;

					for(long j = startI; j < endI; j++)
					{
						edu.arizona.cs.mbel.metadata.GenericTable paramTable = tables[tc.Param][(int) getParam(j) - 1];
						int flags = paramTable.getConstant("Flags").intValue();
						int seq = paramTable.getConstant("Sequence").intValue();
						String name = paramTable.getString("Name");
						if(seq == 0)
						{
							params[(int) getParam(j) - 1] = new edu.arizona.cs.mbel.signature.ParameterInfo(name, flags);
							methods[i].getSignature().getReturnType().setParameterInfo(params[(int) getParam(j) - 1]);
						}
						else
						{
							params[(int) getParam(j) - 1] = new edu.arizona.cs.mbel.signature.ParameterInfo(name, flags);
							pSigs[seq - 1].setParameterInfo(params[(int) getParam(j) - 1]);
						}
					}
				}
			}
		}
	}

	private void setImplMaps()
	{
		// build ImplMaps (after Methods) DONE!
		if(tables[tc.ImplMap] != null)
		{
			row = tables[tc.ImplMap];
			for(int i = 0; i < row.length; i++)
			{
				long coded = row[i].getCodedIndex("MemberForwarded").longValue();
				long token[] = tc.parseCodedIndex(coded, tc.MemberForwarded);
				if(token[0] != tc.Method)
				{
					continue;
				}
				long method = getMethod(token[1]);

				int flags = row[i].getConstant("MappingFlags").intValue();
				String name = row[i].getString("ImportName");
				long modref = row[i].getTableIndex("ImportScope").longValue();

				methods[(int) method - 1].setImplementationMap(new ImplementationMap(flags, name, moduleRefs[(int) modref - 1]));
			}
		}
	}

	private void setDeclSecurity()
	{
		// build DeclSecurity (after Assembly, Method, and TypeDefs) DONE!
		if(tables[tc.DeclSecurity] != null)
		{
			row = tables[tc.DeclSecurity];
			declSecurities = new DeclSecurity[row.length];
			for(int i = 0; i < row.length; i++)
			{
				long coded = row[i].getCodedIndex("Parent").longValue();
				long token[] = tc.parseCodedIndex(coded, tc.HasDeclSecurity);
				int Action = row[i].getConstant("Action").intValue();
				byte[] permission = row[i].getBlob("PermissionSet");
				declSecurities[i] = new DeclSecurity(Action, permission);

				if(token[0] == tc.TypeDef)
				{
					typeDefs[(int) token[1] - 1].setDeclSecurity(declSecurities[i]);
				}
				else if(token[0] == tc.Method)
				{
					methods[(int) getMethod(token[1]) - 1].setDeclSecurity(declSecurities[i]);
				}
				else if(token[0] == tc.Assembly)
				{
					assemblyInfo.setDeclSecurity(declSecurities[i]);
				}
			}
		}
	}


	private void buildTypeDefs()
	{
		// build TypeDefs (after Field and Methods) DONE!
		if(tables[tc.TypeDef] != null)
		{
			row = tables[tc.TypeDef];
			typeDefs = new TypeDef[row.length];
			for(int i = 0; i < row.length; i++)
			{
				String name = row[i].getString("Name");
				String ns = row[i].getString("Namespace");
				long flags = row[i].getConstant("Flags").longValue();

				typeDefs[i] = new TypeDef(ns, name, flags);
				module.addTypeDef(typeDefs[i]);
			}
			// still need to add fields and methods, and extends field
		}
	}

	private void setFieldsAndMethods()
	{
		// set parents of the fields and methods (after Typedef, Method, Field) DONE!
		if(tables[tc.TypeDef] != null)
		{
			row = tables[tc.TypeDef];

			for(int i = 0; i < row.length; i++)
			{
				long fieldS = row[i].getTableIndex("FieldList").longValue();
				if(!(fieldS == 0 || tables[tc.Field] == null || fieldS > tables[tc.Field].length))
				{
					long fieldE = fields.length + 1;
					if(i < row.length - 1)
					{
						fieldE = Math.min(fieldE, row[i + 1].getTableIndex("FieldList").longValue());
					}

					for(long j = fieldS; j < fieldE; j++)
					{
						typeDefs[i].addField(fields[(int) getField(j) - 1]);
					}
					// this sets the field parents
				}

				long methodS = row[i].getTableIndex("MethodList").longValue();
				if(!(methodS == 0 || tables[tc.Method] == null || methodS > tables[tc.Method].length))
				{
					long methodE = methods.length + 1;
					if(i < row.length - 1)
					{
						methodE = Math.min(methodE, row[i + 1].getTableIndex("MethodList").longValue());
					}

					for(long j = methodS; j < methodE; j++)
					{
						typeDefs[i].addMethod(methods[(int) getMethod(j) - 1]);
					}
					// this sets the method parents
				}
			}
		}
	}

	private void buildTypeRefs()
	{
		// build TypeRefs (after TypeDefs and ExportedTypes) DONE!
		if(tables[tc.TypeRef] != null)
		{
			row = tables[tc.TypeRef];
			typeRefs = new TypeRef[row.length];
			for(int i = 0; i < row.length; i++)
			{
				long coded = row[i].getCodedIndex("ResolutionScope").longValue();

				if(coded == 0L)
				{
					// ExportedType
					String Name = row[i].getString("Name");
					String Namespace = row[i].getString("Namespace");

					for(int j = 0; j < exportedTypes.length; j++)
					{
						if(Name.equals(exportedTypes[j].getName()) && Namespace.equals(exportedTypes[j].getNamespace()))
						{
							typeRefs[i] = exportedTypes[j];
						}
					}
					continue;
				}

				long[] token = tc.parseCodedIndex(coded, tc.ResolutionScope);
				String Namespace = row[i].getString("Namespace");
				String Name = row[i].getString("Name");

				switch((int) token[0])
				{
					case edu.arizona.cs.mbel.metadata.TableConstants.ModuleRef:
					{
						ModuleTypeRef mod = new ModuleTypeRef(moduleRefs[(int) token[1] - 1], Namespace, Name);
						typeRefs[i] = mod;
						break;
					}

					case edu.arizona.cs.mbel.metadata.TableConstants.TypeRef:
					{
						NestedTypeRef nest = new NestedTypeRef(Namespace, Name, typeRefs[(int) token[1] - 1]);
						typeRefs[i] = nest;
						break;
					}

					case edu.arizona.cs.mbel.metadata.TableConstants.AssemblyRef:
					{
						AssemblyTypeRef assem = new AssemblyTypeRef(assemblyRefs[(int) token[1] - 1], Namespace, Name);
						typeRefs[i] = assem;
						break;
					}

					case edu.arizona.cs.mbel.metadata.TableConstants.Module:
					{
						// (Implementation == 0x4?)
						// search through TypeDefs for Name and Namespace
						for(int j = 0; j < typeDefs.length; j++)
						{
							if(typeDefs[j].getName().equals(Name) && typeDefs[j].getNamespace().equals(Namespace))
							{
								typeRefs[i] = typeDefs[j];
							}
						}
						break;
					}
				}
			}
		}
	}

	private void buildTypeSpecs()
	{
		// build TypeSpecs (after TypeDef and TypeRef) DONE!
		if(tables[tc.TypeSpec] != null)
		{
			row = tables[tc.TypeSpec];
			typeSpecs = new TypeSpec[row.length];
			for(int i = 0; i < row.length; i++)
			{
				typeSpecs[i] = new TypeSpec();
			}

			byte[] blob = null;
			edu.arizona.cs.mbel.signature.TypeSpecSignature sig = null;
			for(int i = 0; i < row.length; i++)
			{
				blob = row[i].getBlob("Signature");
				sig = (edu.arizona.cs.mbel.signature.TypeSpecSignature) edu.arizona.cs.mbel.signature.TypeSpecSignature.parse(new edu.arizona.cs.mbel.ByteBuffer
						(blob), new TypeGroup(typeDefs, typeRefs, typeSpecs));
				typeSpecs[i].setSignature(sig);
				//module.addTypeSpec(typeSpecs[i]);
			}
		}
	}

	private void setSuperClasses()
	{
		// fill in Typedef extends (after TypeRef and TypeDef) DONE!
		if(tables[tc.TypeDef] != null)
		{
			row = tables[tc.TypeDef];
			for(int i = 0; i < row.length; i++)
			{
				long coded = row[i].getCodedIndex("Extends").longValue();
				if(coded != 0L)
				{
					long[] token = tc.parseCodedIndex(coded, tc.TypeDefOrRef);
					if(token[0] == tc.TypeDef)
					{
						typeDefs[i].setSuperClass(typeDefs[(int) token[1] - 1]);
					}
					else
					{
						typeDefs[i].setSuperClass(typeRefs[(int) token[1] - 1]);
					}
				}
			}
		}
	}

	private void setInterfaceImpls()
	{
		// build InterfaceImpls (after TypeGroup) DONE!
		if(tables[tc.InterfaceImpl] != null)
		{
			row = tables[tc.InterfaceImpl];
			interfaceImpls = new InterfaceImplementation[row.length];
			for(int i = 0; i < row.length; i++)
			{
				long clazz = row[i].getTableIndex("Class").longValue();
				TypeDef def = typeDefs[(int) clazz - 1];
				long coded = row[i].getCodedIndex("Interface").longValue();
				long inter[] = tc.parseCodedIndex(coded, tc.TypeDefOrRef);

				if(inter[0] == tc.TypeDef)
				{
					interfaceImpls[i] = new InterfaceImplementation(typeDefs[(int) inter[1] - 1]);
				}
				else if(inter[0] == tc.TypeRef)
				{
					interfaceImpls[i] = new InterfaceImplementation(typeRefs[(int) inter[1] - 1]);
				}
				def.addInterface(interfaceImpls[i]);
			}
		}
	}

	private void buildProperties()
	{
		// build Properties DONE!
		if(tables[tc.Property] != null)
		{
			row = tables[tc.Property];
			properties = new Property[row.length];
			for(int i = 0; i < row.length; i++)
			{
				String name = row[i].getString("Name");
				int flags = row[i].getConstant("Flags").intValue();
				byte[] blob = row[i].getBlob("Type");
				edu.arizona.cs.mbel.signature.PropertySignature sig = edu.arizona.cs.mbel.signature.PropertySignature.parse(new edu.arizona.cs.mbel.ByteBuffer
						(blob), group);

				properties[i] = new Property(name, flags, sig);
			}
		}
	}

	private void setPropertyMaps()
	{
		// build PropertyMap (after TypeDefs and Property) DONE!
		if(tables[tc.PropertyMap] != null)
		{
			row = tables[tc.PropertyMap];
			for(int i = 0; i < row.length; i++)
			{
				long parent = row[i].getTableIndex("Parent").longValue();
				if(parent == 0)
				{
					continue;
				}
				long propS = row[i].getTableIndex("PropertyList").longValue();
				if(propS == 0 || tables[tc.Property] == null || propS > tables[tc.Property].length)
				{
					continue;
				}
				long propE = properties.length + 1;
				if(i < row.length - 1)
				{
					propE = Math.min(propE, row[i + 1].getTableIndex("PropertyList").longValue());
				}
				for(long j = propS; j < propE; j++)
				{
					typeDefs[(int) parent - 1].addProperty(properties[(int) getProperty(j) - 1]);
				}
			}
		}
	}

	private void setNestedClasses()
	{
		// build NestedClasses (after TypeDefs) DONE!
		if(tables[tc.NestedClass] != null)
		{
			row = tables[tc.NestedClass];
			for(int i = 0; i < row.length; i++)
			{
				long nest = row[i].getTableIndex("NestedClass").longValue();
				long enclose = row[i].getTableIndex("EnclosingClass").longValue();
				typeDefs[(int) enclose - 1].addNestedClass(typeDefs[(int) nest - 1]);
			}
		}
	}

	private void setClassLayouts()
	{
		// build ClassLayouts (after TypeDefs) DONE!
		if(tables[tc.ClassLayout] != null)
		{
			row = tables[tc.ClassLayout];
			for(int i = 0; i < row.length; i++)
			{
				long typedef = row[i].getTableIndex("Parent").longValue();
				int pSize = row[i].getConstant("PackingSize").intValue();
				long cSize = row[i].getConstant("ClassSize").longValue();

				ClassLayout layout = new ClassLayout(pSize, cSize);
				typeDefs[(int) typedef - 1].setClassLayout(layout);
			}
		}
	}

	private void setFieldRVAs()
	{
		// build FieldRVAs (after Fields)
		if(tables[tc.FieldRVA] != null)
		{
			row = tables[tc.FieldRVA];
			for(int i = 0; i < row.length; i++)
			{
				long RVA = row[i].getConstant("RVA").longValue();
				long field = getField(row[i].getTableIndex("Field").longValue());
				fields[(int) field - 1].setFieldRVA(RVA);
			}
		}
	}

	private void buildEvents()
	{
		// build Events (after TypeDef and TypeRef) DONE!
		if(tables[tc.Event] != null)
		{
			row = tables[tc.Event];
			events = new Event[row.length];
			for(int i = 0; i < row.length; i++)
			{
				String name = row[i].getString("Name");
				int flags = row[i].getConstant("EventFlags").intValue();

				TypeRef handler = null;
				long coded = row[i].getCodedIndex("EventType").longValue();
				long[] token = tc.parseCodedIndex(coded, tc.TypeDefOrRef);
				if(token[0] == tc.TypeDef)
				{
					handler = typeDefs[(int) token[1] - 1];
				}
				else
				{
					handler = typeRefs[(int) token[1] - 1];
				}

				events[i] = new Event(name, flags, handler);
			}
		}
	}

	private void setEventMaps()
	{
		// build EventMaps (after Event) DONE!
		if(tables[tc.EventMap] != null)
		{
			row = tables[tc.EventMap];
			for(int i = 0; i < row.length; i++)
			{
				long parent = row[i].getTableIndex("Parent").longValue();
				long eventS = row[i].getTableIndex("EventList").longValue();
				long eventE = events.length + 1;
				if(i < row.length - 1)
				{
					eventE = Math.min(eventE, row[i + 1].getTableIndex("EventList").longValue());
				}
				for(long j = eventS; j < eventE; j++)
				{
					typeDefs[(int) parent - 1].addEvent(events[(int) getEvent(j) - 1]);
				}
			}
		}
	}

	private void setFieldMarshals()
	{
		// build FieldMarshals (after Field and Method) DONE!
		if(tables[tc.FieldMarshal] != null)
		{
			row = tables[tc.FieldMarshal];
			for(int i = 0; i < row.length; i++)
			{
				long[] index = tc.parseCodedIndex(row[i].getCodedIndex("Parent").longValue(), tc.HasFieldMarshal);
				byte[] blob = row[i].getBlob("NativeType");
				edu.arizona.cs.mbel.signature.MarshalSignature sig = edu.arizona.cs.mbel.signature.MarshalSignature.parse(new edu.arizona.cs.mbel.ByteBuffer
						(blob));

				if(index[0] == tc.Field) // Field
				{
					fields[(int) getField(index[1]) - 1].setFieldMarshal(sig);
				}
				else // Param
				{
					params[(int) getParam(index[1]) - 1].setFieldMarshal(sig);
				}
			}
		}
	}

	private void setMethodSemantics()
	{
		// build MethodSemantics (after Method, Event, Property) DONE!
		if(tables[tc.MethodSemantics] != null)
		{
			row = tables[tc.MethodSemantics];
			for(int i = 0; i < row.length; i++)
			{
				long method = getMethod(row[i].getTableIndex("Method").longValue());
				int sem = row[i].getConstant("Semantics").intValue();
				long coded = row[i].getCodedIndex("Association").longValue();
				long token[] = tc.parseCodedIndex(coded, tc.HasSemantics);
				Method meth = methods[(int) method - 1];

				if(token[0] == tc.Event)
				{
					Event event = events[(int) getEvent(token[1]) - 1];
					meth.setMethodSemantics(new MethodSemantics(sem, event));
					if(sem == MethodSemantics.AddOn)
					{
						event.setAddOnMethod(meth);
					}
					else if(sem == MethodSemantics.RemoveOn)
					{
						event.setRemoveOnMethod(meth);
					}
					else if(sem == MethodSemantics.Fire)
					{
						event.setFireMethod(meth);
					}
				}
				else if(token[0] == tc.Property)
				{
					Property prop = properties[(int) getProperty(token[1]) - 1];
					meth.setMethodSemantics(new MethodSemantics(sem, prop));
					if(sem == MethodSemantics.Getter)
					{
						prop.setGetter(meth);
					}
					else if(sem == MethodSemantics.Setter)
					{
						prop.setSetter(meth);
					}
				}
			}
		}
	}

	private void setDefaultValues()
	{
		// build Constants (after Field, Property, Param) DONE!
		if(tables[tc.Constant] != null)
		{
			row = tables[tc.Constant];
			for(int i = 0; i < row.length; i++)
			{
				byte[] blob = row[i].getBlob("Value");
				long coded = row[i].getCodedIndex("Parent").longValue();
				long token[] = tc.parseCodedIndex(coded, tc.HasConst);
				if(token[0] == tc.Field)
				{
					fields[(int) getField(token[1]) - 1].setDefaultValue(blob);
				}
				else if(token[0] == tc.Param)
				{
					params[(int) getParam(token[1]) - 1].setDefaultValue(blob);
				}
				else if(token[0] == tc.Property)
				{
					properties[(int) getProperty(token[1]) - 1].setDefaultValue(blob);
				}
			}
		}
	}

	private void buildMemberRefs()
	{
		// build MemberRefs (after TypeGroup, Method, Field)
		if(tables[tc.MemberRef] != null)
		{
			row = tables[tc.MemberRef];
			memberRefs = new MemberRef[row.length];
			for(int i = 0; i < row.length; i++)
			{
				byte[] blob = row[i].getBlob("Signature");
				if((blob[0] & 0x0F) == edu.arizona.cs.mbel.signature.CallingConvention.FIELD)
				{
					// FIELDREF
					long coded = row[i].getCodedIndex("Class").longValue();
					long newtok[] = tc.parseCodedIndex(coded, tc.MemberRefParent);
					String name = row[i].getString("Name");
					edu.arizona.cs.mbel.signature.FieldSignature sig = edu.arizona.cs.mbel.signature.FieldSignature.parse(new edu.arizona.cs.mbel.ByteBuffer(blob),
							group);

					if(newtok[0] == tc.TypeRef)
					{
						memberRefs[i] = new FieldRef(name, sig, typeRefs[(int) newtok[1] - 1]);
					}
					else if(newtok[0] == tc.ModuleRef)
					{
						memberRefs[i] = new GlobalFieldRef(moduleRefs[(int) newtok[1] - 1], name, sig);
					}
					else if(newtok[0] == tc.TypeSpec)
					{
						memberRefs[i] = new FieldRef(name, sig, typeSpecs[(int) newtok[1] - 1]);
					}
					else if(newtok[0] == tc.TypeDef)
					{
						memberRefs[i] = new FieldRef(name, sig, typeDefs[(int) newtok[1] - 1]);
					}
				}
				else
				{
					// METHODREF
					long coded = row[i].getCodedIndex("Class").longValue();
					long newtok[] = tc.parseCodedIndex(coded, tc.MemberRefParent);
					String name = row[i].getString("Name");

					edu.arizona.cs.mbel.signature.MethodSignature callsig = edu.arizona.cs.mbel.signature.MethodSignature.parse(new edu.arizona.cs.mbel.ByteBuffer
							(blob), group);

					if(newtok[0] == tc.TypeRef)
					{
						memberRefs[i] = new MethodRef(name, typeRefs[(int) newtok[1] - 1], callsig);
					}
					else if(newtok[0] == tc.ModuleRef)
					{
						memberRefs[i] = new GlobalMethodRef(moduleRefs[(int) newtok[1] - 1], name, callsig);
					}
					else if(newtok[0] == tc.Method)
					{
						memberRefs[i] = new VarargsMethodRef(methods[(int) getMethod(newtok[1]) - 1], callsig);
					}
					else if(newtok[0] == tc.TypeSpec)
					{
						memberRefs[i] = new MethodRef(name, typeSpecs[(int) newtok[1] - 1], callsig);
					}
					else if(newtok[0] == tc.TypeDef)
					{
						memberRefs[i] = new MethodRef(name, typeDefs[(int) newtok[1] - 1], callsig);
					}
				}
			}
		}
	}

	private void setMethodMaps()
	{
		// build MethodImpls (after TypeGroup, MemberRef, Method)
		if(tables[tc.MethodImpl] != null)
		{
			row = tables[tc.MethodImpl];
			for(int i = 0; i < row.length; i++)
			{
				long typedef = row[i].getTableIndex("Class").longValue();
				long coded = row[i].getCodedIndex("MethodDeclaration").longValue();
				long decltoken[] = tc.parseCodedIndex(coded, tc.MethodDefOrRef);
				coded = row[i].getCodedIndex("MethodBody").longValue();
				long bodytoken[] = tc.parseCodedIndex(coded, tc.MethodDefOrRef);

				MethodDefOrRef body = null, decl = null;

				if(bodytoken[0] == tc.Method)
				{
					// Method
					body = methods[(int) getMethod(bodytoken[1]) - 1];
				}
				else
				{
					// MemberRef
					body = (MethodDefOrRef) memberRefs[(int) bodytoken[1] - 1];
				}

				if(decltoken[0] == tc.Method)
				{
					// Method
					decl = methods[(int) getMethod(decltoken[1]) - 1];
				}
				else
				{
					// MemberRef
					decl = (MethodDefOrRef) memberRefs[(int) decltoken[1] - 1];
				}

				MethodMap map = new MethodMap(decl, body);
				TypeDef def = typeDefs[(int) typedef - 1];
				def.addMethodMap(map);
			}
		}
	}

	private void buildStandAloneSigs()
	{
		// build StandAloneSig table DONE!
		if(tables[tc.StandAloneSig] != null)
		{
			row = tables[tc.StandAloneSig];
			standAloneSigs = new edu.arizona.cs.mbel.signature.StandAloneSignature[row.length];
			for(int i = 0; i < row.length; i++)
			{
				byte[] blob = row[i].getBlob("Signature");
				if((blob[0] & 0x0F) == edu.arizona.cs.mbel.signature.CallingConvention.LOCAL_SIG)
				{
					// LocalVarList
					standAloneSigs[i] = edu.arizona.cs.mbel.signature.LocalVarList.parse(new edu.arizona.cs.mbel.ByteBuffer(blob), group);
				}
				else if((blob[0] & 0x0F) == edu.arizona.cs.mbel.signature.CallingConvention.FIELD)
				{
					// field
					standAloneSigs[i] = edu.arizona.cs.mbel.signature.FieldSignature.parse(new edu.arizona.cs.mbel.ByteBuffer(blob), group);
				}
				else
				{
					// MethodSignature
					standAloneSigs[i] = edu.arizona.cs.mbel.signature.MethodSignature.parse(new edu.arizona.cs.mbel.ByteBuffer(blob), group);
				}
			}
		}
	}

	private void buildMethodBodies() throws java.io.IOException, edu.arizona.cs.mbel.parse.MSILParseException
	{
		// build method bodies (last!)
		if(tables[tc.Method] != null)
		{
			row = tables[tc.Method];
			for(int i = 0; i < row.length; i++)
			{
				long implflags = row[i].getConstant("ImplFlags").intValue();
				long RVA = row[i].getConstant("RVA").longValue();
				if(RVA != 0 && (implflags & Method.CodeTypeMask) == Method.IL)
				{
					in.seek(in.getFilePointer(RVA));
					MethodBody body = new MethodBody(this);
					methods[i].setMethodBody(body);
				}
			}
		}
	}

	private void setCustomAttributes()
	{
		// build CustomAttribute table
		if(tables[tc.CustomAttribute] != null)
		{
			row = tables[tc.CustomAttribute];
			for(int i = 0; i < row.length; i++)
			{
				byte[] blob = row[i].getBlob("Value");
				long coded = row[i].getCodedIndex("Type").longValue();
				long[] token = tc.parseCodedIndex(coded, tc.CustomAttributeType);

				CustomAttribute ca = null;

				if(token[0] == tc.Method)
				{
					ca = new CustomAttribute(blob, methods[(int) getMethod(token[1]) - 1]);
				}
				else if(token[0] == tc.MemberRef)
				{
					ca = new CustomAttribute(blob, (MethodDefOrRef) memberRefs[(int) token[1] - 1]);
				}

				coded = row[i].getCodedIndex("Parent").longValue();
				token = tc.parseCodedIndex(coded, tc.HasCustomAttribute);

				if(token[0] == tc.Method)
				{
					methods[(int) getMethod(token[1]) - 1].addMethodAttribute(ca);
				}
				else if(token[0] == tc.Field)
				{
					fields[(int) getField(token[1]) - 1].addFieldAttribute(ca);
				}
				else if(token[0] == tc.TypeRef)
				{
					typeRefs[(int) token[1] - 1].addTypeRefAttribute(ca);
				}
				else if(token[0] == tc.TypeDef)
				{
					typeDefs[(int) token[1] - 1].addTypeDefAttribute(ca);
				}
				else if(token[0] == tc.Param)
				{
					params[(int) getParam(token[1]) - 1].addParamAttribute(ca);
				}
				else if(token[0] == tc.InterfaceImpl)
				{
					interfaceImpls[(int) token[1] - 1].addInterfaceImplAttribute(ca);
				}
				else if(token[0] == tc.MemberRef)
				{
					memberRefs[(int) token[1] - 1].addMemberRefAttribute(ca);
				}
				else if(token[0] == tc.Module)
				{
					module.addModuleAttribute(ca);
				}
				else if(token[0] == tc.DeclSecurity)
				{
					declSecurities[(int) token[1] - 1].addDeclSecurityAttribute(ca);
				}
				else if(token[0] == tc.Property)
				{
					properties[(int) getProperty(token[1]) - 1].addPropertyAttribute(ca);
				}
				else if(token[0] == tc.Event)
				{
					events[(int) getEvent(token[1]) - 1].addEventAttribute(ca);
				}
				else if(token[0] == tc.StandAloneSig)
				{
					standAloneSigs[(int) token[1] - 1].addStandAloneSigAttribute(ca);
				}
				else if(token[0] == tc.ModuleRef)
				{
					moduleRefs[(int) token[1] - 1].addModuleRefAttribute(ca);
				}
				else if(token[0] == tc.TypeSpec)
				{
					typeSpecs[(int) token[1] - 1].addTypeSpecAttribute(ca);
				}
				else if(token[0] == tc.Assembly)
				{
					assemblyInfo.addAssemblyAttribute(ca);
				}
				else if(token[0] == tc.AssemblyRef)
				{
					assemblyRefs[(int) token[1] - 1].addAssemblyRefAttribute(ca);
				}
				else if(token[0] == tc.File)
				{
					fileReferences[(int) token[1] - 1].addFileAttribute(ca);
				}
				else if(token[0] == tc.ExportedType)
				{
					exportedTypes[(int) token[1] - 1].addExportedTypeAttribute(ca);
				}
				else if(token[0] == tc.ManifestResource)
				{
					mresources[(int) token[1] - 1].addManifestResourceAttribute(ca);
				}
			}
		}
	}

	private void buildVTableFixups() throws java.io.IOException
	{
		long VirtualAddress = pe_module.cliHeader.VTableFixups.VirtualAddress;
		long Size = pe_module.cliHeader.VTableFixups.Size;

		// parse VTableFixups
		if(VirtualAddress != 0 && Size != 0)
		{
			long length = Size / VTableFixup.STRUCT_SIZE;
			long fp = in.getFilePointer(VirtualAddress);
			VTableFixup temp = null;
			for(int i = 0; i < length; i++)
			{
				in.seek(fp + i * VTableFixup.STRUCT_SIZE);
				temp = new VTableFixup(this);
				module.addVTableFixup(temp);
			}
		}
	}
}
