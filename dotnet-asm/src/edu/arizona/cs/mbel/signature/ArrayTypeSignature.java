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
import edu.arizona.cs.mbel.mbel.TypeGroup;

/**
 * This class describes an array type
 *
 * @author Michael Stepp
 */
public class ArrayTypeSignature extends TypeSpecSignature
{
	private TypeSignature elementTypeSignature;
	private ArrayShapeSignature arrayShape;

	/**
	 * Makes an array type with the given element type and shape
	 *
	 * @param type  the element type of this array
	 * @param shape the shape of this array
	 */
	public ArrayTypeSignature(TypeSignature type, ArrayShapeSignature shape) throws SignatureException
	{
		this();
		if(type == null || shape == null)
		{
			throw new SignatureException("ArrayTypeSignature: null values specified");
		}
		elementTypeSignature = type;
		arrayShape = shape;
	}

	private ArrayTypeSignature()
	{
		super(ELEMENT_TYPE_ARRAY);
	}

	/**
	 * Factory method for parsing array type signatures from raw binary blobs
	 *
	 * @param buffer the buffer to read from
	 * @param group  a TyprGroup for reconciling tokens to mbel references
	 * @return an ArrayTypeSignature representing the given blob, or null if there was a parse error
	 */
	public static TypeSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		ArrayTypeSignature blob = new ArrayTypeSignature();
		byte data = buffer.get();
		if(data != ELEMENT_TYPE_ARRAY)
		{
			return null;
		}

		blob.elementTypeSignature = TypeSignatureParser.parse(buffer, group);
		if(blob.elementTypeSignature == null)
		{
			return null;
		}

		blob.arrayShape = ArrayShapeSignature.parse(buffer);
		if(blob.arrayShape == null)
		{
			return null;
		}
		return blob;
	}

	/**
	 * Returns the element type of this array
	 */
	public TypeSignature getElementType()
	{
		return elementTypeSignature;
	}

	/**
	 * Returns the shape of this array
	 */
	public ArrayShapeSignature getArrayShape()
	{
		return arrayShape;
	}

	/**
	 * Writes this signature to a buffer in raw binary form
	 *
	 * @param buffer the buffer to write to
	 */
	public void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		buffer.put(ELEMENT_TYPE_ARRAY);
		elementTypeSignature.emit(buffer, emitter);
		arrayShape.emit(buffer, emitter);
	}
   
/*
   public void output(){
      System.out.print("ArrayTypeSignature[");
      elementTypeSignature.output();
      System.out.print(", "+arrayShape+"]");
   }
*/
}
