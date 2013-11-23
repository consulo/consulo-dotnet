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
 * This class parses the PE header of a PE/COFF file
 *
 * @author Michael Stepp
 */
public class PE_Header
{
	public static final byte[] PE_TAG = new byte[]{
			80,
			69,
			0,
			0
	}; // "PE\0\0"

	public static final int PE_SUBSYSTEM_NATIVE = 1;
	public static final int PE_SUBSYSTEM_WINDOWS_GUI = 2;
	public static final int PE_SUBSYSTEM_WINDOWS_CUI = 3;
	public static final int PE_SUBSYSTEM_OS2_CUI = 5;
	public static final int PE_SUBSYSTEM_POSIX_CUI = 7;
	public static final int PE_SUBSYSTEM_NATIVE_WINDOWS = 8;
	public static final int PE_SUBSYSTEM_WINDOWS_CE_GUI = 9;
	// constants for the 'Subsystem' field
	public static final int PE32_MAGIC = 0x10b;
	public static final int PE32_PLUS_MAGIC = 0x20b;

	public int Magic;                      // 2bytes
	public int MajorLinkerVersion;         // 1byte
	public int MinorLinkerVersion;         // 1byte
	public long SizeOfCode;                // 4bytes
	public long SizeOfInitializedData;     // 4bytes
	public long SizeOfUninitializedData;   // 4bytes
	public long AddressOfEntryPoint;       // 4byte RVA
	public long BaseOfCode;                // 4byte RVA
	// end of same format for PE32/PE32+
	public long BaseOfData;                // 4byte RVA
	// absent in PE32+


	// NT additional fields
	public long ImageBase;                    // 4byte VA
	public long SectionAlignment;             // 4bytes
	public long FileAlignment;                // 4bytes
	public int MajorOperatingSystemVersion;   // 2bytes
	public int MinorOperatingSystemVersion;   // 2bytes
	public int MajorImageVersion;             // 2bytes
	public int MinorImageVersion;             // 2bytes
	public int MajorSubsystemVersion;         // 2bytes
	public int MinorSubsystemVersion;         // 2bytes
	public long Win32VersionValue;            // 4bytes
	public long SizeOfImage;                  // 4bytes
	public long SizeOfHeaders;                // 4bytes
	public long CheckSum;                     // 4bytes
	public int Subsystem;                     // 2bytes
	public int DllCharacteristics;            // 2bytes
	public long SizeOfStackReserve;           // 4bytes
	public long SizeOfStackCommit;            // 4bytes
	public long SizeOfHeapReserve;            // 4bytes
	public long SizeOfHeapCommit;             // 4bytes
	public long LoaderFlags;                  // 4bytes
	public long NumberOfRvaAndSizes;          // 4bytes
	public ImageDataDirectory[] DataDirectory;// usually [16]

	protected PE_Header(int sub)
	{
		Magic = PE32_MAGIC;
		MajorLinkerVersion = 6;
		MinorLinkerVersion = 0;
		SizeOfCode = 0;   // will get updated
		SizeOfInitializedData = 0x1000;
		SizeOfUninitializedData = 0;
		AddressOfEntryPoint = 0x2056;
		BaseOfCode = 0x2000;
		BaseOfData = 0x4000;
		ImageBase = 0x400000;
		SectionAlignment = 0x2000;
		FileAlignment = 0x1000;
		MajorOperatingSystemVersion = 4;
		MinorOperatingSystemVersion = 0;
		MajorImageVersion = 0;
		MinorImageVersion = 0;
		MajorSubsystemVersion = 4;
		MinorSubsystemVersion = 0;
		Win32VersionValue = 0;
		SizeOfImage = 0;  // will be updated
		SizeOfHeaders = 0x1000;
		CheckSum = 0;
		Subsystem = sub;
		DllCharacteristics = 0;
		SizeOfStackReserve = 1048576;
		SizeOfStackCommit = 4096;
		SizeOfHeapReserve = 1048576;
		SizeOfHeapCommit = 4096;
		LoaderFlags = 0;
		NumberOfRvaAndSizes = 16;
		DataDirectory = new ImageDataDirectory[16];
		for(int i = 0; i < 16; i++)
		{
			DataDirectory[i] = new ImageDataDirectory();
		}

		DataDirectory[1].VirtualAddress = 0x2010;
		DataDirectory[1].Size = 40;

		DataDirectory[5].VirtualAddress = 0x4000;
		DataDirectory[5].Size = 12;

		DataDirectory[12].VirtualAddress = 0x2000;
		DataDirectory[12].Size = 8;
	}


	/**
	 * Parses a PE_Header from an input stream
	 */
	public PE_Header(MSILInputStream in) throws IOException, MSILParseException
	{
		Magic = in.readWORD();
		MajorLinkerVersion = in.readBYTE();
		MinorLinkerVersion = in.readBYTE();
		SizeOfCode = in.readDWORD();
		SizeOfInitializedData = in.readDWORD();
		SizeOfUninitializedData = in.readDWORD();
		AddressOfEntryPoint = in.readDWORD();
		BaseOfCode = in.readDWORD();

		if(Magic == PE32_MAGIC)
		{
			// PE32 (normal)
			BaseOfData = in.readDWORD();
			ImageBase = in.readDWORD();
			SectionAlignment = in.readDWORD();
			FileAlignment = in.readDWORD();
			MajorOperatingSystemVersion = in.readWORD();
			MinorOperatingSystemVersion = in.readWORD();
			MajorImageVersion = in.readWORD();
			MinorImageVersion = in.readWORD();
			MajorSubsystemVersion = in.readWORD();
			MinorSubsystemVersion = in.readWORD();
			Win32VersionValue = in.readDWORD();
			SizeOfImage = in.readDWORD();
			SizeOfHeaders = in.readDWORD();
			CheckSum = in.readDWORD();
			Subsystem = in.readWORD();
			DllCharacteristics = in.readWORD();
			// obsolete, ==0

			SizeOfStackReserve = in.readDWORD();
			SizeOfStackCommit = in.readDWORD();
			SizeOfHeapReserve = in.readDWORD();
			SizeOfHeapCommit = in.readDWORD();
			LoaderFlags = in.readDWORD();
			// obsolete, ==0

			NumberOfRvaAndSizes = in.readDWORD();
		}
		else if(Magic == PE32_PLUS_MAGIC)
		{
			// PE32+
			ImageBase = in.readDDWORD();
			SectionAlignment = in.readDWORD();
			FileAlignment = in.readDWORD();
			MajorOperatingSystemVersion = in.readWORD();
			MinorOperatingSystemVersion = in.readWORD();
			MajorImageVersion = in.readWORD();
			MinorImageVersion = in.readWORD();
			MajorSubsystemVersion = in.readWORD();
			MinorSubsystemVersion = in.readWORD();
			Win32VersionValue = in.readDWORD();
			SizeOfImage = in.readDWORD();
			SizeOfHeaders = in.readDWORD();
			CheckSum = in.readDWORD();
			Subsystem = in.readWORD();
			DllCharacteristics = in.readWORD();
			// obsolete, ==0

			SizeOfStackReserve = in.readDDWORD();
			SizeOfStackCommit = in.readDDWORD();
			SizeOfHeapReserve = in.readDDWORD();
			SizeOfHeapCommit = in.readDDWORD();
			LoaderFlags = in.readDWORD();
			// obsolete, ==0

			NumberOfRvaAndSizes = in.readDWORD();
		}
		else
		{
			throw new MSILParseException("PE_Header: Invalid magic number");
		}

		DataDirectory = new ImageDataDirectory[(int) NumberOfRvaAndSizes];
		for(int i = 0; i < NumberOfRvaAndSizes; i++)
		{
			DataDirectory[i] = new ImageDataDirectory(in);
		}
	}

	/**
	 * Writes the PE_Header out to a buffer
	 */
	public void emit(ByteBuffer buffer)
	{
		buffer.putWORD(Magic);
		buffer.put(MajorLinkerVersion);
		buffer.put(MinorLinkerVersion);
		buffer.putDWORD(SizeOfCode);
		buffer.putDWORD(SizeOfInitializedData);
		buffer.putDWORD(SizeOfUninitializedData);
		buffer.putDWORD(AddressOfEntryPoint);
		buffer.putDWORD(BaseOfCode);
		buffer.putDWORD(BaseOfData);
		buffer.putDWORD(ImageBase);
		buffer.putDWORD(SectionAlignment);
		buffer.putDWORD(FileAlignment);
		buffer.putWORD(MajorOperatingSystemVersion);
		buffer.putWORD(MinorOperatingSystemVersion);
		buffer.putWORD(MajorImageVersion);
		buffer.putWORD(MinorImageVersion);
		buffer.putWORD(MajorSubsystemVersion);
		buffer.putWORD(MinorSubsystemVersion);
		buffer.putDWORD(Win32VersionValue);
		buffer.putDWORD(SizeOfImage);
		buffer.putDWORD(SizeOfHeaders);
		buffer.putDWORD(0);  // checksum (if present, will definitely change)
		buffer.putWORD(Subsystem);
		buffer.putWORD(DllCharacteristics);
		buffer.putDWORD(SizeOfStackReserve);
		buffer.putDWORD(SizeOfStackCommit);
		buffer.putDWORD(SizeOfHeapReserve);
		buffer.putDWORD(SizeOfHeapCommit);
		buffer.putDWORD(LoaderFlags);
		buffer.putDWORD(NumberOfRvaAndSizes);
	}

	 /*
   public void output(){
      System.out.print("PE Header:{");
      System.out.print("\n  Magic = " + "0x" + Integer.toHexString(Magic));
      System.out.print("\n  MajorLinkerVersion = " + MajorLinkerVersion);
      System.out.print("\n  MinorLinkerVersion = " + MinorLinkerVersion);
      System.out.print("\n  SizeOfCode = " + SizeOfCode);
      System.out.print("\n  SizeOfInitializedData = " + SizeOfInitializedData);
      System.out.print("\n  SizeOfUninitializedData = " + SizeOfUninitializedData);
      System.out.print("\n  AddressOfEntryPoint = " + "0x" + Long.toHexString(AddressOfEntryPoint));
      System.out.print("\n  BaseOfCode = " + "0x" + Long.toHexString(BaseOfCode));
      System.out.print("\n  BaseOfData = " + "0x" + Long.toHexString(BaseOfData));
      System.out.print("\n  /// NT Additional fields ////////");
      System.out.print("\n  ImageBase = " + "0x" + Long.toHexString(ImageBase));
      System.out.print("\n  SectionAlignment = " + "0x" + Long.toHexString(SectionAlignment));
      System.out.print("\n  FileAlignment = " + "0x" + Long.toHexString(FileAlignment));
      System.out.print("\n  MajorOperatingSystemVersion = " + MajorOperatingSystemVersion);
      System.out.print("\n  MinorOperatingSystemVersion = " + MinorOperatingSystemVersion);
      System.out.print("\n  MajorImageVersion = " + MajorImageVersion);
      System.out.print("\n  MinorImageVersion = " + MinorImageVersion);
      System.out.print("\n  MajorSubsystemVersion = " + MajorSubsystemVersion);
      System.out.print("\n  MinorSubsystemVersion = " + MinorSubsystemVersion);
      System.out.print("\n  Win32VersionValue = " + Win32VersionValue);
      System.out.print("\n  SizeOfImage = " + SizeOfImage);
      System.out.print("\n  SizeOfHeaders = " + SizeOfHeaders);
      System.out.print("\n  CheckSum = " + "0x" + Long.toHexString(CheckSum));
      System.out.print("\n  Subsystem = " + Subsystem);
      System.out.print("\n  DllCharacteristics = " + "0x" + Integer.toBinaryString(DllCharacteristics));
      System.out.print("\n  SizeOfStackReserve = " + SizeOfStackReserve);
      System.out.print("\n  SizeOfStackCommit = " + SizeOfStackCommit);
      System.out.print("\n  SizeOfHeapReserve = " + SizeOfHeapReserve);
      System.out.print("\n  SizeOfHeapCommit = " + SizeOfHeapCommit);
      System.out.print("\n  LoaderFlags = " + "0x" + Long.toBinaryString(LoaderFlags));
      System.out.print("\n  NumberOfRvaAndSizes = " + NumberOfRvaAndSizes);

      System.out.print("\n  DataDirectory[" + NumberOfRvaAndSizes + "] = \n  {");

      for (int i=0;i<NumberOfRvaAndSizes;i++){
         System.out.print("\n    {\n      VirtualAddress = " + "0x" + Long.toHexString(DataDirectory[i].VirtualAddress) + "\n      Size = " +
         DataDirectory[i].Size + "\n    }");
         if (i<16)
            System.out.print(ImageDataDirectory.STRINGS[i]);
      }
      System.out.print("\n  }\n}");
   }
	 */
}
