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
 * Convert values.<br>
 * Stack transition:<br>
 * ..., value --> ..., result
 *
 * @author Michael Stepp
 */
public class CONV extends Instruction
{
	public static final int CONV_I1 = 0x67;
	public static final int CONV_I2 = 0x68;
	public static final int CONV_I4 = 0x69;
	public static final int CONV_I8 = 0x6A;
	public static final int CONV_R4 = 0x6B;
	public static final int CONV_R8 = 0x6C;
	public static final int CONV_U1 = 0xD2;
	public static final int CONV_U2 = 0xD1;
	public static final int CONV_U4 = 0x6D;
	public static final int CONV_U8 = 0x6E;
	public static final int CONV_I = 0xD3;
	public static final int CONV_U = 0xE0;
	public static final int CONV_R_UN = 0x76;


	protected static final int OPCODE_LIST[] = {
			CONV_I1,
			CONV_I2,
			CONV_I4,
			CONV_I8,
			CONV_R4,
			CONV_R8,
			CONV_U1,
			CONV_U2,
			CONV_U4,
			CONV_U8,
			CONV_I,
			CONV_U,
			CONV_R_UN
	};

	/**
	 * Makes a CONV object corresponding to the given opcode
	 *
	 * @param op the opcode (must be one of CONV_I1, CONV_I2, etc)
	 */
	public CONV(int op) throws InstructionInitException
	{
		super(op, OPCODE_LIST);
	}

	public String getName()
	{
		String[] str = {
				"conv.i1",
				"conv.i2",
				"conv.i4",
				"conv.i8",
				"conv.u1",
				"conv.u2",
				"conv.u4",
				"conv.u8",
				"conv.r4",
				"conv.r8",
				"conv.i",
				"conv.u",
				"conv.r.un"
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

	public CONV(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof CONV));
	}
}
