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
 * Load array element.<br>
 * Stack transition:<br>
 * ..., array, index --> ..., value
 *
 * @author Michael Stepp
 */
public class LDELEM extends Instruction
{
	public static final int LDELEM_I1 = 0x90;
	public static final int LDELEM_I2 = 0x92;
	public static final int LDELEM_I4 = 0x94;
	public static final int LDELEM_I8 = 0x96;
	public static final int LDELEM_U1 = 0x91;
	public static final int LDELEM_U2 = 0x93;
	public static final int LDELEM_U4 = 0x95;
	public static final int LDELEM_R4 = 0x98;
	public static final int LDELEM_R8 = 0x99;
	public static final int LDELEM_I = 0x97;
	public static final int LDELEM_REF = 0x9A;

	protected static final int OPCODE_LIST[] = {
			LDELEM_I1,
			LDELEM_I2,
			LDELEM_I4,
			LDELEM_I8,
			LDELEM_U1,
			LDELEM_U2,
			LDELEM_U4,
			LDELEM_R4,
			LDELEM_R8,
			LDELEM_I,
			LDELEM_REF
	};

	/**
	 * Makes a LDELEM object corresponding to the given opcode.
	 *
	 * @param op the opcode (must be one of LDELEM_I1, LDELEM_I2, etc)
	 */
	public LDELEM(int op) throws InstructionInitException
	{
		super(op, OPCODE_LIST);
	}

	public String getName()
	{
		String str[] = {
				"ldelem.i1",
				"ldelem.i2",
				"ldelem.i4",
				"ldelem.i8",
				"ldelem.u1",
				"ldelem.u2",
				"ldelem.u4",
				"ldelem.r4",
				"ldelem.r8",
				"ldelem.i",
				"ldelem.ref"
		};
		for(int i = 0; i < str.length; i++)
		{
			if(getOpcode() == OPCODE_LIST[i])
			{
				return str[i];
			}
		}
		return "";
	}

	public LDELEM(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof LDELEM));
	}
}
