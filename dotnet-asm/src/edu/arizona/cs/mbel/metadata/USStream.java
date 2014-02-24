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

import java.io.IOException;

import edu.arizona.cs.mbel.MSILInputStream;

/**
 * This class represents the user string stream in module metadata.
 * User strings are UNICODE-16 strings, little-endian. They are single-null terminated.
 *
 * @author Michael Stepp
 */
public class USStream
{
	// should be 4-byte aligned
	private byte[] rawbytes;

	/**
	 * Parses a USStream from an input stream, of the given size
	 *
	 * @param in   the input stream
	 * @param size the size in bytes of this USStream
	 */
	public USStream(MSILInputStream in, long size) throws IOException
	{
		rawbytes = new byte[(int) size];
		in.read(rawbytes);
	}

   /*
   public void output(){
      System.out.print("#USStream = {}");
   }
   */

	/**
	 * Returns a string from the user string stream, indexed by byte offset
	 *
	 * @param off the byte offset within the stream of the start of the string
	 * @return the string at the given offset
	 */
	public String getStringByOffset(long off)
	{
		int length;
		int offset = (int) off;
		if((rawbytes[offset] & 0xC0) == 0xC0)
		{
			// 4byte length
			length = ((rawbytes[offset] << 24) & 0x1F000000) | ((rawbytes[offset + 1] << 16) & 0x00FF0000) | ((rawbytes[offset + 2] << 8) & 0x0000FF00) |
					(rawbytes[offset + 3] & 0xFF);
			offset += 4;
		}
		else if((rawbytes[offset] & 0x80) == 0x80)
		{
			length = ((rawbytes[offset] << 8) & 0x3F00) | (rawbytes[offset + 1] & 0xFF);
			offset += 2;
		}
		else
		{
			length = rawbytes[offset];
			offset++;
		}

		String result = "";
		for(int i = 0; (i + i) < length - 1; i++)
		{
			result += (char) (rawbytes[offset + i + i] | (rawbytes[offset + i + i + 1] << 8));
		}
		return result;
	}
}

