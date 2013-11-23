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
 * This class stores the #GUID metadata stream.
 * A GUID is a 128-bit value (Globally Unique IDentifier).
 * GUIDs are indexed by GUID number, not by byte offset.
 * The GUID numbers are 1-based.
 *
 * @author Michael Stepp
 */
public class GUIDStream
{
	// should be 4-byte aligned
	private byte[][] guids;
	private int num_guids;

	/**
	 * Constructs and parses a GUIDStream
	 *
	 * @param in   the input stream to read from
	 * @param size the size in bytes of the #GUID stream
	 */
	public GUIDStream(edu.arizona.cs.mbel.MSILInputStream in, long size) throws java.io.IOException
	{
		guids = new byte[(int) (size >> 4) + 1][16];
		num_guids = 0;

		for(num_guids = 0; (num_guids << 4) < size; num_guids++)
		{
			in.read(guids[num_guids]);
		}
	}

	/**
	 * Method to convert a GUID to a hexadecimal string
	 *
	 * @param guid a byte array which represents a GUID (should be of size 16)
	 */
	public static String GUIDToString(byte[] guid)
	{
		// prints out a big-endian interpretation of the byte array
		// (==BlobStream.blobToString(byte[]))

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
		for(int i = 0; i < guid.length; i++)
		{
			temp = guid[i] & 0xFF;
			result += hex[(temp >> 4) & 0xF] + hex[temp & 0xF];
		}
		if(guid.length == 0)
		{
			result += '0';
		}
		return result;
	}

	/**
	 * Returns the GUID indexed by the given number
	 *
	 * @param index the GUID index number. If index is out of bounds, then a 0-length byte array is returned.
	 * @return the given GUID, or a 0-length byte array if the index is invalid
	 */
	public byte[] getGUIDByIndex(long index)
	{
		// returns a copy of the given GUID (uses 1-based indexes (0==null))
		index--;
		if(index < 0 || index >= num_guids)
		{
			return new byte[0];
		}

		byte[] result = new byte[16];
		for(int i = 0; i < 16; i++)
		{
			result[i] = guids[(int) index][i];
		}

		return result;
	}

	/**
	 * Returns the number of GUIDS (note: this value should be 1/16th the size in bytes of the GUID stream)
	 */
	public int getNumGUIDS()
	{
		return num_guids;
	}

/*
   public void output(){
      String bytes[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

      System.out.print("#GUIDStream["+num_guids+"] = {");
      for (int i=0;i<num_guids;i++){
         System.out.print("\n  0x");
         for (int j=0;j<16;j++)
            System.out.print(bytes[(guids[i][j]>>4) & 0x0F] + bytes[guids[i][j] & 0x0F]);
      }
      System.out.print("\n}");
   }
*/
}
