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

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.emit.ClassEmitter;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * Branch on false.<br>
 * Stack transition:<br>
 * ..., value --> ...
 *
 * @author Michael Stepp
 */
public class BRFALSE extends BranchInstruction
{
	// BRFALSE <int32>
	// BRFALSE.S <int8>
	// aliases: BRZERO, BRNULL
	public static final int BRFALSE = 0x39;
	public static final int BRFALSE_S = 0x2C;
	protected static final int OPCODE_LIST[] = {
			BRFALSE,
			BRFALSE_S
	};

	/**
	 * Makes a new BRFALSE object with the given branch target, possibly in short form
	 *
	 * @param shortF true iff this is a short form instruction
	 * @param ih     the baranch target handle
	 */
	public BRFALSE(boolean shortF, InstructionHandle ih) throws InstructionInitException
	{
		super((shortF ? BRFALSE_S : BRFALSE), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BRFALSE_S);
	}

	public String getName()
	{
		return (isShort() ? "brfalse.s" : "brfalse");
	}

	public int getLength()
	{
		return (super.getLength() + (isShort() ? 1 : 4));
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		if(isShort())
		{
			buffer.put(getTarget());
		}
		else
		{
			buffer.putINT32(getTarget());
		}
	}

	public BRFALSE(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		setTarget((isShort() ? parse.getMSILInputStream().readINT8() : parse.getMSILInputStream().readINT32()));
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BRFALSE));
	}
}
