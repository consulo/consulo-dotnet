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

import edu.arizona.cs.mbel.signature.PInvokeAttributes;

/**
 * This class allows a managed .NET Method object to act as a wrapper around an unmanaged
 * DLL method. Upon calling the managed method, the runtime will see the ImplementationMap
 * metadata item and make the correct unmanaged DLL call.
 *
 * @author Michael Stepp
 */
public class ImplementationMap implements PInvokeAttributes
{
	private int MappingFlags;
	private String ImportName;          // name of method in unmanaged DLL
	private ModuleRefInfo ImportScope;  // module containing method

	/**
	 * Makes an ImplementationMap with the given flags, unmanaged method name, and unmanaged module
	 *
	 * @param flags  a bit vector of flags (defined in PInvokeAttributes)
	 * @param name   the name of the unmanaged method this will call
	 * @param module info about the unmanaged module containing this unmanaged method
	 */
	public ImplementationMap(int flags, String name, ModuleRefInfo module)
	{
		MappingFlags = flags;
		ImportName = name;
		ImportScope = module;
	}

	/**
	 * Returns the Mapping Flags for this ImplementationMap (defined in PInvokeAttributes)
	 */
	public int getFlags()
	{
		return MappingFlags;
	}

	/**
	 * Returns the name of the unmanaged method
	 */
	public String getImportName()
	{
		return ImportName;
	}

	/**
	 * Returns the ModuleRefInfo for the unmanaged module containing the unmanaged method
	 */
	public ModuleRefInfo getImportScope()
	{
		return ImportScope;
	}
   
/*
   public void output(){
      System.out.print("ImplementationMap[ImportName=\""+ImportName+"\"");
      System.out.print("]");
   }
*/
}
