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
 * This class represents a TypeRef that refers to a type defined in another module of the same
 * assembly as the module in which it appears.
 *
 * @author Michael Stepp
 */
public class ModuleTypeRef extends TypeRef
{
	private ModuleRefInfo moduleRef;

	/**
	 * Makes a ModuleTypeRef with the given module, namespace, and name.
	 *
	 * @param module a ModuleInfo object with information about the type parent module
	 * @param ns     the namespace of the type
	 * @param name   the name of the type
	 */
	public ModuleTypeRef(ModuleRefInfo module, String ns, String name)
	{
		super(ns, name);
		moduleRef = module;
	}

	/**
	 * Returns the ModuleRefInfo for the parent module of this typeref.
	 */
	public ModuleRefInfo getModuleRefInfo()
	{
		return moduleRef;
	}
   
/*
   public void output(){
      System.out.print("ModuleTypeRef[Name=\""+getName()+"\", Namespace=\""+getNamespace()+"\"");
      System.out.print("]");
   }
*/
}
