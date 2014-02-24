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
 * This class is used to construct  #GUID stream from a Module.
 * GUIDs are passed in using addGUID, and a 1-based GUID index is returned.
 *
 * @author Michael Stepp
 */
class GUIDStreamGen
{
	private byte[][] data;
	private int numguids;

	/**
	 * Creates a new GUID stream that is empty.
	 */
	public GUIDStreamGen()
	{
		data = new byte[10][16];
		numguids = 0;
	}

	/**
	 * Returns the GUID index of the given GUID.
	 * If the given GUID is already in this GUID stream, its index is returned.
	 * If it is not, it is added to this GUID stream and its new idnex is returned.
	 * If guid==null or guid.length!=16, 0 is returned (invalid)
	 */
	public long addGUID(byte[] guid)
	{
		// remember that these are 1-based indexes!
		if(guid == null || guid.length != 16)
		{
			return 0L;
		}

		long index = findGUID(guid);
		if(index != -1L)
		{
			return index;
		}

		grow();
		long result = (long) (numguids + 1);
		for(int j = 0; j < 16; j++)
		{
			data[numguids][j] = guid[j];
		}
		numguids++;
		return result;
	}

	private void grow()
	{
		if(numguids >= data.length)
		{
			byte[][] newdata = new byte[data.length + 10][];
			for(int i = 0; i < data.length; i++)
			{
				newdata[i] = data[i];
			}
			for(int i = data.length; i < newdata.length; i++)
			{
				newdata[i] = new byte[16];
			}
			data = newdata;
		}
	}

	private long findGUID(byte[] guid)
	{
		boolean wrong;
		for(int i = 0; i < numguids; i++)
		{
			wrong = false;
			for(int j = 0; j < 16; j++)
			{
				if(data[i][j] != guid[j])
				{
					wrong = true;
					break;
				}
			}
			if(!wrong)
			{
				return (long) (i + 1);
			}
		}
		return -1L;
	}

	/**
	 * Returns the number of GUIDs in this GUID stream (note this is not equal to the size in bytes)
	 */
	public int getNumGUIDS()
	{
		return numguids;
	}

	/**
	 * Writes out this GUID stream to a buffer.
	 * Should write getNumGUIDs()*16 bytes.
	 */
	public void emit(ByteBuffer buffer)
	{
		for(int i = 0; i < numguids; i++)
		{
			buffer.put(data[i]);
		}
	}
   
/*
   public void output(){
      char hex[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
      System.out.println("GUIDStreamGen[");
      for (int i=0;i<numguids;i++){
         System.out.print("  ");
         for (int j=0;j<16;j++){
            System.out.print(hex[(data[i][j]>>4)&0xF]);
            System.out.print(hex[data[i][j]&0xF]);
         }
         System.out.println();
      }
      System.out.println("]");
   }
*/
}
