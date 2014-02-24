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
 * Represents a ManifestResource defined in another assembly.
 * The only information needed is the Assembly reference
 * and the name of the resource, with some flags.
 *
 * @author Michael Stepp
 */
public class AssemblyManifestResource extends ManifestResource
{
	private AssemblyRefInfo assemblyRef;

	/**
	 * Makes a new ManifestResource for the given assembly
	 *
	 * @param name  the logical resource name
	 * @param flags a bit vector fo flags (defined in ManifestResourceAttributes)
	 * @param ref   the Asembly reference for the assembly containting this resource
	 */
	public AssemblyManifestResource(String name, long flags, AssemblyRefInfo ref)
	{
		super(name, flags);
		assemblyRef = ref;
	}

	/**
	 * Returns the Assembly info for the assembly containing this resource
	 */
	public AssemblyRefInfo getAssemblyRefInfo()
	{
		return assemblyRef;
	}

	/**
	 * Compares 2 AssemblyManifestResources.
	 * Returns true iff their AssemblyRefInfo are equal
	 */
	public boolean equals(Object o)
	{
		if(!super.equals(o))
		{
			return false;
		}

		if(!(o instanceof AssemblyManifestResource))
		{
			return false;
		}

		AssemblyManifestResource res = (AssemblyManifestResource) o;

		return assemblyRef.equals(res.assemblyRef);
	}
   
/*
   public void output(){
      System.out.print("AssemblyManifestResource[Name=\""+getName()+"\", AssemblyName=\""+assemblyRef.getName()+"\"]");
   }
*/
}
