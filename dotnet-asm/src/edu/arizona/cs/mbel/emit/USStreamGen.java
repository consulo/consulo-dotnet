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

package edu.arizona.cs.mbel.emit;

/**
 * This is used to construct a #US stream from a Module. Strings are passed to
 * addUserString and are added to this stream, and a token is returned. The only
 * user strings should come from ldstr instructions.
 *
 * @author Michael Stepp
 */
class USStreamGen
{
	private static final int SIZE = 1000;
	private byte[][] data;
	private long length;

	/**
	 * Creates a new US Stream with only the null string ""
	 */
	public USStreamGen()
	{
		data = new byte[2][SIZE];
		data[0][0] = 0;
		length = 1L;
	}

	/**
	 * Returns the token for the given string. If the string is already in the #US stream,
	 * its token is returned. If it is not in this stream, it is added to the end and its new token
	 * is returned. Passing null or "" returns 0.
	 */
	public long addUserString(String str)
	{
		if(str == null)
		{
			return 0L;
		}
		int size = 1 + str.length() * 2;
		byte[] toadd = null;
		if(size <= 0x7FL)
		{
			toadd = new byte[size + 1];
			toadd[0] = (byte) (size & 0xFF);
			for(int i = 0; i < str.length(); i++)
			{
				toadd[i + i + 1] = (byte) (str.charAt(i) & 0xFF);
				toadd[i + i + 2] = (byte) ((str.charAt(i) >> 8) & 0xFF);
			}
			toadd[toadd.length - 1] = 0;
		}
		else if(size <= 0x3FFFL)
		{
			toadd = new byte[size + 2];
			toadd[0] = (byte) (0x80 | (size >> 8));
			toadd[1] = (byte) (size & 0xFF);
			for(int i = 0; i < str.length(); i++)
			{
				toadd[i + i + 2] = (byte) (str.charAt(i) & 0xFF);
				toadd[i + i + 3] = (byte) ((str.charAt(i) >> 8) & 0xFF);
			}
			toadd[toadd.length - 1] = 0;
		}
		else if(size <= 0x1FFFFFFFL)
		{
			toadd = new byte[size + 4];
			toadd[0] = (byte) (0xC0 | (size >> 24));
			toadd[1] = (byte) ((size >> 16) & 0xFF);
			toadd[2] = (byte) ((size >> 8) & 0xFF);
			toadd[3] = (byte) (size & 0xFF);
			for(int i = 0; i < str.length(); i++)
			{
				toadd[i + i + 4] = (byte) (str.charAt(i) & 0xFF);
				toadd[i + i + 5] = (byte) ((str.charAt(i) >> 8) & 0xFF);
			}
			toadd[toadd.length - 1] = 0;
		}
		else
		{
			return -1L;
		}

		long index = findUserString(toadd);
		if(index != -1L)
		{
			return index;
		}

		grow(toadd.length);
		long result = length;
		for(int i = 0; i < toadd.length; i++)
		{
			data[(int) (length / SIZE)][(int) (length % SIZE)] = toadd[i];
			length++;
		}
		return result;
	}

	/**
	 * Returns the length of this #US stream in bytes (as it would be written to disk)
	 */
	public long getLength()
	{
		return length;
	}

	/**
	 * Writes this #US stream to a buffer (should write getLength() bytes)
	 */
	public void emit(edu.arizona.cs.mbel.ByteBuffer buffer)
	{
		long len = length;
		int i = 0;
		while(len >= SIZE)
		{
			buffer.put(data[i]);
			i++;
			len -= SIZE;
		}
		for(int j = 0; j < len; j++)
		{
			buffer.put(data[i][j]);
		}
	}

	private void grow(int by)
	{
		if((length + by) >= data.length * SIZE)
		{
			int rows = (int) (2 + (length + by) / SIZE);
			byte[][] newdata = new byte[rows][];
			for(int i = 0; i < data.length; i++)
			{
				newdata[i] = data[i];
			}
			for(int i = data.length; i < newdata.length; i++)
			{
				newdata[i] = new byte[SIZE];
			}
			data = newdata;
		}
	}

	private long findUserString(byte[] blob)
	{
		if(blob == null || blob.length == 0)
		{
			return -1L;
		}
		byte start = blob[0];
		for(long i = 0; i <= (length - blob.length); i++)
		{
			if(data[(int) (i / SIZE)][(int) (i % SIZE)] == start)
			{
				boolean wrong = false;
				for(int j = 1; j < blob.length; j++)
				{
					if(data[(int) ((i + j) / SIZE)][(int) ((i + j) % SIZE)] != blob[j])
					{
						wrong = true;
						break;
					}
				}
				if(!wrong)
				{
					return i;
				}
			}
		}
		return -1L;
	}
   
/*
   public void output(){
      System.out.print("USStreamGen["+length);
      for (long i=0;i<length;i++){
         if (i%20==0)
            System.out.print("\n  ");
         int b = data[(int)(i/SIZE)][(int)(i%SIZE)]&0xFF;
         if (b<32 || b>127)
            System.out.print("*");
         else
            System.out.print((char)b);
      }
      System.out.print("\n]\n");
   }
*/
}