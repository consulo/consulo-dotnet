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
 * This class represents the description of variables types
 * that are used in the mbel Fields. The primitive types are singletons
 * (i.e. cannot be re-instantiated), so a simple == comparsison will tell if they
 * are equal. Note the simple types for STRING and OBJECT. These are treated as special
 * cases by the runtime, and have their own innate type value.
 *
 * @author Michael Stepp
 */
public class TypeSignature extends Signature
{
	public static final TypeSignature BOOLEAN = new TypeSignature(ELEMENT_TYPE_BOOLEAN);
	public static final TypeSignature CHAR = new TypeSignature(ELEMENT_TYPE_CHAR);
	public static final TypeSignature I1 = new TypeSignature(ELEMENT_TYPE_I1);
	public static final TypeSignature U1 = new TypeSignature(ELEMENT_TYPE_U1);
	public static final TypeSignature I2 = new TypeSignature(ELEMENT_TYPE_I2);
	public static final TypeSignature U2 = new TypeSignature(ELEMENT_TYPE_U2);
	public static final TypeSignature I4 = new TypeSignature(ELEMENT_TYPE_I4);
	public static final TypeSignature U4 = new TypeSignature(ELEMENT_TYPE_U4);
	public static final TypeSignature I8 = new TypeSignature(ELEMENT_TYPE_I8);
	public static final TypeSignature U8 = new TypeSignature(ELEMENT_TYPE_U8);
	public static final TypeSignature R4 = new TypeSignature(ELEMENT_TYPE_R4);
	public static final TypeSignature R8 = new TypeSignature(ELEMENT_TYPE_R8);
	public static final TypeSignature I = new TypeSignature(ELEMENT_TYPE_I);
	public static final TypeSignature U = new TypeSignature(ELEMENT_TYPE_U);
	public static final TypeSignature STRING = new TypeSignature(ELEMENT_TYPE_STRING);
	public static final TypeSignature OBJECT = new TypeSignature(ELEMENT_TYPE_OBJECT);
	////////////////////////////////////////////////////

	private byte elementType;

	/**
	 * Constructor for simple types. Simple types are completely identified
	 * by a single byte value.
	 *
	 * @param elemType The code value for this type
	 */
	protected TypeSignature(byte elemType)
	{
		elementType = elemType;
	}

	/**
	 * Returns the single-byte code value for this type
	 */
	public byte getType()
	{
		return elementType;
	}

	/**
	 * Writes this type back to its binary equivalent, into a ByteBuffer.
	 *
	 * @param buffer the buffer into which the signature will be written
	 */
	public void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		buffer.put(elementType);
	}

/*
   public void output(){
      System.out.print(this);
   }
*/

	public String toString()
	{
		String result = "TypeSignature[";

		switch(elementType)
		{
			case ELEMENT_TYPE_BOOLEAN:
				result += "BOOLEAN";
				break;
			case ELEMENT_TYPE_CHAR:
				result += "CHAR";
				break;
			case ELEMENT_TYPE_I1:
				result += "I1";
				break;
			case ELEMENT_TYPE_U1:
				result += "U1";
				break;
			case ELEMENT_TYPE_I2:
				result += "I2]";
				break;
			case ELEMENT_TYPE_U2:
				result += "U2";
				break;
			case ELEMENT_TYPE_I4:
				result += "I4";
				break;
			case ELEMENT_TYPE_U4:
				result += "U4";
				break;
			case ELEMENT_TYPE_I8:
				result += "I8";
				break;
			case ELEMENT_TYPE_U8:
				result += "U8";
				break;
			case ELEMENT_TYPE_R4:
				result += "R4";
				break;
			case ELEMENT_TYPE_R8:
				result += "R8";
				break;
			case ELEMENT_TYPE_I:
				result += "I";
				break;
			case ELEMENT_TYPE_U:
				result += "U";
				break;
			case ELEMENT_TYPE_STRING:
				result += "STRING";
				break;
			case ELEMENT_TYPE_OBJECT:
				result += "OBJECT";
				break;

			default:
				result += elementType;
				break;
		}
		result += "]";
		return result;
	}
}
