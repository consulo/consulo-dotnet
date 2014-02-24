package edu.arizona.cs.mbel.parse;
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


import java.io.IOException;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.MSILInputStream;

/**
 * This is the structure used to parse a PE/COFF Section Header.
 * Sections must appear in ascending order of virtual address,
 * and must be consecutive in the virtual address space.
 * Their virtual addresses must be multiples of SectionAlignment
 * in the PE header.
 * <p/>
 * Also, the PointerToRawData and SizeOfRawData fields must be multiples of
 * FileAlignment in the PE header.
 * If (SectionAlignment < PageSize) then
 * PointerToRawData == VirtualAddress
 *
 * @author Michael Stepp
 */
public class SectionHeader
{
	public static final int STRUCT_SIZE = 40;

	///// Flags for 'Characteristics' field //////////////////////////////
	public static final int IMAGE_SCN_SCALE_INDEX = 0x00000001;
	public static final int IMAGE_SCN_CNT_CODE = 0x00000020;
	public static final int IMAGE_SCN_CNT_INITIALIZED_DATA = 0x00000040;
	public static final int IMAGE_SCN_CNT_UNINITIALIZED_DATA = 0x00000080;
	public static final int IMAGE_SCN_NO_DEFER_SPEC_EXC = 0x00004000;
	public static final int IMAGE_SCN_LNK_NRELOC_OVFL = 0x01000000;
	public static final int IMAGE_SCN_MEM_DISCARDABLE = 0x02000000;
	public static final int IMAGE_SCN_MEM_NOT_CACHED = 0x04000000;
	public static final int IMAGE_SCN_MEM_NOT_PAGED = 0x08000000;
	public static final int IMAGE_SCN_MEM_SHARED = 0x10000000;
	public static final int IMAGE_SCN_MEM_EXECUTE = 0x20000000;
	public static final int IMAGE_SCN_MEM_READ = 0x40000000;
	public static final int IMAGE_SCN_MEM_WRITE = 0x80000000;
	//////////////////////////////////////////////////////////////////////

	public byte Name[];                 // 8bytes
	public long VirtualSize;            // 4bytes
	public long VirtualAddress;         // 4bytes (really an RVA)
	public long SizeOfRawData;          // 4bytes
	public long PointerToRawData;       // 4bytes
	public long PointerToRelocations;   // 4bytes (file pointer) (object only)
	public long PointerToLinenumbers;   // 4bytes (file pointer)
	public int NumberOfRelocations;     // 2bytes
	public int NumberOfLinenumbers;     // 2bytes
	public long Characteristics;        // 4bytes
	public long startFP;

	/**
	 * Makes a new section header with none of its field set
	 */
	public SectionHeader()
	{
		Name = new byte[8];
	}

	/**
	 * Parses a Section Header from an input stream
	 */
	protected SectionHeader(MSILInputStream in) throws IOException
	{
		startFP = in.getCurrent();

		Name = new byte[8];
		in.read(Name);

		VirtualSize = in.readDWORD();
		VirtualAddress = in.readDWORD();
		SizeOfRawData = in.readDWORD();
		PointerToRawData = in.readDWORD();
		PointerToRelocations = in.readDWORD();
		PointerToLinenumbers = in.readDWORD();
		NumberOfRelocations = in.readWORD();
		NumberOfLinenumbers = in.readWORD();
		Characteristics = in.readDWORD();
	}

	/**
	 * This returns true iff the name of this section header is ".net"
	 */
	public boolean isNetHeader()
	{
		boolean result = (Name[0] == (byte) '.') &&
				(Name[1] == (byte) 'n') &&
				(Name[2] == (byte) 'e') &&
				(Name[3] == (byte) 't') &&
				(Name[4] == 0) &&
				(Name[5] == 0) &&
				(Name[6] == 0) &&
				(Name[7] == 0);
		return result;
	}

	/**
	 * Writes this section header out to a buffer
	 */
	public void emit(ByteBuffer buffer)
	{
		buffer.put(Name);
		buffer.putDWORD(VirtualSize);
		buffer.putDWORD(VirtualAddress);
		buffer.putDWORD(SizeOfRawData);
		buffer.putDWORD(PointerToRawData);
		buffer.putDWORD(PointerToRelocations); // must be patched if shifted!
		buffer.putDWORD(PointerToLinenumbers); // must be patched if shifted!
		buffer.putWORD(NumberOfRelocations);
		buffer.putWORD(NumberOfLinenumbers);
		buffer.putDWORD(Characteristics);
	}

	 /*
   public void output(){
      System.out.print("Name = [ ");
      for (int i=0;i<8;i++){
         if ((Name[i]&0xFF)>32 && (Name[i]&0xFF)<128)
            System.out.print((char)(Name[i]&0xFF) + " ");
         else
            System.out.print((Name[i]&0xFF) + " ");
      }

      System.out.print("]\nVirtualSize = " + "0x" + Long.toHexString(VirtualSize));
      System.out.print("\nVirtualAddress = 0x" + Long.toHexString(VirtualAddress));
      System.out.print("\nSizeOfRawData = 0x" + Long.toHexString(SizeOfRawData));
      System.out.print("\nPointerToRawData = " + "0x" + Long.toHexString(PointerToRawData));
      System.out.print("\nPointerToRelocations = " + "0x" + Long.toHexString(PointerToRelocations));
      System.out.print("\nPointerToLinenumbers = " + "0x" + Long.toHexString(PointerToLinenumbers));
      System.out.print("\nNumberOfRelocations = " + NumberOfRelocations);
      System.out.print("\nNumberOfLinenumbers = " + NumberOfLinenumbers);
      System.out.print("\nCharacteristics = 0x" + Long.toHexString(Characteristics));
   }
	 */
}
