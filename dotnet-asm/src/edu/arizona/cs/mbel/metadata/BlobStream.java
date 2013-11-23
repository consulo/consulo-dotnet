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
 * This class holds the #Blob stream for a .NET module.
 * The blob stream contains raw binary data, such as default field values and method signatures.
 * It is accessed by 0-based index, at which point there is a PackedLen integer giving the blob's
 * length, followed by the blob itself.
 *
 * @author Michael Stepp
 */
public class BlobStream
{
	// #blob heap, 4-byte aligned
	private byte[] raw_bytes;

	/**
	 * Parses a BlobStream from an input stream
	 */
	public BlobStream(edu.arizona.cs.mbel.MSILInputStream in, long size) throws java.io.IOException
	{
		raw_bytes = new byte[(int) size];
		in.read(raw_bytes);
	}

	/**
	 * Returns the hex representation of a given blob
	 */
	public static String blobToString(byte[] bytes)
	{
		// prints out a big-endian interpretation of the byte array

		char hex[] = {
				'0',
				'1',
				'2',
				'3',
				'4',
				'5',
				'6',
				'7',
				'8',
				'9',
				'A',
				'B',
				'C',
				'D',
				'E',
				'F'
		};
		String result = "0x";
		int temp;
		for(int i = 0; i < bytes.length; i++)
		{
			temp = bytes[i] & 0xFF;
			result += hex[(temp >> 4) & 0xF] + "" + hex[temp & 0xF];
		}
		if(bytes.length == 0)
		{
			result += '0';
		}
		return result;
	}

	/**
	 * Returns the size in bytes of this blob stream
	 */
	public int getSize()
	{
		return raw_bytes.length;
	}

	/**
	 * Returns the blob starting at the given offset
	 */
	public byte[] getBlobByOffset(long offset)
	{
		if(offset < 0 || offset >= raw_bytes.length)
		{
			return new byte[0];
		}
		int off = (int) offset;
		int length = raw_bytes[off] & 0xFF;
		int start = off + 1;
		if((length & 0xE0) == 0xC0)
		{
			// 4 byte length
			length = (length & 0x3F) << 24;
			length |= (((int) raw_bytes[off + 1]) & 0xFF) << 16;
			length |= (((int) raw_bytes[off + 2]) & 0xFF) << 8;
			length |= (((int) raw_bytes[off + 3]) & 0xFF);
			start = off + 4;
		}
		else if((length & 0xC0) == 0x80)
		{
			length = (length & 0x7F) << 8;
			length |= (((int) raw_bytes[off + 1]) & 0xFF);
			start = off + 2;
		}

		byte[] result = new byte[length];
		for(int i = 0; i < length; i++)
		{
			result[i] = raw_bytes[start + i];
		}

		return result;
	}

/*
   public void output(){
      System.out.print("BlobStream:[");

      char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
      int temp;
      for (int i=0;i<raw_bytes.length;i++){
         if (i%20==0)
            System.out.print("\n  ");
         temp = raw_bytes[i]&0xFF;
         System.out.print(hex[(temp>>4)&0xF] + "" + hex[temp&0xF]);
      }
      System.out.print("\n]");
   }
*/
}
