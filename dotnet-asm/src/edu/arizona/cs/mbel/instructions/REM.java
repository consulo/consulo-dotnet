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
 * Remainder (mod).<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ..., result
 *
 * @author Michael Stepp
 */
public class REM extends Instruction
{
	public static final int REM = 0x5D;
	public static final int REM_UN = 0x5E;
	protected static final int OPCODE_LIST[] = {
			REM,
			REM_UN
	};

	/**
	 * Makes a REM object that is possibly unsigned.
	 *
	 * @param un true iff this is an unsigned remainder
	 */
	public REM(boolean un) throws InstructionInitException
	{
		super((un ? REM_UN : REM), OPCODE_LIST);
	}

	public boolean isUnsigned()
	{
		return (getOpcode() == REM_UN);
	}

	public String getName()
	{
		return (isUnsigned() ? "rem.un" : "rem");
	}

	public REM(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof REM));
	}
}
