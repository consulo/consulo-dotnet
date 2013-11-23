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

package edu.arizona.cs.mbel.metadata;

/**
 * This class contains parsing methods and constants for dealing with metadata tables.
 * Reference: ECMA Spec, PartitionII, page 21.
 * NOTE!!! Metadata token row values can be equal to rowcount+1 for some reason.
 * A few words about metadata table indexes (RIDs):
 * All indexes are 1-based.
 * <p/>
 * Coded indexes:
 * There will be a list of possible flag values, say n of them.
 * Round n to the next higher power of 2, say 2^k (= 2^ceil(lg(n)) ).
 * The lower k bits of the coded index will be the flag determining
 * which of the tables to index into. The upper bits will be the
 * index itself. If any of the tables that the lower k bits could potentially
 * index into contains more than 2^16 - 2^k entries (i.e. the maximum index could
 * not be expressed in 2 bytes) then the index will be 4 bytes long.
 * Example: Suppose that you have a TypeDefOrRef coded index with value 0x0025.
 * TypeDefOrRef indexes have 2 bits for the table type, and the bottom
 * 2 bits of this value are 01, which corresponds to the TypeRef table.
 * The upper 14 bits are 00000000001001, which is the value 9, which
 * means that this token refers to row 9 of the TypeRef table.
 * <p/>
 * Table indexes are 2 bytes if the table has < 2^16 entries in it, 4 otherwise
 *
 * @author Michael Stepp
 */
public class TableConstants
{
	// class full of various constants that these tables will need
	// also contains references to the heaps, and contains the metadata tables
	// this is the uber-class that gets passed around whenever you want
	// to play with the metadata (sort of misnamed now, not just constants)


	// indices for coded index types (to be used
	// in the arrays and methods also defined in this class)
	public static final int TypeDefOrRef = 0;
	public static final int HasConst = 1;
	public static final int HasCustomAttribute = 2;
	public static final int HasFieldMarshal = 3;
	public static final int HasDeclSecurity = 4;
	public static final int MemberRefParent = 5;
	public static final int HasSemantics = 6;
	public static final int MethodDefOrRef = 7;
	public static final int MemberForwarded = 8;
	public static final int Implementation = 9;
	public static final int CustomAttributeType = 10;
	public static final int ResolutionScope = 11;
	// indices for the heaps
	public static final int StringsHeap = 0;
	public static final int GUIDHeap = 1;
	public static final int BlobHeap = 2;
	// indices for each table, these correspond to the
	// actual table numbers as defined by ECMA
	public static final int USString = 0x70;
	public static final int Assembly = 0x20;
	public static final int AssemblyOS = 0x22;
	public static final int AssemblyProcessor = 0x21;
	public static final int AssemblyRef = 0x23;
	public static final int AssemblyRefOS = 0x25;
	public static final int AssemblyRefProcessor = 0x24;
	public static final int ClassLayout = 0x0F;
	public static final int Constant = 0x0B;
	public static final int CustomAttribute = 0x0C;
	public static final int DeclSecurity = 0x0E;
	public static final int ENCLog = 0x1E;
	public static final int ENCMap = 0x1F;
	public static final int EventMap = 0x12;
	public static final int Event = 0x14;
	public static final int EventPtr = 0x13;
	public static final int ExportedType = 0x27;
	public static final int Field = 0x04;
	public static final int FieldLayout = 0x10;
	public static final int FieldMarshal = 0x0D;
	public static final int FieldPtr = 0x03;
	public static final int FieldRVA = 0x1D;
	public static final int File = 0x26;
	public static final int ImplMap = 0x1C;
	public static final int InterfaceImpl = 0x09;
	public static final int ManifestResource = 0x28;
	public static final int MemberRef = 0x0A;
	public static final int Method = 0x06;
	public static final int MethodImpl = 0x19;
	public static final int MethodPtr = 0x05;
	public static final int MethodSemantics = 0x18;
	public static final int Module = 0x00;
	public static final int ModuleRef = 0x1A;
	public static final int NestedClass = 0x29;
	public static final int Param = 0x08;
	public static final int ParamPtr = 0x07;
	public static final int Property = 0x17;
	public static final int PropertyMap = 0x15;
	public static final int PropertyPtr = 0x16;
	public static final int StandAloneSig = 0x11;
	public static final int TypeDef = 0x02;
	public static final int TypeRef = 0x01;
	public static final int TypeSpec = 0x1B;

	public static final String[] GRAMMAR = new String[64];

	static
	{
		GRAMMAR[Assembly] = "Assembly:HashAlgID=4,MajorVersion=2,MinorVersion=2,BuildNumber=2,RevisionNumber=2,Flags=4,PublicKey=B,Name=S,Culture=S";
		GRAMMAR[AssemblyOS] = "AssemblyOS:OSPlatformID=4,OSMajorVersion=4,OSMinorVersion=4";
		GRAMMAR[AssemblyProcessor] = "AssemblyProcessor:Processor=4";
		GRAMMAR[AssemblyRef] = "AssemblyRef:MajorVersion=2,MinorVersion=2,BuildNumber=2,RevisionNumber=2,Flags=4,PublicKeyOrToken=B,Name=S,Culture=S," +
				"HashValue=B";
		GRAMMAR[AssemblyRefOS] = "AssemblyRefOS:OSPlatformID=4,OSMajorVersion=4,OSMinorVersion=4,AssemblyRef=T|" + AssemblyRef;
		GRAMMAR[AssemblyRefProcessor] = "AssemblyRefProcessor:Processor=4,AssemblyRef=T|" + AssemblyRef;
		GRAMMAR[ClassLayout] = "ClassLayout:PackingSize=2,ClassSize=4,Parent=T|" + TypeDef;
		GRAMMAR[Constant] = "Constant:Type=1,Padding=1,Parent=C|" + HasConst + ",Value=B";
		GRAMMAR[CustomAttribute] = "CustomAttribute:Parent=C|" + HasCustomAttribute + ",Type=C|" + CustomAttributeType + ",Value=B";
		GRAMMAR[DeclSecurity] = "DeclSecurity:Action=2,Parent=C|" + HasDeclSecurity + ",PermissionSet=B";
		GRAMMAR[ENCLog] = "ENCLog:Token=4,FuncCode=4";
		GRAMMAR[ENCMap] = "ENCMap:Token=4";
		GRAMMAR[EventMap] = "EventMap:Parent=T|" + TypeDef + ",EventList=T|" + Event;
		GRAMMAR[Event] = "Event:EventFlags=2,Name=S,EventType=C|" + TypeDefOrRef;
		GRAMMAR[EventPtr] = "EventPtr:Event=T|" + Event;
		GRAMMAR[ExportedType] = "ExportedType:Flags=4,TypeDefID=4,TypeName=S,TypeNamespace=S,Implementation=C|" + Implementation;
		GRAMMAR[Field] = "Field:Flags=2,Name=S,Signature=B";
		GRAMMAR[FieldLayout] = "FieldLayout:Offset=4,Field=T|" + Field;
		GRAMMAR[FieldPtr] = "FieldPtr:Field=T|" + Field;
		GRAMMAR[FieldMarshal] = "FieldMarshal:Parent=C|" + HasFieldMarshal + ",NativeType=B";
		GRAMMAR[FieldRVA] = "FieldRVA:RVA=4,Field=T|" + Field;
		GRAMMAR[File] = "File:Flags=4,Name=S,HashValue=B";
		GRAMMAR[ImplMap] = "ImplMap:MappingFlags=2,MemberForwarded=C|" + MemberForwarded + ",ImportName=S,ImportScope=T|" + ModuleRef;
		GRAMMAR[InterfaceImpl] = "InterfaceImpl:Class=T|" + TypeDef + ",Interface=C|" + TypeDefOrRef;
		GRAMMAR[ManifestResource] = "ManifestResource:Offset=4,Flags=4,Name=S,Implementation=C|" + Implementation;
		GRAMMAR[MemberRef] = "MemberRef:Class=C|" + MemberRefParent + ",Name=S,Signature=B";
		GRAMMAR[Method] = "Method:RVA=4,ImplFlags=2,Flags=2,Name=S,Signature=B,ParamList=T|" + Param;
		GRAMMAR[MethodImpl] = "MethodImpl:Class=T|" + TypeDef + ",MethodBody=C|" + MethodDefOrRef + ",MethodDeclaration=C|" + MethodDefOrRef;
		GRAMMAR[MethodPtr] = "MethodPtr:Method=T|" + Method;
		GRAMMAR[MethodSemantics] = "MethodSemantics:Semantics=2,Method=T|" + Method + ",Association=C|" + HasSemantics;
		GRAMMAR[Module] = "Module:Generation=2,Name=S,Mvid=G,EncID=G,EncBaseID=G";
		GRAMMAR[ModuleRef] = "ModuleRef:Name=S";
		GRAMMAR[NestedClass] = "NestedClass:NestedClass=T|" + TypeDef + ",EnclosingClass=T|" + TypeDef;
		GRAMMAR[Param] = "Param:Flags=2,Sequence=2,Name=S";
		GRAMMAR[ParamPtr] = "ParamPtr:Param=T|" + Param;
		GRAMMAR[Property] = "Property:Flags=2,Name=S,Type=B";
		GRAMMAR[PropertyMap] = "PropertyMap:Parent=T|" + TypeDef + ",PropertyList=T|" + Property;
		GRAMMAR[PropertyPtr] = "PropertyPtr:Property=T|" + Property;
		GRAMMAR[StandAloneSig] = "StandAloneSig:Signature=B";
		GRAMMAR[TypeDef] = "TypeDef:Flags=4,Name=S,Namespace=S,Extends=C|" + TypeDefOrRef + ",FieldList=T|" + Field + ",MethodList=T|" + Method;
		GRAMMAR[TypeRef] = "TypeRef:ResolutionScope=C|" + ResolutionScope + ",Name=S,Namespace=S";
		GRAMMAR[TypeSpec] = "TypeSpec:Signature=B";
	}

	// BITS[i] = ceil(lg(TABLE_OPTIONS[i].length))
	public static final int[] BITS = {
			2,
			2,
			5,
			1,
			2,
			3,
			1,
			1,
			1,
			2,
			3,
			2
	};
	private static final int[] MASKS = {
			0x3,
			0x3,
			0x1F,
			0x1,
			0x3,
			0x7,
			0x1,
			0x1,
			0x1,
			0x3,
			0x7,
			0x3
	};

	private int[] INDEX_BITS;
	// these values are based on c_stream.Counts
	// INDEX_BITS[i]+BITS[i] determines the coded index size

	// coded indexes
	public static final int[][] TABLE_OPTIONS = {
   /*TypeDefOrRef*/
			{
					TypeDef,
					TypeRef,
					TypeSpec
			},
   /*HasConst*/
			{
					Field,
					Param,
					Property
			},
   /*HasCustomAttribute*/
			{
					Method,
					Field,
					TypeRef,
					TypeDef,
					Param,
					InterfaceImpl,
					MemberRef,
					Module,
					DeclSecurity,
					Property,
					Event,
					StandAloneSig,
					ModuleRef,
					TypeSpec,
					Assembly,
					AssemblyRef,
					File,
					ExportedType,
					ManifestResource
			},
   /*HasFieldMarshal*/
			{
					Field,
					Param
			},
   /*HasDeclSecurity*/
			{
					TypeDef,
					Method,
					Assembly
			},
   /*MemberRefParent*/
			{
					TypeDef,
					TypeRef,
					ModuleRef,
					Method,
					TypeSpec
			},
   /*HasSemantics*/
			{
					Event,
					Property
			},
   /*MethodDefOrRef*/
			{
					Method,
					MemberRef
			},
   /*MemberForwarded*/
			{
					Field,
					Method
			},
   /*Implementation*/
			{
					File,
					AssemblyRef,
					ExportedType
			},
   /*CustomAttributeType*/
			{
					TypeRef,
					TypeDef,
					Method,
					MemberRef
			},
			//, USString},
   /*ResolutionScope*/
			{
					Module,
					ModuleRef,
					AssemblyRef,
					TypeRef
			}
	};

	private int[] heapIndexSizes;
	private StringsStream strings_stream;
	private BlobStream blob_stream;
	private GUIDStream guid_stream;
	private USStream us_stream;
	private CompressedStream c_stream;
	private GenericTable[][] tables;

	/**
	 * Makes a TableConstants with the given table sizes, heap sizes, and streams
	 *
	 * @param compS   the #~ stream
	 * @param stringS the #Strings heap
	 * @param blobS   the #Blob heap
	 * @param guidS   the #GUID heap
	 * @param usS     the #US stream
	 */
	public TableConstants(CompressedStream compS, StringsStream stringsS, BlobStream blobS, GUIDStream guidS, USStream usS)
	{
		// tSizes must be at least 64 long
		heapIndexSizes = new int[3];

		c_stream = compS;
		strings_stream = stringsS;
		blob_stream = blobS;
		guid_stream = guidS;
		us_stream = usS;

		heapIndexSizes[StringsHeap] = c_stream.getStringsIndexSize();
		heapIndexSizes[GUIDHeap] = c_stream.getGUIDIndexSize();
		heapIndexSizes[BlobHeap] = c_stream.getBlobIndexSize();

		INDEX_BITS = new int[12];
		for(int j = 0; j < 12; j++)
		{
			long max = 0;
			for(int i = 0; i < TABLE_OPTIONS[j].length; i++)
			{
				if(TABLE_OPTIONS[j][i] != USString)
				{
					if(max < c_stream.Counts[TABLE_OPTIONS[j][i]])
					{
						max = c_stream.Counts[TABLE_OPTIONS[j][i]];
					}
				}
			}
			for(INDEX_BITS[j] = -1; max > 0; INDEX_BITS[j]++)
			{
				max >>= 1;
			}
			if(INDEX_BITS[j] == -1)
			{
				INDEX_BITS[j] = 0;
			}
		}
	}

	/**
	 * This method parses the metadata tables from the given input stream
	 *
	 * @param in the input stream to read from
	 */
	public void parseTables(edu.arizona.cs.mbel.MSILInputStream in) throws java.io.IOException
	{
		tables = new GenericTable[64][];

		for(int i = 0; i < 64; i++)
		{
			if(c_stream.Counts[i] > 0)
			{ // if table is present
				tables[i] = new GenericTable[(int) c_stream.Counts[i]];
				for(int j = 0; j < c_stream.Counts[i]; j++)
				{
					tables[i][j] = new GenericTable(GRAMMAR[i]);
					tables[i][j].parse(in, this);
				}
			}
		}
	}

	/**
	 * Returns all the metadata tables (should not be called until after parseTables)
	 */
	public GenericTable[][] getTables()
	{
		return tables;
	}

	/**
	 * Wrapper method around StringsStream.getStringByOffset,
	 * gets a string from the #Strings heap
	 *
	 * @param offset the offset into the #Strings heap
	 * @return the string from the #Strings heap, or "" if there is no #Strings heap
	 */
	public String getString(long offset)
	{
		if(strings_stream == null)
		{
			return "";
		}

		return strings_stream.getStringByOffset(offset);
	}

	/**
	 * Wrapper method around Blob.getBlobByOffset
	 *
	 * @param offset the offset into the #Blob stream
	 * @return the blob from the #Blob stream, or bull if there is no #Blob stream
	 */
	public byte[] getBlob(long offset)
	{
		if(blob_stream == null)
		{
			return new byte[0];
		}

		return blob_stream.getBlobByOffset(offset);
	}

	/**
	 * Wrapper method around GUIDStream.getGUIDByIndex
	 *
	 * @param index the 1-based index into the #GUID stream (note: not a byte offset!)
	 * @return the given GUID, or null if there is no #GUID stream
	 */
	public byte[] getGUID(long index)
	{
		if(guid_stream == null)
		{
			return new byte[0];
		}

		return guid_stream.getGUIDByIndex(index);
	}

	public String getUSString(long index)
	{
		if(us_stream == null)
		{
			return "";
		}
		return us_stream.getStringByOffset(index);
	}

	/**
	 * Turns a coded index into a pair of {table index, row index}
	 *
	 * @param codedIndex the coded index value
	 * @param type       the type of coded index (one of the constants defined in this class, i.e. HasConst)
	 * @return a long integer pair of {table number, row number}
	 */
	public long[] parseCodedIndex(long codedIndex, int type)
	{
		//  converts a codedindex into a {table number, row number} int array
		if(type < 0 || type > ResolutionScope)
		{
			return null;
		}

		long result[] = new long[2];
		result[0] = (long) TABLE_OPTIONS[type][(int) (codedIndex & MASKS[type])];
		// this will be the actual table number, not the stupid coded index flag number
		result[1] = (codedIndex >> BITS[type]);
		return result;
	}


	public static long buildCodedIndex(int codedtype, int tabletype, long row)
	{
		long result = 0;
		for(int i = 0; i < TABLE_OPTIONS[codedtype].length; i++)
		{
			if(tabletype == TABLE_OPTIONS[codedtype][i])
			{
				result = (row << BITS[codedtype]) | (MASKS[codedtype] & i);
				return result;
			}
		}
		return -1L;
	}

	/**
	 * Parses a coded index from the given input stream.
	 * This method implicitly uses the correct size of the given coded index
	 * when parsing it from the input stream. If the output value of this method is "token",
	 * then "token" and "type" would be valid inputs to parseCodedIndex.
	 *
	 * @param in   the input stream to read from
	 * @param type the type of coded index (one of the constants defined in this class, i.e. HasConst)
	 * @return a coded index value
	 */
	public long readCodedIndex(edu.arizona.cs.mbel.MSILInputStream in, int type) throws java.io.IOException
	{
		// reads the correct number of bytes into a coded index long
		// (this long can then be run through parseCodedIndex)
		if(type < 0 || type > ResolutionScope)
		{
			return -1L;
		}
		if(INDEX_BITS[type] + BITS[type] >= 16)
		{
			return in.readDWORD();
		}
		else
		{
			return (in.readWORD() & 0xFFFFL);
		}
	}

	/**
	 * Parses an index into one of the heaps (i.e. #Strings, #Blob, or #GUID).
	 * This method implcitly uses the correct size for the heap index.
	 *
	 * @param in   the input stream to read from
	 * @param heap a constant indicating which heap you want (constants defined in this class, i.e. StringsHeap)
	 * @return a token into one of the heaps
	 */
	public long readHeapIndex(edu.arizona.cs.mbel.MSILInputStream in, int heap) throws java.io.IOException
	{
		// reads the appropriate number of bytes from the file into a long
		if(heap < 0 || heap >= 3)
		{
			return -1L;
		}

		if(heapIndexSizes[heap] == 2)
		{
			return (in.readWORD() & 0xFFFFL);
		}
		else
		{
			return in.readDWORD();
		}
	}

	/**
	 * Parses a table index token (RID) from the given input stream.
	 * This method implicitly uses the correct size for the RID value.
	 *
	 * @param in    the input stream to read from
	 * @param table the number of the table type (constants defined in this class, i.e. TypeDef)
	 * @return a table index token (RID)
	 */
	public long readTableIndex(edu.arizona.cs.mbel.MSILInputStream in, int table) throws java.io.IOException
	{
		// reads the appropriate number of bytes for the given table index
		if(table < 0 || table >= 64)
		{
			return -1L;
		}
		if(c_stream.Counts[table] >= 65536)
		{
			return in.readDWORD();
		}
		else
		{
			return in.readWORD();
		}
	}

	/**
	 * Returns the number of elements in the specified table
	 *
	 * @param the table number (i.e. TypeDef)
	 */
	public long getTableSize(int table)
	{
		// returns the number of rows in the given table
		if(table < 0 || table >= 64)
		{
			return -1;
		}
		return c_stream.Counts[table];
	}
   
/*
   public void output(){
      System.out.print("TableConstants:{\n    StringsIndexSize = " + heapIndexSizes[StringsHeap]);
      System.out.print("\n    BlobIndexSize = " + heapIndexSizes[BlobHeap]);
      System.out.print("\n    GUIDIndexSize = " + heapIndexSizes[GUIDHeap]);
      System.out.print("\n    INDEX_BITS[12] = {");
      for (int i=0;i<12;i++)
         System.out.print(INDEX_BITS[i] + " ");
      System.out.print("}\n  }\n\n");

      for (int i=0;i<tables.length;i++){
         if (tables[i]!=null)
         for (int j=0;j<tables[i].length;j++){
            System.out.print((j+1)+": ");
            tables[i][j].output();
            System.out.print("\n");
         }
      }
   }
*/
}
