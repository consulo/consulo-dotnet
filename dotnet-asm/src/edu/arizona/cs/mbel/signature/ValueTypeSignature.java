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
import edu.arizona.cs.mbel.mbel.AbstractTypeReference;
import edu.arizona.cs.mbel.mbel.TypeGroup;
import edu.arizona.cs.mbel.metadata.TableConstants;

/**
 * This class represents a ValueType type signature.
 *
 * @author Michael Stepp
 */
public class ValueTypeSignature extends TypeSignature
{
	private AbstractTypeReference valueType;

	/**
	 * Constructs a ValueTypeSignature representing the given ValueType
	 *
	 * @param value an AbstractTypeReference representing a ValueType
	 * @throws SignatureException if value is not a ValueType or is null
	 */
	public ValueTypeSignature(AbstractTypeReference value) throws SignatureException
	{
		this();
		if(value == null)
		{
			throw new SignatureException("ValueTypeSignature: Given class null");
		}
		valueType = value;
	}

	private ValueTypeSignature()
	{
		super(ELEMENT_TYPE_VALUETYPE);
	}

	/**
	 * Factory method to get a ValueTypeSignature from a binary blob (called by TypeSignature.parse)
	 *
	 * @param buffer the buffer wrapped around the binary blob
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return the ValueTypeSignature representing the binary blob, or null if there was a parse error
	 */
	public static TypeSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		ValueTypeSignature blob = new ValueTypeSignature();
		byte data = buffer.get();
		if(data != ELEMENT_TYPE_VALUETYPE)
		{
			return null;
		}

		int token[] = parseTypeDefOrRefEncoded(buffer);
		if(token[0] == TableConstants.TypeDef)
		{
			blob.valueType = group.getTypeDefs()[token[1] - 1];
		}
		else if(token[0] == TableConstants.TypeRef)
		{
			blob.valueType = group.getTypeRefs()[token[1] - 1];
		}
		else if(token[0] == TableConstants.TypeSpec)
		{
			blob.valueType = group.getTypeSpecs()[token[1] - 1];
		}
		else
		{
			return null;
		}
		return blob;
	}

	public void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		buffer.put(ELEMENT_TYPE_VALUETYPE);
		long token = emitter.getTypeToken(valueType);
		byte[] data = makeTypeDefOrRefEncoded((int) ((token >> 24) & 0xFF), (int) (token & 0xFFFFFF));
		buffer.put(data);
	}

	/**
	 * Getter method for the ValueType reference
	 */
	public AbstractTypeReference getValueType()
	{
		return valueType;
	}
   
/*
   public void output(){
      System.out.print("ValueTypeSignature[ValueType=");
      valueType.output();
      System.out.print("]");
   }
*/
}
