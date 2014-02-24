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

import edu.arizona.cs.mbel.instructions.LoadableType;
import edu.arizona.cs.mbel.signature.BaseCustomAttributeOwner;

/**
 * The abstract parent class of TypeDef, TypeRef, and TypeSpec.
 * These are all the classes that can represent defined types in a module.
 * Note that all of TypeDef, TypeRef, and TypeSpec have tokens that may be loaded by a ldtoken
 * instruction, so this class implements LoadableType.
 *
 * @author Michael Stepp
 */
public abstract class AbstractTypeReference extends BaseCustomAttributeOwner implements LoadableType
{
	//public abstract void output();

	public abstract String getFullName();
}
