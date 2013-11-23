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
 * Represents a method reference for a CALLI instruction,
 * which has a callsite method signature. This is only used when
 * the called method has a VARARGS signature, so the caller must
 * specify the exact list of arguments.
 *
 * @author Michael Stepp
 */
public class VarargsMethodRef extends MethodRef
{
	private Method method;

	/**
	 * Creates a VarargsMethodRef for the given method, with the given callsite method signature.
	 * The signature should have the VARARG calling convention.
	 */
	public VarargsMethodRef(Method meth, edu.arizona.cs.mbel.signature.MethodSignature callsig)
	{
		super(meth.getName(), meth.getParent(), callsig);
		method = meth;
	}

	/**
	 * Overrides MemberRef.getParent() so that this will always return the
	 * parent of the underlying Method object.
	 */
	public AbstractTypeReference getParent()
	{
		return method.getParent();
	}

	/**
	 * Returns a reference to the method in this methodref
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * Sets the method for this methodref
	 */
	public void setMethod(Method meth)
	{
		method = meth;
		setName(method.getName());
		setParent(method.getParent());
	}
   
/*
   public void output(){
      System.out.print("VarargsMethodRef[");
      method.output();
      System.out.print("]");
   }
*/
}
