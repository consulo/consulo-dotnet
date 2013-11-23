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
 * This class is used to construct a #Strings stream from a Module.
 * A string is passed in using addString, and its token value is returned.
 *
 * @author Michael Stepp
 */
class StringsStreamGen
{
	private static final int SIZE = 1000;
	private byte[][] data;
	private long length;

	/**
	 * Creates a new StringsStreamGen that contains only the empty string ""
	 */
	public StringsStreamGen()
	{
		data = new byte[2][SIZE];
		data[0][0] = 0;
		length = 1L;
	}

	/**
	 * Returns the token for ths given string. If this string is already in this strings stream,
	 * its token is returned. If not, it is added and its new token is returned.
	 * If str==null or str.length()==0, 0 is returned.
	 */
	public long addString(String str)
	{
		if(str == null)
		{
			return 0L;
		}
		byte blob[] = new byte[str.length() + 1];
		for(int i = 0; i < str.length(); i++)
		{
			blob[i] = (byte) (str.charAt(i) & 0xFF);
		}
		blob[str.length()] = 0;

		long index = findString(blob);
		if(index != -1L)
		{
			return index;
		}

		grow(blob.length);
		long result = length;
		for(int i = 0; i < blob.length; i++)
		{
			data[(int) (length / SIZE)][(int) (length % SIZE)] = blob[i];
			length++;
		}
		return result;
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

	private long findString(byte[] blob)
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

	/**
	 * Returns the length in bytes of this Strings stream
	 */
	public long getLength()
	{
		return length;
	}

	/**
	 * Writes this strings stream out to a buffer (should write getLength() bytes)
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
   
/*
   public void output(){
      System.out.print("StringsStreamGen["+length);
      for (long i=0;i<length;i++){
         if (i%20==0)
            System.out.print("\n  ");
         byte b = data[(int)(i/SIZE)][(int)(i%SIZE)];
         System.out.print((char)(b&0xFF));
      }
      System.out.print("\n]\n");
   }
*/
}
