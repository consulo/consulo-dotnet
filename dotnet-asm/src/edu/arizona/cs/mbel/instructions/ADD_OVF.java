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

package edu.arizona.cs.mbel.instructions;

/**
 * Add with overflow detection.<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ..., result
 *
 * @author Michael Stepp
 */
public class ADD_OVF extends Instruction
{
	public static final int ADD_OVF = 0xD6;
	public static final int ADD_OVF_UN = 0xD7;
	protected static final int OPCODE_LIST[] = {
			ADD_OVF,
			ADD_OVF_UN
	};

	/**
	 * Makes a ADD_OVF object, possibly unsigned
	 *
	 * @param unsign true iff this is unsigned addition
	 */
	public ADD_OVF(boolean unsign) throws InstructionInitException
	{
		super((unsign ? ADD_OVF_UN : ADD_OVF), OPCODE_LIST);
	}

	public boolean isUnsigned()
	{
		return (getOpcode() == ADD_OVF_UN);
	}

	public String getName()
	{
		return (isUnsigned() ? "add.ovf.un" : "add.ovf");
	}

	public ADD_OVF(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof ADD_OVF));
	}
}
