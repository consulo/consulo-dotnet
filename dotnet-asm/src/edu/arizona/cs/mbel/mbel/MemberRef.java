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

import edu.arizona.cs.mbel.signature.BaseCustomAttributeOwner;

/**
 * Abstract parent of all member references. A member reference will
 * either refer to a field or a method. MemberRefs are specifically used in the IL
 * instructions that manipulate fields and methods.
 *
 * @author Michael Stepp
 */
public abstract class MemberRef extends BaseCustomAttributeOwner
{
	private long MemberRefRID = -1L;
	private String Name; // field or method name
	private AbstractTypeReference parent;

	/**
	 * Creates a new MemberRef with the given name and parent type
	 */
	protected MemberRef(String name, AbstractTypeReference ref)
	{
		Name = name;
		parent = ref;
	}

	/**
	 * Returns the MemberRef RID of this MemberRef (used by emitter)
	 */
	public long getMemberRefRID()
	{
		return MemberRefRID;
	}

	/**
	 * Sets the MemberRef RID of this MemberRef (used by emitter).
	 * Can only be called once.
	 */
	public void setMemberRefRID(long rid)
	{
		if(MemberRefRID == -1L)
		{
			MemberRefRID = rid;
		}
	}

	/**
	 * Returns the name of this MemberRef (will be either a field name or a method name)
	 */
	public String getName()
	{
		return Name;
	}

	/**
	 * Sets the name of this MemberRef
	 */
	public void setName(String name)
	{
		Name = name;
	}

	/**
	 * Returns the parent type of this MemberRef
	 */
	public AbstractTypeReference getParent()
	{
		return parent;
	}

	/**
	 * Sets the parent type of this MemberRef
	 */
	public void setParent(AbstractTypeReference ref)
	{
		parent = ref;
	}

	//   public abstract void output();
}
