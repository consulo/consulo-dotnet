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
 * This class is just a container for the various types defined in a module.
 * It is used by the parser to pass around all the types, without glutting each method's
 * signature with 3 extra params. NOT TO BE USED BY USER.
 *
 * @author Michael Stepp
 */
public class TypeGroup
{
	private TypeDef[] typeDefs;
	private TypeRef[] typeRefs;
	private TypeSpec[] typeSpecs;

	/**
	 * Makes a new TypeGroup for the given defs, refs, and specs.
	 *
	 * @param def  the list of TypeDefs in a module
	 * @param ref  the list of TypeRefs in a module
	 * @param spec the list of TypeSpecs in a module
	 */
	public TypeGroup(TypeDef[] def, TypeRef[] ref, TypeSpec[] spec)
	{
		typeDefs = def;
		typeRefs = ref;
		typeSpecs = spec;
	}

	/**
	 * Returns the TypeDef list
	 */
	public TypeDef[] getTypeDefs()
	{
		return typeDefs;
	}

	/**
	 * Returns the TypeRef list
	 */
	public TypeRef[] getTypeRefs()
	{
		return typeRefs;
	}

	/**
	 * Returns the TypeSpec list
	 */
	public TypeSpec[] getTypeSpecs()
	{
		return typeSpecs;
	}
}
