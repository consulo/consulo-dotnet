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


package edu.arizona.cs.mbel.signature;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.emit.ClassEmitter;

/**
 * This class describes the shape of a general array
 *
 * @author Michael Stepp
 */
public class ArrayShapeSignature extends Signature
{
	private int rank;          // compressed int
	private int[] sizes;       // [numSizes], compressed ints
	private int[] loBounds;    // [numLoBounds], compressed ints

	/**
	 * Makes an array shape with the given rank, dimension sizes, and lowerbounds
	 *
	 * @param Rank     the rank of the array (number of dimensions)
	 * @param Sizes    the sizes of the dimension of the array (may have <Rank elements)
	 * @param LoBounds a list of lowerbounds for the dimension of the array (may have <Rank elements)
	 */
	public ArrayShapeSignature(int Rank, int[] Sizes, int[] LoBounds) throws SignatureException
	{
		rank = Rank;
		if(Sizes == null)
		{
			sizes = new int[0];
		}
		else
		{
			sizes = Sizes;
			for(int size : sizes)
			{
				if(size < 0)
				{
					throw new SignatureException("ArrayShapeSignature: Negative array size given");
				}
			}
		}
		if(LoBounds == null)
		{
			loBounds = new int[0];
		}
		else
		{
			loBounds = LoBounds;
		}
	}

	protected ArrayShapeSignature()
	{
	}

	/**
	 * Factory method for parsing an array shape signature from a raw binary blob
	 *
	 * @param buffer the bufer to read from
	 * @return an ArrayShapeSignature representing the given blob, or null if there was a parse error
	 */
	public static ArrayShapeSignature parse(ByteBuffer buffer)
	{
		ArrayShapeSignature blob = new ArrayShapeSignature();

		blob.rank = readCodedInteger(buffer);
		int numSizes = readCodedInteger(buffer);
		blob.sizes = new int[numSizes];
		for(int i = 0; i < numSizes; i++)
		{
			blob.sizes[i] = readCodedInteger(buffer);
		}

		int numLoBounds = readCodedInteger(buffer);
		blob.loBounds = new int[numLoBounds];
		for(int i = 0; i < numLoBounds; i++)
		{
			blob.loBounds[i] = readCodedInteger(buffer);
		}

		return blob;
	}

	/**
	 * Returns the rank of this array (number of dimensions)
	 */
	public int getRank()
	{
		return rank;
	}

	/**
	 * Returns a list of sizes of the dimensions of this array.
	 * This list may have <Rank entries, and they apply to the first n dimensions
	 * (i.e. you can't specify something like int[3,4,,6])
	 */
	public int[] getSizes()
	{
		return sizes;
	}

	/**
	 * Returns a list of lowerbounds of the dimension of this array.
	 * This list may have <Rank entries, and they apply to the first n dimensions
	 * (i.e. you can't specify something like int[1...5,-1...3, ,0...7])
	 */
	public int[] getLowerBounds()
	{
		return loBounds;
	}

	/**
	 * Writes this signature out to a buffer in raw binary form
	 *
	 * @param buffer the buffer to read from
	 */
	public void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		buffer.put(encodeInteger(rank));
		buffer.put(encodeInteger(sizes.length));
		for(int size : sizes)
		{
			buffer.put(encodeInteger(size));
		}
		buffer.put(encodeInteger(loBounds.length));
		for(int loBound : loBounds)
		{
			buffer.put(encodeInteger(loBound));
		}
	}

	public String toString()
	{
		String result = "ArrayShape[Rank=" + rank + ",NumSizes=" + sizes.length + ",Sizes[]={";
		int lb, size;
		for(int i = 0; i < sizes.length; i++)
		{
			if(i > 0)
			{
				result += ",";
			}
			result += sizes[i];
		}
		result += "},NumLoBounds=" + loBounds.length + ",LoBounds[]={";
		for(int i = 0; i < loBounds.length; i++)
		{
			if(i > 0)
			{
				result += ",";
			}
			result += loBounds[i];
		}

		result += "}]";
		return result;
	}
}
