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

// used to implement the MethodImpl table
package edu.arizona.cs.mbel.mbel;

/**
 * This class is used to represent the MethodImpl metadata table. It is used when a language
 * does not conform to CLI specs. It is used when a class must implement a method (say, from an
 * interface or abstract superclass) and it gives an explicit mapping from a real method to that
 * method. Each TypeDef will have a vector of these, referring to the Methods it owns.
 *
 * @author Michael Stepp
 */
public class MethodMap
{
	// this class decorates a TypeDef
	private MethodDefOrRef methodDeclaration;
	private MethodDefOrRef methodBody;  // might be defined in this TypeDef, might be in a superclass

	/**
	 * Makes a MethodMap with the given method declaration and method body
	 *
	 * @param decl the method declaration that 'body' is satifying (i.e. an interface method)
	 * @param body the actual method definition, with body and all
	 */
	public MethodMap(MethodDefOrRef decl, MethodDefOrRef body)
	{
		methodDeclaration = decl;
		methodBody = body;
	}

	/**
	 * Returns the method info of the method declaration being satisfied
	 */
	public MethodDefOrRef getMethodDeclaration()
	{
		return methodDeclaration;
	}

	/**
	 * Sets the method declaration for this method map
	 */
	public void setMethodDeclaration(MethodDefOrRef ref)
	{
		methodDeclaration = ref;
	}

	/**
	 * Returns the real method that is satisfying the declaration
	 */
	public MethodDefOrRef getMethodBody()
	{
		return methodBody;
	}

	/**
	 * Sets the method body of this method map
	 */
	public void setMethodBody(MethodDefOrRef ref)
	{
		methodBody = ref;
	}
}
