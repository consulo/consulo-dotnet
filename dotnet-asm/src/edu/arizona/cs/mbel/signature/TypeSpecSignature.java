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
import edu.arizona.cs.mbel.mbel.TypeGroup;

/**
 * This class holds the description of a TypeSpec
 *
 * @author Michael Stepp
 */
public abstract class TypeSpecSignature extends TypeSignature
{
	protected TypeSpecSignature(byte type)
	{
		super(type);
	}

	/**
	 * Factory method for generating a TypeSpecSignature from a binary blob
	 *
	 * @param buffer the ByteBuffer wrapper around the binary blob
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return a TypeSpecSignature representing the binary blob, or null if there was a parse error
	 */
	public static TypeSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		byte data = buffer.peek();
		switch(data)
		{
			case ELEMENT_TYPE_PTR:
				return PointerTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_FNPTR:
				return FunctionPointerTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_ARRAY:
				return ArrayTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_SZARRAY:
				return SZArrayTypeSignature.parse(buffer, group);
			default:
				return null;
		}
	}
}
