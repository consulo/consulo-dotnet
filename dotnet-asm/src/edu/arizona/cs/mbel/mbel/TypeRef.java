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
 * Represents a type reference. Type references refer to types
 * in other assemblies or modules, as well as nested types or even
 * TypeDefs within the same file. All TypeRefs know their name and namespace.
 *
 * @author Michael Stepp
 */
public abstract class TypeRef extends AbstractTypeReference
{
	private long TypeRefRID = -1L;
	private String Name;
	private String Namespace;


	/**
	 * Makes a TypeRef with the given name and namespace.
	 *
	 * @param ns   the namespace of this TypeRef
	 * @param name the name of this TypeRef
	 */
	protected TypeRef(String ns, String name)
	{
		Name = name;
		Namespace = ns;
	}

	/**
	 * Returns the TypeRef RID for this TypeRef (used by emitter)
	 */
	public long getTypeRefRID()
	{
		return TypeRefRID;
	}

	/**
	 * Sets the TypeRef RID for this TypeRef (used by emitter).
	 * This method can only be called once.
	 */
	public void setTypeRefRID(long rid)
	{
		if(TypeRefRID == -1L)
		{
			TypeRefRID = rid;
		}
	}

	/**
	 * Returns the name of this TypeRef.
	 */
	public String getName()
	{
		return Name;
	}

	/**
	 * Sets the name of this TypeRef
	 */
	public void setName(String name)
	{
		Name = name;
	}

	/**
	 * Returns the namespace of this TypeRef
	 */
	public String getNamespace()
	{
		return Namespace;
	}

	@Override
	public String getFullName()
	{
		if(getNamespace() == null || getNamespace().isEmpty())
		{
			return getName();
		}
		return getNamespace() + "." + getName();
	}

	/**
	 * Sets the namespace of this TypeRef
	 */
	public void setNamespace(String ns)
	{
		Namespace = ns;
	}
}
