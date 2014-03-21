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
import edu.arizona.cs.mbel.mbel.AbstractTypeReference;
import edu.arizona.cs.mbel.mbel.TypeGroup;
import edu.arizona.cs.mbel.metadata.TableConstants;

/**
 * This class describes a class type signature
 *
 * @author Michael Stepp
 */
public class ClassTypeSignature extends TypeSignature
{
	private AbstractTypeReference classType;

	/**
	 * Makes a class signature representing the given type
	 *
	 * @param clazz an mbel reference to the type this signature describes
	 */
	public ClassTypeSignature(AbstractTypeReference clazz) throws SignatureException
	{
		this();
		if(clazz == null)
		{
			throw new SignatureException("ClassTypeSignature: null class given");
		}
		classType = clazz;
	}

	private ClassTypeSignature()
	{
		super(ELEMENT_TYPE_CLASS);
	}

	/**
	 * Factory method for parsing a class signature from a raw binary blob
	 *
	 * @param buffer the buffer to read from
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return a ClassTypeSignature representing the given blob, or null if there was a parse error
	 */
	public static TypeSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		ClassTypeSignature blob = new ClassTypeSignature();
		byte data = buffer.get();
		if(data != ELEMENT_TYPE_CLASS)
		{
			return null;
		}

		int token[] = parseTypeDefOrRefEncoded(buffer);
		if(token[0] == TableConstants.TypeDef)
		{
			blob.classType = group.getTypeDefs()[token[1] - 1];
		}
		else if(token[0] == TableConstants.TypeRef)
		{
			blob.classType = group.getTypeRefs()[token[1] - 1];
		}
		else if(token[0] == TableConstants.TypeSpec)
		{
			blob.classType = group.getTypeSpecs()[token[1] - 1];
		}
		else
		{
			return null;
		}
		return blob;
	}

	/**
	 * Returns a reference to the type this type signature describes
	 */
	public AbstractTypeReference getClassType()
	{
		return classType;
	}
}
