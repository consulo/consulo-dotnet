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
 * Branch if true.<br>
 * Stack transition:<br>
 * ..., value --> ...
 *
 * @author Michael Stepp
 */
public class BRTRUE extends BranchInstruction
{
	// BRTRUE <int32>
	// BRTRUE.S <int8>
	// alias: BRINST
	public static final int BRTRUE = 0x3A;
	public static final int BRTRUE_S = 0x2D;
	protected static final int OPCODE_LIST[] = {
			BRTRUE,
			BRTRUE_S
	};

	/**
	 * Makes a BRTRUE object with the given branch target, possibly in short form
	 *
	 * @param shortF true iff this is a short form instruction
	 * @param ih     the branch target handle
	 */
	public BRTRUE(boolean shortF, InstructionHandle ih) throws InstructionInitException
	{
		super((shortF ? BRTRUE_S : BRTRUE), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BRTRUE_S);
	}

	public String getName()
	{
		return (isShort() ? "brtrue.s" : "brtrue");
	}

	public int getLength()
	{
		return (super.getLength() + (isShort() ? 1 : 4));
	}

	protected void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
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

	public BRTRUE(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		setTarget((isShort() ? parse.getMSILInputStream().readINT8() : parse.getMSILInputStream().readINT32()));
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BRTRUE));
	}
}
