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
 * Convert values with overflow detection (unsigned).<br>
 * Stack transition:<br>
 * ..., value --> ..., result
 *
 * @author Michael Stepp
 */
public class CONV_OVF_UN extends Instruction
{
	public static final int CONV_OVF_I1_UN = 0x82;
	public static final int CONV_OVF_I2_UN = 0x83;
	public static final int CONV_OVF_I4_UN = 0x84;
	public static final int CONV_OVF_I8_UN = 0x85;
	public static final int CONV_OVF_U1_UN = 0x86;
	public static final int CONV_OVF_U2_UN = 0x87;
	public static final int CONV_OVF_U4_UN = 0x88;
	public static final int CONV_OVF_U8_UN = 0x89;
	public static final int CONV_OVF_I_UN = 0x8A;
	public static final int CONV_OVF_U_UN = 0x8B;
	protected static final int OPCODE_LIST[] = {
			CONV_OVF_I1_UN,
			CONV_OVF_I2_UN,
			CONV_OVF_I4_UN,
			CONV_OVF_I8_UN,
			CONV_OVF_U1_UN,
			CONV_OVF_U2_UN,
			CONV_OVF_U4_UN,
			CONV_OVF_U8_UN,
			CONV_OVF_I_UN,
			CONV_OVF_U_UN
	};

	/**
	 * Makes a CONV_OVF_UN object corresponding to the given opcode
	 *
	 * @param op the opcode (must be one of CONV_OVF_I1_UN, CONV_OVF_I2_UN, etc)
	 */
	public CONV_OVF_UN(int op) throws InstructionInitException
	{
		super(op, OPCODE_LIST);
	}

	public String getName()
	{
		String str[] = {
				"conv.ovf.i1.un",
				"conv.ovf.i2.un",
				"conv.ovf.i4.un",
				"conv.ovf.i8.un",
				"conv.ovf.u1.un",
				"conv.ovf.u2.un",
				"conv.ovf.u4.un",
				"conv.ovf.u8.un",
				"conv.ovf.i.un",
				"conv.ovf.u.un"
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

	public CONV_OVF_UN(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof CONV_OVF_UN));
	}
}
