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
 * Unconditional branch.<br>
 * Stack transition:<br>
 * ... --> ...
 *
 * @author Michael Stepp
 */
public class BR extends BranchInstruction
{
	// BR <int32>
	// BR.S <int8>
	public static final int BR = 0x38;
	public static final int BR_S = 0x2B;
	protected static final int OPCODE_LIST[] = {
			BR,
			BR_S
	};

	/**
	 * Makes a BR object with the given target handle, possibly in short form
	 *
	 * @param shortF true iff this is a short form instruction
	 * @param ih     the target handle
	 */
	public BR(boolean shortF, InstructionHandle ih) throws InstructionInitException
	{
		super((shortF ? BR_S : BR), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BR_S);
	}

	public String getName()
	{
		return (isShort() ? "br.s" : "br");
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

	public BR(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		setTarget((isShort() ? parse.getMSILInputStream().readINT8() : parse.getMSILInputStream().readINT32()));
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BR));
	}
}
