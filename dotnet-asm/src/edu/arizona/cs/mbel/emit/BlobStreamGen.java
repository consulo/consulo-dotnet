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

import edu.arizona.cs.mbel.ByteBuffer;

/**
 * This class is used to generate a #Blob stream from a parsed Module.
 * The Blob stream packs blobs in the order they are given, and does not
 * contain any duplicate blobs.
 *
 * @author Michael Stepp
 */
class BlobStreamGen
{
	private static final int SIZE = 1000;
	private byte[][] data;
	private long length;

	/**
	 * Creates a BlobStreamGen containing only the null blob {}
	 */
	public BlobStreamGen()
	{
		data = new byte[2][SIZE];
		data[0][0] = 0;
		length = 1L;
	}

	/**
	 * Adds a blob to this BlobStreamGen and returns the blob token for it.
	 * This method first checks to see if the given blob is already in the blob stream.
	 * If so, the blob stream is unaltered an the token of the start of that blob is returned.
	 * If the blob is not in the given stream, it is added to the end of the blob stream and a
	 * new token is returned. If null or byte[0] is passed, 0 is returned (which is still technically accurate).
	 */
	public long addBlob(byte[] blob)
	{
		// adds the blob to the BlobStreamGen and returns a BlobStream token for its location
		// (doesn't add if the blob is already in here somewhere)
		if(blob == null)
		{
			return 0L;
		}
		int size = blob.length;
		byte[] toadd = null;
		if(size <= 0x7FL)
		{
			toadd = new byte[size + 1];
			toadd[0] = (byte) size;
			for(int i = 0; i < size; i++)
			{
				toadd[i + 1] = blob[i];
			}
		}
		else if(size <= 0x3FFFL)
		{
			toadd = new byte[size + 2];
			toadd[0] = (byte) (0x80 | (size >> 8));
			toadd[1] = (byte) (size & 0xFF);
			for(int i = 0; i < size; i++)
			{
				toadd[i + 2] = blob[i];
			}
		}
		else if(size <= 0x1FFFFFFFL)
		{
			toadd = new byte[size + 4];
			toadd[0] = (byte) (0xC0 | (size >> 24));
			toadd[1] = (byte) ((size >> 16) & 0xFF);
			toadd[2] = (byte) ((size >> 8) & 0xFF);
			toadd[3] = (byte) (size & 0xFF);
			for(int i = 0; i < size; i++)
			{
				toadd[i + 4] = blob[i];
			}
		}
		else
		{
			return -1L;
		}

		long index = findBlob(toadd);
		if(index != -1L)
		{
			return index;
		}

		grow(toadd.length);
		long result = length;
		for(byte aToadd : toadd)
		{
			data[(int) (length / SIZE)][(int) (length % SIZE)] = aToadd;
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

	private long findBlob(byte[] blob)
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
	 * Returns the length in bytes of this blob stream (as it would be written on disk)
	 */
	public long getLength()
	{
		return length;
	}

	/**
	 * Writes this blob stream out to a buffer, as it would to a file
	 */
	public void emit(ByteBuffer buffer)
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
      char[] hex = new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
      
      System.out.print("BlobStreamGen[");
      for (long i=0;i<length;i++){
         if (i%20==0)
            System.out.print("\n  ");
         byte b = data[(int)(i/SIZE)][(int)(i%SIZE)];
         System.out.print(hex[(b>>4)&0xF]);
         System.out.print(hex[b&0xF]);
      }
      System.out.print("\n]\n");
   }
*/
}
