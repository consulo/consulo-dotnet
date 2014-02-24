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

package edu.arizona.cs.mbel.mbel;

/**
 * This class contains information about the layout of data within a TypeDef.
 * It is analogous to a ClassLayout metadata table. Each TypeDef will have 0 or 1 of these.
 *
 * @author Michael Stepp
 */
public class ClassLayout
{
	private int PackingSize;   // 2 bytes
	private long ClassSize;    // 4 bytes

	/**
	 * Makes a ClassLayout object with the given packing size and class size
	 *
	 * @param pSize the packing size of this type
	 * @param cSize the class size of this type
	 */
	public ClassLayout(int pSize, long cSize)
	{
		PackingSize = pSize;
		ClassSize = cSize;
	}

	/**
	 * Returns the packing size of this type
	 */
	public int getPackingSize()
	{
		return PackingSize;
	}

	/**
	 * Sets the packing size of this type
	 */
	public void setPackingSize(int size)
	{
		PackingSize = size;
	}

	/**
	 * Returns the class size of this type
	 */
	public long getClassSize()
	{
		return ClassSize;
	}

	/**
	 * Sets the class size of this type
	 */
	public void setClassSize(long size)
	{
		ClassSize = size;
	}
   
/*
   public void output(){
      System.out.print("ClassLayout[PackingSize="+PackingSize+", ClassSize="+ClassSize+"]");
   }
*/
}
