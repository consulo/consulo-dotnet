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
 * This class represents a Manifest Resource that is embedded in the current module.
 * The resource is given as a raw byte array, and is only accessed by the program through the API.
 *
 * @author Michael Stepp
 */
public class LocalManifestResource extends ManifestResource
{
	private byte[] resource;
	// in file, resource is emitted as (4-btye LE length, resource)
	// with 4-byte padding between resources

	/**
	 * Makes a LocalManifestResource with the given name, flags, and data value
	 *
	 * @param name  the logical name of the resource
	 * @param flags a bit vector of flags (defined in ManifestResourceAttributes)
	 * @param data  the raw bytes of the resource itself
	 */
	public LocalManifestResource(String name, long flags, byte[] data)
	{
		super(name, flags);
		resource = data;
	}

	/**
	 * Returns the resource data as a raw byte array
	 */
	public byte[] getResourceData()
	{
		return resource;
	}

	/**
	 * Comparse 2 LocalManifestResources
	 * Returns true iff super.equals and the raw data is equal
	 */
	public boolean equals(Object o)
	{
		if(!super.equals(o))
		{
			return false;
		}
		if(o == null || !(o instanceof LocalManifestResource))
		{
			return false;
		}

		LocalManifestResource res = (LocalManifestResource) o;
		if(resource.length != res.resource.length)
		{
			return false;
		}

		for(int i = 0; i < resource.length; i++)
		{
			if(res.resource[i] != resource[i])
			{
				return false;
			}
		}
		return true;
	}
   
/*
   public void output(){
      System.out.print("LocalManifestResource[Name=\"" + getName() + "\"]");
   }
*/
}
