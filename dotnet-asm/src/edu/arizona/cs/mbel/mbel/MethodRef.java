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

import edu.arizona.cs.mbel.signature.MethodSignature;

/**
 * Represents a method reference. Method references refer to
 * methods defined in types in other modules/assemblies.
 * This means that the parent type given to the MethodRef constructor will
 * typically be a TypeRef or TypeSpec and not a TypeDef. It legally can
 * be a TypeDef, but in that case it would be more convenient to simply
 * use the appropriate Method object instead of a MethodRef.
 *
 * @author Michael Stepp
 */
public class MethodRef extends MethodDefOrRef
{
	private MethodSignature callsiteSignature;

	/**
	 * Creates a MethodRef with the given name, parent type, and method callsite signature
	 */
	public MethodRef(String name, AbstractTypeReference ref, MethodSignature sig)
	{
		super(name, ref);
		callsiteSignature = sig;
	}

	/**
	 * Returns the method callsite signature for this method reference
	 */
	public MethodSignature getCallsiteSignature()
	{
		return callsiteSignature;
	}

	/**
	 * Sets the method callsite signature for this method reference
	 */
	public void setCallsiteSignature(MethodSignature sig)
	{
		callsiteSignature = sig;
	}
   
/*
   public void output(){
      System.out.print("MethodRef[Name=\""+getName()+"\"");
      System.out.print(", Parent=");
      getParent().output();
      System.out.print(", CallsiteSig=");
      callsiteSignature.output();
      System.out.print("]");
   }
*/
}
