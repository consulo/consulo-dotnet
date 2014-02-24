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

import edu.arizona.cs.mbel.signature.FieldSignature;

/**
 * Repesents a field reference, as used in an instruction. Field references can
 * be fields defined in this module, another module, or another assembly. They can
 * also be GlobalFieldRefs which reference a global field in another module.
 *
 * @author Michael Stepp
 */
public class FieldRef extends MemberRef
{
	private FieldSignature signature;

	/**
	 * Makes a field reference witht he given name, field signature, and parent type
	 *
	 * @param name the name of the field
	 * @param sig  the field signature
	 * @param par  the parent type in which the field is defined
	 */
	public FieldRef(String name, FieldSignature sig, AbstractTypeReference par)
	{
		super(name, par);
		signature = sig;
	}

	/**
	 * Returns the field signature for this field
	 */
	public FieldSignature getSignature()
	{
		return signature;
	}

	/**
	 * Sets the field signature for this field
	 */
	public void setSignature(FieldSignature sig)
	{
		signature = sig;
	}
   
/*
   public void output(){
      System.out.print("FieldRef[Name=\""+getName()+"\", Signature="+signature);
      System.out.print(", Parent=");
      getParent().output();
      System.out.print("]");
   }
*/
}
