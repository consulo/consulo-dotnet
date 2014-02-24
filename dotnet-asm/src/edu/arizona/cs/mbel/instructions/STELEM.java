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

import java.io.IOException;

import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * Store array element.<br>
 * Stack transition:<br>
 * ..., array, index, value --> ...
 *
 * @author Michael Stepp
 */
public class STELEM extends Instruction
{
	public static final int STELEM_I1 = 0x9C;
	public static final int STELEM_I2 = 0x9D;
	public static final int STELEM_I4 = 0x9E;
	public static final int STELEM_I8 = 0x9F;
	public static final int STELEM_R4 = 0xA0;
	public static final int STELEM_R8 = 0xA1;
	public static final int STELEM_I = 0x9B;
	public static final int STELEM_REF = 0xA2;
	protected static final int OPCODE_LIST[] = {
			STELEM_I1,
			STELEM_I2,
			STELEM_I4,
			STELEM_I8,
			STELEM_R4,
			STELEM_R8,
			STELEM_I,
			STELEM_REF
	};

	/**
	 * Makes a STELEM object with the given opcode
	 *
	 * @param op the opcode (must be one of STELEM_I1, STELEM_I2, etc)
	 */
	public STELEM(int op) throws InstructionInitException
	{
		super(op, OPCODE_LIST);
	}

	public String getName()
	{
		String str[] = {
				"stelem.i1",
				"stelem.i2",
				"stelem.i4",
				"stelem.i8",
				"stelem.r4",
				"stelem.r8",
				"stelem.i",
				"stelem.ref"
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

	public STELEM(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof STELEM));
	}
}
