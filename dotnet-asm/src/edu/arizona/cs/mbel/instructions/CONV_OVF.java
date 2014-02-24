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
 * Convert value with overflow detection.<br>
 * Stack transition:<br>
 * ..., value --> ..., result
 *
 * @author Michael Stepp
 */
public class CONV_OVF extends Instruction
{
	public static final int CONV_OVF_I1 = 0xB3;
	public static final int CONV_OVF_I2 = 0xB5;
	public static final int CONV_OVF_I4 = 0xB7;
	public static final int CONV_OVF_I8 = 0xB9;
	public static final int CONV_OVF_U1 = 0xB4;
	public static final int CONV_OVF_U2 = 0xB6;
	public static final int CONV_OVF_U4 = 0xB8;
	public static final int CONV_OVF_U8 = 0xBA;
	public static final int CONV_OVF_I = 0xD4;
	public static final int CONV_OVF_U = 0xD5;

	protected static final int OPCODE_LIST[] = {
			CONV_OVF_I1,
			CONV_OVF_I2,
			CONV_OVF_I4,
			CONV_OVF_I8,
			CONV_OVF_U1,
			CONV_OVF_U2,
			CONV_OVF_U4,
			CONV_OVF_U8,
			CONV_OVF_I,
			CONV_OVF_U
	};

	/**
	 * Makes a CONV_OVF object corresponding to the given opcode
	 *
	 * @param op the opcode (must be one of CONV_OVF_I1, CONV_OVF_I2, etc)
	 */
	public CONV_OVF(int op) throws InstructionInitException
	{
		super(op, OPCODE_LIST);
	}

	public String getName()
	{
		String str[] = {
				"conv.ovf.i1",
				"conv.ovf.i2",
				"conv.ovf.i4",
				"conv.ovf.i8",
				"conv.ovf.u1",
				"conv.ovf.u2",
				"conv.ovf.u4",
				"conv.ovf.u8",
				"conv.ovf.i",
				"conv.ovf.u"
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

	public CONV_OVF(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof CONV_OVF));
	}
}
