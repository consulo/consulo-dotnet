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

package edu.arizona.cs.mbel.mbel;

/**
 * Represents an on-the-fly type specification. TypeSpecs can be arrays,
 * pointers, or function pointer. A TypeSpec is mostly defined by its internal
 * TypeSpecSignature, which is a subclass of TypeSignature.
 *
 * @author Michael Stepp
 */
public class TypeSpec extends AbstractTypeReference
{
	private long TypeSpecRID = -1L;
	private edu.arizona.cs.mbel.signature.TypeSpecSignature signature;
	private java.util.Vector typeSpecAttributes;

	/**
	 * Makes a TypeSpec with the given TypeSpecSignature
	 */
	public TypeSpec(edu.arizona.cs.mbel.signature.TypeSpecSignature sig)
	{
		this();
		signature = sig;
	}

	protected TypeSpec()
	{
		// donothing constructor
		typeSpecAttributes = new java.util.Vector(10);
	}

	/**
	 * Adds a CustomAttribute to this TypeSpec
	 */
	public void addTypeSpecAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			typeSpecAttributes.add(ca);
		}
	}

	/**
	 * Returns a non-null list of CustomAttributes on this TypeSpec
	 */
	public CustomAttribute[] getTypeSpecAttributes()
	{
		CustomAttribute[] cas = new CustomAttribute[typeSpecAttributes.size()];
		for(int i = 0; i < cas.length; i++)
		{
			cas[i] = (CustomAttribute) typeSpecAttributes.get(i);
		}
		return cas;
	}

	/**
	 * Removes a CustomAttribute from this TypeSpec
	 */
	public void removeTypeSpecAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			typeSpecAttributes.remove(ca);
		}
	}

	/**
	 * Returns the TypeSpec RID for this TypeSpec (used by emitter)
	 */
	public long getTypeSpecRID()
	{
		return TypeSpecRID;
	}

	/**
	 * Sets the TypeSpec RID for this TypeSpec (used by emitter).
	 * This method can only be called once.
	 */
	public void setTypeSpecRID(long RID)
	{
		if(TypeSpecRID == -1L)
		{
			TypeSpecRID = RID;
		}
	}

	/**
	 * Returns the TypeSpecSignature for this TypeSpec.
	 */
	public edu.arizona.cs.mbel.signature.TypeSpecSignature getSignature()
	{
		return signature;
	}

	/**
	 * Sets the TypeSpecSignature for this TypeSpec.
	 */
	public void setSignature(edu.arizona.cs.mbel.signature.TypeSpecSignature sig)
	{
		signature = sig;
	}
   
/*
   public void output(){
      System.out.print("TypeSpec[Signature=");
      signature.output();
      System.out.print("]");
   }
*/
}
