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
 * Branch not equal or unordered.<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ...
 *
 * @author Michael Stepp
 */
public class BNE_UN extends BranchInstruction
{
	// BNE.UN <int32>
	// BNE.UN.S <int8>
	public static final int BNE_UN = 0x40;
	public static final int BNE_UN_S = 0x33;
	protected static final int OPCODE_LIST[] = {
			BNE_UN,
			BNE_UN_S
	};

	/**
	 * Makes a BNE_UN object withthe given target handle, possibly in short form
	 *
	 * @param shortF true iff this is a short form instruction
	 * @param ih     the target handle
	 */
	public BNE_UN(boolean shortF, InstructionHandle ih) throws InstructionInitException
	{
		super((shortF ? BNE_UN_S : BNE_UN), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BNE_UN_S);
	}

	public String getName()
	{
		return (isShort() ? "bne.un.s" : "bne.un");
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

	public BNE_UN(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		setTarget((isShort() ? parse.getMSILInputStream().readINT8() : parse.getMSILInputStream().readINT32()));
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BNE_UN));
	}
}
