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

import edu.arizona.cs.mbel.signature.MethodSignature;

/**
 * This class represents a typeref for a global method in another module in this assembly.
 * Global methods actually belong to a hidden TypeDef called "<Module>" with namespace "".
 *
 * @author Michael Stepp
 */
public class GlobalMethodRef extends MethodRef
{
	private ModuleRefInfo moduleRef;

	/**
	 * Makes a GlobalMethodRef with the given parent module, name, and signature
	 *
	 * @param mod  information about the parent module of this global method
	 * @param name the name of the method
	 * @param sig  the callsite signature of the method
	 */
	public GlobalMethodRef(ModuleRefInfo mod, String name, MethodSignature sig)
	{
		super(name, new ModuleTypeRef(mod, "", "<Module>"), sig);
		moduleRef = mod;
	}

	/**
	 * Returns the ModuleRefInfo for the parent module of this methodref
	 */
	public ModuleRefInfo getModuleRefInfo()
	{
		return moduleRef;
	}

	/**
	 * This method is ovverridden to do nothing, because the parent
	 * of a global method must always be "<Module>"
	 */
	public void setParent(AbstractTypeReference ref)
	{
		// can't change my parent! :-P
	}
  
/*
   public void output(){
      System.out.print("GlobalMethodRef[Name=\""+getName()+"\", Signature=");
      getCallsiteSignature().output();
      System.out.print(", Parent=<Module>]");
   }
*/
}
