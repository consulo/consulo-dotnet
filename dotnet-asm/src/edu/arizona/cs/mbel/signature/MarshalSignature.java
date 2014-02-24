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
 * This class describes .NET field marshalling information
 *
 * @author Michael Stepp
 */
public class MarshalSignature extends Signature implements MarshalSignatureConstants
{
	private byte type;
	// if type==ARRAY
	private byte arrayElemType;
	private int paramNum;
	private int elemMult;
	private int numElem;

	private MarshalSignature()
	{
	}

	/**
	 * Constructs a MarshalSignature for the given native type (not NATIVE_TYPE_ARRAY)
	 *
	 * @param typecode the byte code value of the marshal type (defined in MarshalSignatureConstants)
	 */
	public MarshalSignature(byte typecode) throws SignatureException
	{
		if(typecode == NATIVE_TYPE_ARRAY)
		{
			throw new SignatureException("MarshalSignature: this constructor cannot be used with NATIVE_TYPE_ARRAY");
		}
		type = typecode;
	}

	/**
	 * Constructs a MarshalSignature for an array (NATIVE_ARRAY_TYPE).
	 *
	 * @param elemtype the byte code value of the array element type (defined in MarshalSignatureConstants)
	 * @param pNum     the 1-based index of the parameter in the method signature that holds the number of elements in the array
	 * @param eMult    the element multiplier
	 * @param nElem    the number of elements in the array
	 */
	public MarshalSignature(byte elemtype, int pNum, int eMult, int nElem) throws SignatureException
	{
		if(elemtype == NATIVE_TYPE_ARRAY)
		{
			throw new SignatureException("MarshalSignature: element type cannot be NATIVE_TYPE_ARRAY");
		}
		arrayElemType = elemtype;
		if(pNum < 0)
		{
			throw new SignatureException("MarshalSignature: array size parameter number cannot be <0");
		}
		paramNum = pNum;
		if(eMult <= 0)
		{
			throw new SignatureException("MarshalSignature: element multiplier cannot be <=0");
		}
		elemMult = eMult;
		if(nElem < 0)
		{
			throw new SignatureException("MarshalSignature: number of array elements cannot be<0");
		}
		numElem = nElem;
	}

	/**
	 * Factory method for parsing field marshal signatures
	 *
	 * @param buffer the buffer to read from
	 * @return a MarshalSignature representing the given binary blob, or null if there was a parse error
	 */
	public static MarshalSignature parse(ByteBuffer buffer)
	{
		MarshalSignature blob = new MarshalSignature();

		byte data = buffer.get();
		if(data == NATIVE_TYPE_ARRAY)
		{
			blob.type = data;
			data = buffer.get();
			if((data & 0xFF) >= 1 && (data & 0xFF) <= 0x2d && (data & 0xFF) != 0x29)
			{
				blob.arrayElemType = data;
			}
			else
			{
				return null;
			}

			blob.paramNum = readCodedInteger(buffer);
			blob.elemMult = readCodedInteger(buffer);
			blob.numElem = readCodedInteger(buffer);
		}
		else if((data & 0xFF) >= 1 && (data & 0xFF) <= 0x2d && (data & 0xFF) != 0x29)
		{
			blob.arrayElemType = data;
		}
		else
		{
			return null;
		}
		return blob;
	}

	/**
	 * Returns a status byte representing the type of field marshal this is.
	 * This value will be one of the values defined in MarshalSignatureConstants.
	 */
	public byte getType()
	{
		return type;
	}

	/**
	 * Convenience method to tell whether this field marshal describes an array or not
	 *
	 * @return true if this field marshal describes an array, false otherwise
	 */
	public boolean isArray()
	{
		return (type == NATIVE_TYPE_ARRAY);
	}

	/**
	 * Returns a byte describing the type of array this marshal signature describes
	 *
	 * @return a type byte describing the element type of the array (a value from MarshalSignatureConstants). This will be 0 if this is not an array.
	 */
	public byte getArrayElementType()
	{
		return arrayElemType;
	}

	/**
	 * Returns the parameter index of the array length in the method signature
	 */
	public int getParameterIndex()
	{
		// paramNum tells which parameter in the method
		// signature indicates the number of elements in the array
		return paramNum;
	}

	/**
	 * Returns the factor by which to multiply size values
	 */
	public int elementMultiplier()
	{
		return elemMult;
	}

	/**
	 * Returns the number of elements in the array
	 */
	public int numElements()
	{
		return numElem;
	}

	/**
	 * Write this signature to a buffer in raw binary form
	 *
	 * @param buffer the buffer to write to
	 */
	public void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		if(isArray())
		{
			buffer.put(type);
			buffer.put(arrayElemType);
			byte[] code = encodeInteger(paramNum);
			buffer.put(code);
			code = encodeInteger(elemMult);
			buffer.put(code);
			code = encodeInteger(numElem);
			buffer.put(code);
		}
		else
		{
			buffer.put(type);
		}
	}
}
