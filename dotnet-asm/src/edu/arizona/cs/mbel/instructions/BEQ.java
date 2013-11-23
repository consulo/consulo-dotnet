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
 * Branch on equals.<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ...
 *
 * @author Michael Stepp
 */
public class BEQ extends BranchInstruction
{
	// BEQ <int32>
	// BEQ.S <int8>
	public static final int BEQ = 0x3B;
	public static final int BEQ_S = 0x2E;
	protected static final int OPCODE_LIST[] = {
			BEQ,
			BEQ_S
	};

	/**
	 * Makes a BEQ object with the given target handle, possibly in short form
	 *
	 * @param shortF true iff this is a short form instruction
	 * @param ih     the target handle
	 */
	public BEQ(boolean shortF, InstructionHandle ih) throws InstructionInitException
	{
		super((shortF ? BEQ_S : BEQ), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BEQ_S);
	}

	public String getName()
	{
		return (isShort() ? "beq.s" : "beq");
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

	public BEQ(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		if(opcode == BEQ)
		{
			setTarget(parse.getMSILInputStream().readINT32());
		}
		else
		{
			setTarget(parse.getMSILInputStream().readINT8());
		}
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BEQ));
	}
}
