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
 * This class describes a custom modifier to be applied to another signature type
 *
 * @author Michael Stepp
 */
public class CustomModifierSignature extends TypeSignature
{
	private AbstractTypeReference type;

	private CustomModifierSignature(boolean optional)
	{
		super(optional ? ELEMENT_TYPE_CMOD_OPT : ELEMENT_TYPE_CMOD_REQD);
	}

	/**
	 * Constructs a new custom modifier with the given type.
	 *
	 * @param optional true if this modifier is optional, false if required
	 * @param ref      the reference to the modifier type
	 */
	public CustomModifierSignature(boolean optional, AbstractTypeReference ref) throws SignatureException
	{
		this(optional);
		type = ref;
		if(type == null)
		{
			throw new SignatureException("CustomModifierSignature: null type reference given");
		}
	}

	/**
	 * Factory method for parsing a custom modifier from a raw binary blob
	 *
	 * @param buffer the buffer to read from
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return a CustomModifierSignature representing the given blob, or null if there was a parse error
	 */
	public static CustomModifierSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		byte data = buffer.get();
		if(!(data == ELEMENT_TYPE_CMOD_REQD || data == ELEMENT_TYPE_CMOD_OPT))
		{
			return null;
		}

		CustomModifierSignature blob = new CustomModifierSignature(data == ELEMENT_TYPE_CMOD_OPT);

		int token[] = parseTypeDefOrRefEncoded(buffer);

		if(token[0] == TableConstants.TypeDef)
		{
			blob.type = group.getTypeDefs()[token[1] - 1];
		}
		else if(token[0] == TableConstants.TypeRef)
		{
			blob.type = group.getTypeRefs()[token[1] - 1];
		}
		else if(token[0] == TableConstants.TypeSpec)
		{
			blob.type = group.getTypeSpecs()[token[1] - 1];
		}
		else
		{
			throw new IllegalArgumentException();
		}
		return blob;
	}

	/**
	 * Returns the type associated with this custom modifier
	 */
	public AbstractTypeReference getInnerType()
	{
		return type;
	}

	/**
	 * Writes this signature out to a buffer in raw binary form
	 *
	 * @param buffer the buffer to write to
	 */
	public void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		buffer.put(elementType);
		long token = emitter.getTypeToken(type);
		byte[] data = makeTypeDefOrRefEncoded((int) ((token >> 24) & 0xFF), (int) (token & 0xFFFFFF));
		buffer.put(data);
	}

/*
   public void output(){
      System.out.print("CustomModifierSignature[");
      if (elementType==ELEMENT_TYPE_CMOD_REQD){
         System.out.print("CMOD_REQD,");
      }else{
         System.out.print("CMOD_OPT,");
      }
      type.output();
      System.out.print("]");
   }
*/
}
