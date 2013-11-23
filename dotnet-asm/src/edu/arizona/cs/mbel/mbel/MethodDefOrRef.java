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
 * Abstract parent class of Method and MethodRef. Method does not extend MethodRef because
 * a MethodRef represents a calling point, whereas a Method represents a method definition.
 * Therefore each MethodRef will have a callsite signature, and each Method will have a
 * definition signature. It would be wrong to give a Method a callsite signature, since the
 * same method may be called in several different ways if it has a VARARGS signature.
 *
 * @author Michael Stepp
 */
public abstract class MethodDefOrRef extends MemberRef
{
	/**
	 * Makes a MethodDefOrRef withthe given name and parent type
	 */
	public MethodDefOrRef(String name, AbstractTypeReference par)
	{
		super(name, par);
	}

	//   public abstract void output();
}
