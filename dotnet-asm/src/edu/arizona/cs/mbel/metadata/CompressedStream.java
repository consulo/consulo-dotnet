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
 * This class represents a #~ or #- metadata stream.
 * These streams contain the metadata tables. A #- stream in "unoptimized" which
 * means that it may have more tables to compensate for its sloppy design.
 *
 * @author Michael Stepp
 */
public class CompressedStream
{
	// #~ or #- stream
	public static final int STRINGS_MASK = 0x01;
	public static final int GUID_MASK = 0x02;
	public static final int BLOB_MASK = 0x04;
	//////////////////////////////////////////////

	public int Major;                  // 1byte
	public int Minor;                  // 1byte
	public int Heaps;                  // 1byte
	/*
		  if Heaps & STRINGS_MASK, String indexes are 4 bytes, else 2
		  if Heaps & GUID_MASK, GUID indexes are 4 bytes, else 2
		  if Heaps & BLOB_MASK, Blob indexes are 4 bytes, else 2
	   */
	public int Rid;           // 1byte
	public long MaskValid;    // 8bytes
	public long Sorted;       // 8bytes
	public long Counts[];     // [64]
	public long tableStartFP; // file pointer of start of tables

	/**
	 * Parses a CompressedStream from an input stream
	 */
	public CompressedStream(MSILInputStream in) throws IOException
	{
		in.readDWORD();   // reserved 4 bytes (==0)
		Major = in.readBYTE();
		Minor = in.readBYTE();
		Heaps = in.readBYTE();
		Rid = in.readBYTE();
		MaskValid = in.readDDWORD();
		Sorted = in.readDDWORD();

		Counts = new long[64];
		for(int i = 0; i < 64; i++)
		{
			if((MaskValid & (1L << i)) != 0)
			{
				Counts[i] = in.readDWORD();
			}
			else
			{
				Counts[i] = 0L;
			}
		}
		tableStartFP = in.getCurrent();
	}

	/**
	 * Returns the number of bytes in a #Strings stream token
	 */
	public int getStringsIndexSize()
	{
		return ((Heaps & STRINGS_MASK) != 0 ? 4 : 2);
	}

	/**
	 * Returns the number of bytes in a #GUID stream token
	 */
	public int getGUIDIndexSize()
	{
		return ((Heaps & GUID_MASK) != 0 ? 4 : 2);
	}

	/**
	 * Returns the number of bytes in a #Blob stream token
	 */
	public int getBlobIndexSize()
	{
		return ((Heaps & BLOB_MASK) != 0 ? 4 : 2);
	}

/*
   public void output(){
      System.out.print("CMDS:{");
      System.out.print("\n  Major = " + Major);
      System.out.print("\n  Minor = " + Minor);
      System.out.print("\n  Heaps = " + Integer.toBinaryString(Heaps));
      System.out.print("\n  Rid = " + Rid);
      System.out.print("\n  MaskValid = " + Long.toBinaryString(MaskValid));
      System.out.print("\n  Sorted = " + Long.toBinaryString(Sorted));
      System.out.print("\n  Counts[64] = { ");
      for (int i=0;i<64;i++){
         if ((MaskValid & (1L<<i)) != 0)
            System.out.print(Counts[i]+" ");
      }
      System.out.print("}\n}");
   }
*/
}
