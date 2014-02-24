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
 * This class represents a TypeRef for a type that is defiend within another type.
 * This nested type will have a reference to its parent class.
 *
 * @author Michael Stepp
 */
public class NestedTypeRef extends TypeRef
{
	private TypeRef EnclosingTypeRef;

	/**
	 * Makes a new NestedTypeRef witht he given namespace, name, and parent
	 *
	 * @param ns   the namespace of this type (should equal its parent's namespace)
	 * @param name the name of this type
	 * @param ref  the parent type in which this type is defined
	 */
	public NestedTypeRef(String ns, String name, TypeRef ref)
	{
		super(ns, name);
		EnclosingTypeRef = ref;
	}

	/**
	 * Returns the TypeRef of the type in which this type is defined
	 */
	public TypeRef getEnclosingTypeRef()
	{
		return EnclosingTypeRef;
	}

	/**
	 * Sets the TypeRef of the type in which this type is defined
	 */
	public void setEnclosingTypeRef(TypeRef parent)
	{
		EnclosingTypeRef = parent;
	}
   
/*
   public void output(){
      System.out.print("NestedTypeRef[Name=\""+getName()+"\", Namespace=\""+getNamespace()+"\"");
      System.out.print(", EnclosingTypeRef=");
      EnclosingTypeRef.output();
      System.out.print("]");
   }
*/
}
