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


package edu.arizona.cs.mbel.parse;

import java.io.IOException;

import edu.arizona.cs.mbel.MSILInputStream;

/**
 * This class holds the data from the COFF Header in a PE/COFF file.
 * The COFF header comes right after the PE signature, and right before the PE header.
 *
 * @author Michael Stepp
 */
public class COFF_Header
{
	public static final int[] MACHINE_TYPES = {
			0,
			0x014c,
			0x0162,
			0x0166,
			0x0168,
			0x0169,
			0x0184,
			0x01F0,
			0x01A2,
			0x01A4,
			0x01A6,
			0x01C0,
			0x01C2,
			0x0200,
			0x0266,
			0x0366,
			0x0466,
			0x0284,
			0x0284
	};

	public static final int IMAGE_FILE_MACHINE_UNKNOWN = 0;
	public static final int IMAGE_FILE_MACHINE_I386 = 1;
	public static final int IMAGE_FILE_MACHINE_R3000 = 2;
	public static final int IMAGE_FILE_MACHINE_R4000 = 3;
	public static final int IMAGE_FILE_MACHINE_R10000 = 4;
	public static final int IMAGE_FILE_MACHINE_WCEMIPSV2 = 5;
	public static final int IMAGE_FILE_MACHINE_ALPHA = 6;
	public static final int IMAGE_FILE_MACHINE_POWERPC = 7;
	public static final int IMAGE_FILE_MACHINE_SH3 = 8;
	public static final int IMAGE_FILE_MACHINE_SH3E = 9;
	public static final int IMAGE_FILE_MACHINE_SH4 = 10;
	public static final int IMAGE_FILE_MACHINE_ARM = 11;
	public static final int IMAGE_FILE_MACHINE_THUMB = 12;
	public static final int IMAGE_FILE_MACHINE_IA64 = 13;
	public static final int IMAGE_FILE_MACHINE_MIPS16 = 14;
	public static final int IMAGE_FILE_MACHINE_MIPSFPU = 15;
	public static final int IMAGE_FILE_MACHINE_MIPSFPU16 = 16;
	public static final int IMAGE_FILE_MACHINE_ALPHA64 = 17;
	public static final int IMAGE_FILE_MACHINE_AXP64 = 18;
	// Constants for 'Machine' field /////////////////////////
	public static final int IMAGE_FILE_RELOCS_STRIPPED = 0x0001;
	public static final int IMAGE_FILE_EXECUTABLE_IMAGE = 0x0002;
	public static final int IMAGE_FILE_LINE_NUMS_STRIPPED = 0x0004;
	public static final int IMAGE_FILE_LOCAL_SYMS_STRIPPED = 0x0008;
	public static final int IMAGE_FILE_AGGRESIVE_WS_TRIM = 0x0010;
	public static final int IMAGE_FILE_LARGE_ADDRESS_AWARE = 0x0020;
	public static final int IMAGE_FILE_BYTES_REVERSED_LO = 0x0080;
	public static final int IMAGE_FILE_32BIT_MACHINE = 0x0100;
	public static final int IMAGE_FILE_DEBUG_STRIPPED = 0x0200;
	public static final int IMAGE_FILE_REMOVABLE_RUN_FROM_SWAP = 0x0400;
	public static final int IMAGE_FILE_NET_RUN_FROM_SWAP = 0x0800;
	public static final int IMAGE_FILE_SYSTEM = 0x1000;
	public static final int IMAGE_FILE_DLL = 0x2000;
	public static final int IMAGE_FILE_UP_SYSTEM_ONLY = 0x4000;
	public static final int IMAGE_FILE_BYTES_REVERSED_HI = 0x8000;
	// Constants for 'Characteristics' field ///////////////////

	public int Machine;                 // 2bytes
	public int NumberOfSections;        // 2bytes
	public long TimeDateStamp;          // 4bytes
	public long PointerToSymbolTable;   // 4bytes (==0) (file pointer)
	public long NumberOfSymbols;        // 4bytes (==0)
	public int SizeOfOptionalHeader;    // 2bytes
	public int Characteristics;         // 2bytes

	protected COFF_Header()
	{
		Machine = 0x14c;
		NumberOfSections = 2;
		TimeDateStamp = System.currentTimeMillis() / 1000;
		PointerToSymbolTable = 0;
		NumberOfSymbols = 0;
		SizeOfOptionalHeader = 224;
		Characteristics = IMAGE_FILE_EXECUTABLE_IMAGE |
				IMAGE_FILE_LINE_NUMS_STRIPPED |
				IMAGE_FILE_LOCAL_SYMS_STRIPPED |
				IMAGE_FILE_32BIT_MACHINE;
	}

	/**
	 * Parses a COFF_Header from an input stream
	 */
	public COFF_Header(MSILInputStream in) throws IOException, MSILParseException
	{
		Machine = in.readWORD();
		NumberOfSections = in.readWORD();
		TimeDateStamp = in.readDWORD();
		PointerToSymbolTable = in.readDWORD();
		NumberOfSymbols = in.readDWORD();
		SizeOfOptionalHeader = in.readWORD();
		Characteristics = in.readWORD();
	}

	 /*
   public void output(){
      System.out.print("COFF Header:{\n  Machine = " + "0x" + Integer.toHexString(Machine));
      System.out.print("\n  NumberOfSections = " + NumberOfSections);
      System.out.print("\n  TimeDateStamp = " + "0x" + Long.toHexString(TimeDateStamp));
      System.out.print("\n  PointerToSymbolTable = " + "0x" + Long.toHexString(PointerToSymbolTable));
      System.out.print("\n  NumberOfSymbols = " + NumberOfSymbols);
      System.out.print("\n  SizeOfOptionalHeader = " + SizeOfOptionalHeader);
      System.out.print("\n  Characteristics = " + Integer.toBinaryString(Characteristics));
      System.out.print("\n}\n");
   }
	 */
}


