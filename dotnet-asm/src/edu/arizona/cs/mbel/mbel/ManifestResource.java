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
import edu.arizona.cs.mbel.signature.ManifestResourceAttributes;

/**
 * Represents a managed resource. Managed resources can be embedded in the current module,
 * or found in another assembly, or in a File that is reference by this module.
 * Resources all have a logical name associated with them.
 *
 * @author Michael Stepp
 */
public abstract class ManifestResource extends BaseCustomAttributeOwner implements ManifestResourceAttributes
{
	private long Flags;
	private String Name;

	/**
	 * Makes a new ManifestResource with the given name and flags
	 *
	 * @param name  the logical name of this resource
	 * @param flags a bit vector of flags (defined in ManifestResourceAttributes)
	 */
	protected ManifestResource(String name, long flags)
	{
		Name = name;
		Flags = flags;
	}

	/**
	 * Returns the logical name of this ManifestResource
	 */
	public String getName()
	{
		return Name;
	}

	/**
	 * Sets the logical name of this ManifestResource
	 */
	public void setName(String name)
	{
		Name = name;
	}

	/**
	 * Returns the bit vector of flags for this resource (defined in ManifestResourceAttributes)
	 */
	public long getFlags()
	{
		return Flags;
	}

	/**
	 * Compares 2 ManifestResources.
	 * Returns true iff the name and flags are equal
	 */
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof ManifestResource))
		{
			return false;
		}
		ManifestResource res = (ManifestResource) o;
		return (Name.equals(res.Name) && Flags == res.Flags);
	}

	//   public abstract void output();
}
