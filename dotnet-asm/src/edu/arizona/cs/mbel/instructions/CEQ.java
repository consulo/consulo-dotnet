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
 * Compare equality.<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ..., result
 *
 * @author Michael Stepp
 */
public class CEQ extends Instruction
{
	public static final int CEQ = 0x01FE;
	protected static final int OPCODE_LIST[] = {CEQ};

	public CEQ() throws InstructionInitException
	{
		super(CEQ, OPCODE_LIST);
	}

	public String getName()
	{
		return "ceq";
	}

	public CEQ(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof CEQ));
	}
}
