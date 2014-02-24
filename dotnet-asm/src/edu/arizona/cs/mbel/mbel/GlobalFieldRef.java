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

import edu.arizona.cs.mbel.signature.FieldSignature;

/**
 * This class is for global fields defined in other modules of the same assembly.
 * Global fields are actually defined in a hidden TypeDef named "<Module>" with namespace "".
 *
 * @author Michael Stepp
 */
public class GlobalFieldRef extends FieldRef
{
	private ModuleRefInfo moduleRef;

	/**
	 * Makes a GlobalFieldRef with the given module, name, and signature
	 *
	 * @param module information about the module where this global field is defined
	 * @param name   the name of this global field
	 * @param sig    the field signature of this global field
	 */
	public GlobalFieldRef(ModuleRefInfo module, String name, FieldSignature sig)
	{
		super(name, sig, new ModuleTypeRef(module, "", "<Module>"));
		moduleRef = module;
	}

	/**
	 * Returns the module info for this global field
	 */
	public ModuleRefInfo getModuleRefInfo()
	{
		return moduleRef;
	}

	/**
	 * Overrides this method to do nothing, because the parent of a
	 * global field must always be "<Module>"
	 */
	public void setParent(AbstractTypeReference ref)
	{
		// can't change my parent! :-P
	}
   
/*
   public void output(){
      System.out.print("GlobalFieldRef[Name=\""+getName()+"\", Signature=");
      getSignature().output();
      System.out.print(", Parent=<Module>]");
   }
*/
}
