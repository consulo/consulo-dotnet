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

import edu.arizona.cs.mbel.signature.AssemblyFlags;

/**
 * This TypeRef is for types defined in other assemblies. Each AssemblyTypeRef has an
 * AssemblyRefInfo object and a name and namespace.
 *
 * @author Michael Stepp
 */
public class AssemblyTypeRef extends TypeRef implements AssemblyFlags
{
	/**
	 * A TypeRef corresponding to [mscorlib]System.Object. (note this is only usable outside of mscorlib)
	 */
	public static final AssemblyTypeRef OBJECT = new AssemblyTypeRef(AssemblyRefInfo.MSCORLIB, "System", "Object");
	/**
	 * A TypeRef corresponding to [mscorlib]System.String
	 */
	public static final AssemblyTypeRef STRING = new AssemblyTypeRef(AssemblyRefInfo.MSCORLIB, "System", "String");
	/**
	 * A TypeRef corresponding to [mscorlib]System.ValueType
	 */
	public static final AssemblyTypeRef VALUETYPE = new AssemblyTypeRef(AssemblyRefInfo.MSCORLIB, "System", "ValueType");
	//////////////////////////////////////////////////////////////////
	private AssemblyRefInfo assemblyRef;

	/**
	 * Makes an AssemblyTypeRef object with the given assembly, name, and namespace
	 *
	 * @param ref  information about the defining assembly
	 * @param ns   the namespace of the type
	 * @param name the name of the type
	 */
	public AssemblyTypeRef(AssemblyRefInfo ref, String ns, String name)
	{
		super(ns, name);
		assemblyRef = ref;
	}

	/**
	 * Returns the AssemblyRefInfo for this type
	 */
	public AssemblyRefInfo getAssemblyRefInfo()
	{
		return assemblyRef;
	}

	/**
	 * Sets the AssemblyRefInfo for this type
	 */
	public void setAssemblyRefInfo(AssemblyRefInfo ref)
	{
		assemblyRef = ref;
	}
   
/*
   public void output(){
      System.out.print("AssemblyTypeRef[AssemblyName=\""+assemblyRef.getName()+"\", Name=\""+getName() + "\", Namespace=\""+getNamespace()+"\"]");
   }
*/
}
