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
 * This class describes a function pointer type
 *
 * @author Michael Stepp
 */
public class FunctionPointerTypeSignature extends TypeSpecSignature
{
	private MethodSignature methodSig;

	/**
	 * Makes a function pointer type with the given method signature
	 *
	 * @param method the method signature of this function pointer
	 */
	public FunctionPointerTypeSignature(MethodSignature method) throws SignatureException
	{
		super(ELEMENT_TYPE_FNPTR);
		if(method == null)
		{
			throw new SignatureException("FunctionPointerTypeSignature: null method signature given");
		}
		methodSig = method;
	}

	private FunctionPointerTypeSignature()
	{
		super(ELEMENT_TYPE_FNPTR);
	}

	/**
	 * Factory method for parsing a FunctionPointerTypeSignature from a raw binary blob
	 *
	 * @param buffer the buffer to read from
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return a FunctionPointerTypeSignature representing the given blob, or null if there was a parse error
	 */
	public static TypeSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		FunctionPointerTypeSignature blob = new FunctionPointerTypeSignature();
		byte data = buffer.get();
		if(data != ELEMENT_TYPE_FNPTR)
		{
			return null;
		}

		blob.methodSig = MethodSignature.parse(buffer, group);
		if(blob.methodSig == null)
		{
			return null;
		}
		return blob;
	}

	/* Returns the method signature of this function pointer
		*/
	public MethodSignature getMethodSignature()
	{
		return methodSig;
	}
}
