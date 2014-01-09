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
 * This class contains all the information used in referencing another module.
 * This class is used in ModuleTypeRef, GlobalMethodRef, GlobalFieldRef, and ImplementationMap.
 * It also directly reflects a ModuleRef metadata table, possibly containing CustomAttributes.
 *
 * @author Michael Stepp
 */
public class ModuleRefInfo extends BaseCustomAttributeOwner
{
	private long ModuleRefRID = -1L;

	private String moduleName;

	/**
	 * Makes a new ModuleRefInfo with the given module name
	 *
	 * @param modName the name of this module
	 */
	public ModuleRefInfo(String modName)
	{
		moduleName = modName;
	}

	/**
	 * Returns the ModuleRef RID for this ModuleRefInfo (used by emitter)
	 */
	public long getModuleRefRID()
	{
		return ModuleRefRID;
	}

	/**
	 * Sets the ModuleRef RID of this ModuleRefInfo (used by emitter)
	 */
	public void setModuleRefRID(long rid)
	{
		if(ModuleRefRID == -1L)
		{
			ModuleRefRID = rid;
		}
	}

	/**
	 * Returns the name of this module
	 */
	public String getModuleName()
	{
		return moduleName;
	}
}
