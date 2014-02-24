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

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.MSILInputStream;

/**
 * This class holds a data directory entry, which comes after the PE header in a PE/COFF file.
 * A data directory entry has only 2 fields: VirtualAddress and Size.
 * The VirtualAddress is an RVA to the data for this entry, and the Size is the size in bytes of that entry.
 *
 * @author Michael Stepp
 */
public class ImageDataDirectory
{
	public static final String[] STRINGS = {
			"Export Table",
			"Import Table",
			"Resource Table",
			"Exception Table",
			"Certificate Table",
			"Base Relocation Table",
			"Debug",
			"Architecture",
			"Global Pointer",
			"TLS Table",
			"Load Config Table",
			"Bound Import Table",
			"Import Address Table",
			"Delay Import Descriptor Table",
			"CLI Header",
			"None"
	};

	public static final int EXPORT_TABLE_INDEX = 0;
	public static final int IMPORT_TABLE_INDEX = 1;
	public static final int RESOURCE_TABLE_INDEX = 2;
	public static final int EXCEPTION_TABLE_INDEX = 3;
	public static final int CERTIFICATE_TABLE_INDEX = 4;
	public static final int BASE_RELOCATION_TABLE_INDEX = 5;
	public static final int DEBUG_TABLE_INDEX = 6;
	public static final int ARCHITECTURE_INDEX = 7;
	public static final int GLOBAL_PTR_INDEX = 8;
	public static final int TLS_TABLE_INDEX = 9;
	public static final int LOAD_CONFIG_TABLE_INDEX = 10;
	public static final int BOUND_IMPORT_TABLE_INDEX = 11;
	public static final int IMPORT_ADDRESS_TABLE_INDEX = 12;
	public static final int DELAY_IMPORT_TABLE_INDEX = 13;
	public static final int CLI_HEADER_INDEX = 14;

	public long VirtualAddress; // 4byte RVA
	public long Size;           // 4bytes

	protected ImageDataDirectory()
	{
		VirtualAddress = Size = 0;
	}

	/**
	 * Parses an ImageDataDirectory from an input stream
	 */
	public ImageDataDirectory(MSILInputStream in) throws IOException
	{
		VirtualAddress = in.readDWORD();
		Size = in.readDWORD();
	}

	/**
	 * Writes this data directory out to a buffer
	 */
	public void emit(ByteBuffer buffer)
	{
		buffer.putDWORD(VirtualAddress);
		buffer.putDWORD(Size);
	}

	 /*
   public void output(){
      System.out.print("{\n  VirtualAddress = " + "0x" + Long.toHexString(VirtualAddress));
      System.out.print("\n  Size = " + Size + "\n}");
   }
	 */

	public String toString()
	{
		String result = "{\n  VirtualAddress = " + "0x" + Long.toHexString(VirtualAddress);
		result += ("\n  Size = " + Size + "\n}");
		return result;
	}
}
