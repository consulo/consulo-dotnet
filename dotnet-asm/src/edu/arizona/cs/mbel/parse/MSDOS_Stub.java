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
 * Ths MS-DOS stub starts a PE/COFF file. The only relevant field in this structure
 * is the NewFileHeaderAddress, which will be at offset 0x3C romt he start of the file.
 * The value here will be the file offset to the PE signature, which is followed by the PE header.
 *
 * @author Michael Stepp
 */
public class MSDOS_Stub
{
	private static final int MAGIC = 0x5A4D;  // == 'MZ'

	private int Magic;                  // 2 bytes
	private byte[] data1;               // 58 bytes
	private long NewFileHeaderAddress;  // 4 bytes
	private byte[] data2;               // (NewFileHeaderAddress-64) bytes

	protected MSDOS_Stub()
	{
		Magic = MAGIC;
		data1 = new byte[]{
				(byte) 0x90,
				0x00,
				0x03,
				0x00,
				0x00,
				0x00,
				0x04,
				0x00,
				0x00,
				0x00,
				(byte) 0xFF,
				(byte) 0xFF,
				0x00,
				0x00,
				(byte) 0xb8,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x40,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00
		};
		NewFileHeaderAddress = 0x80L;
		data2 = new byte[]{
				0x0e,
				0x1f,
				(byte) 0xba,
				0x0e,
				0x00,
				(byte) 0xb4,
				0x09,
				(byte) 0xcd,
				0x21,
				(byte) 0xb8,
				0x01,
				0x4c,
				(byte) 0xcd,
				0x21,
				0x54,
				0x68,
				0x69,
				0x73,
				0x20,
				0x70,
				0x72,
				0x6f,
				0x67,
				0x72,
				0x61,
				0x6d,
				0x20,
				0x63,
				0x61,
				0x6e,
				0x6e,
				0x6f,
				0x74,
				0x20,
				0x62,
				0x65,
				0x20,
				0x72,
				0x75,
				0x6e,
				0x20,
				0x69,
				0x6e,
				0x20,
				0x44,
				0x4f,
				0x53,
				0x20,
				0x6d,
				0x6f,
				0x64,
				0x65,
				0x2e,
				0x0d,
				0x0d,
				0x0a,
				0x24,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00,
				0x00
		};
	}

	/**
	 * Prses an MSDOS_Stub from an input stream
	 */
	public MSDOS_Stub(MSILInputStream in) throws IOException, MSILParseException
	{
		Magic = in.readWORD();
		if(Magic != MAGIC)
		{
			throw new MSILParseException("MSDOS_Stub: File does not start with magic number 0x4D5A");
		}

		data1 = new byte[58];
		in.read(data1);
		NewFileHeaderAddress = in.readDWORD();
		data2 = new byte[(int) (NewFileHeaderAddress - 64)];
		in.read(data2);
	}

	/**
	 * Returns the file offset of the PE signature
	 */
	public long getHeaderAddress()
	{
		return NewFileHeaderAddress;
	}

	 /*
   public void output(){
      System.out.print("MSDOS_Stub:{");
      System.out.print("\n  Magic = 0x" + Integer.toHexString(Magic));
      System.out.print("\n  <data1>");
      System.out.print("\n  NewFileHeaderAddress = 0x" + Long.toHexString(NewFileHeaderAddress));
      System.out.print("\n  <data2>");
      System.out.print("\n}\n");
   }
	 */

	/**
	 * Writes the MSDOS_Stub back out to a buffer
	 */
	public void emit(ByteBuffer out)
	{
		out.putWORD(MAGIC);
		out.put(data1);
		out.putDWORD(NewFileHeaderAddress);
		out.put(data2);
	}
}
