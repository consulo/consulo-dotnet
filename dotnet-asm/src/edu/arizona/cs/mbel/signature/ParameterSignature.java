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

import java.util.Vector;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.emit.ClassEmitter;
import edu.arizona.cs.mbel.mbel.TypeGroup;

/**
 * This class describes a parameter in a method signature
 *
 * @author Michael Stepp
 */
public class ParameterSignature extends TypeSignature
{
	private Vector customMods;   // CustomModifierSignatures
	private TypeSignature type;

	private ParameterInfo paramInfo;
   /* elementType:      meaning:
	  TYPEONLY(==0)    just Type, no BYREF
      BYREF             BYREF and Type
      TYPEDBYREF        TYPEDBYREF
   */

	/**
	 * Makes a new ParameterSignature object with the given name, flags and type, possibly passed by reference
	 *
	 * @param sig   the type signature of this parameter
	 * @param byref true iff this parameter is passed by reference
	 */
	public ParameterSignature(TypeSignature sig, boolean byref) throws SignatureException
	{
		super(byref ? ELEMENT_TYPE_BYREF : ELEMENT_TYPE_TYPEONLY);
		customMods = new Vector(10);
		type = sig;
		if(type == null)
		{
			throw new SignatureException("ParameterSignature: Null type specified");
		}
	}

	/**
	 * Makes a new ParameterSignature object with the given name and flags, with TYPEDBYREF semantics
	 */
	public ParameterSignature()
	{
		super(ELEMENT_TYPE_TYPEDBYREF);
		customMods = new Vector(10);
	}

	/**
	 * Factory method for parsing a ParameterSignature from a binary blob
	 *
	 * @param buffer the buffer to read from
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return a ParameterSignature representing the given binary blob, or null if there was a parse error
	 */
	public static ParameterSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		ParameterSignature blob = new ParameterSignature();

		int pos = buffer.getPosition();
		CustomModifierSignature temp = CustomModifierSignature.parse(buffer, group);
		while(temp != null)
		{
			blob.customMods.add(temp);
			pos = buffer.getPosition();
			temp = CustomModifierSignature.parse(buffer, group);
		}
		buffer.setPosition(pos);

		byte data = buffer.peek();
		blob.elementType = ELEMENT_TYPE_TYPEONLY;
		if(data == ELEMENT_TYPE_BYREF)
		{
			blob.elementType = data;
			buffer.get();
			blob.type = TypeSignatureParser.parse(buffer, group);
			if(blob.type == null)
			{
				return null;
			}
		}
		else if(data == ELEMENT_TYPE_TYPEDBYREF)
		{
			blob.elementType = data;
			buffer.get();
		}
		else
		{
			// just Type
			blob.type = TypeSignatureParser.parse(buffer, group);
			if(blob.type == null)
			{
				return null;
			}
		}
		return blob;
	}

	/**
	 * Returns the ParamInfo object for this parameter (may be null)
	 */
	public ParameterInfo getParameterInfo()
	{
		return paramInfo;
	}

	/**
	 * Sets the ParamInfo object for this parameter (may be null)
	 */
	public void setParameterInfo(ParameterInfo info)
	{
		paramInfo = info;
	}

	/**
	 * Getter method for the CustomModifiers applied to this signature
	 */
	public CustomModifierSignature[] getCustomModifiers()
	{
		CustomModifierSignature[] sigs = new CustomModifierSignature[customMods.size()];
		for(int i = 0; i < sigs.length; i++)
		{
			sigs[i] = (CustomModifierSignature) customMods.get(i);
		}

		return sigs;
	}
	/**
	 * Getter method for the type of this parameter (can be null)
	 */
	public TypeSignature getInnerType()
	{
		return type;
	}

	/**
	 * Write this signature to a buffer in raw binary form
	 *
	 * @param buffer the buffer to write to
	 */
	public void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		for(Object customMod : customMods)
		{
			((CustomModifierSignature) customMod).emit(buffer, emitter);
		}
		if(elementType == ELEMENT_TYPE_TYPEONLY)
		{
			type.emit(buffer, emitter);
		}
		else if(elementType == ELEMENT_TYPE_BYREF)
		{
			buffer.put(ELEMENT_TYPE_BYREF);
			type.emit(buffer, emitter);
		}
		else if(elementType == ELEMENT_TYPE_TYPEDBYREF)
		{
			buffer.put(ELEMENT_TYPE_TYPEDBYREF);
		}
	}

/*
   public void output(){
      System.out.print("ParameterSignature[");
      for (int i=0;i<customMods.size();i++){
         ((CustomModifierSignature)customMods.get(i)).output();
         System.out.print(',');
      }

      if (elementType == SignatureConstants.ELEMENT_TYPE_TYPEDBYREF){
         System.out.print("TYPEDBYREF]");
      }else if (elementType==ELEMENT_TYPE_BYREF){
         System.out.print("BYREF,");
         type.output();
         System.out.print("]");
      }else{// TYPEONLY
         type.output();
         System.out.print("]");
      }
   }
*/
}
