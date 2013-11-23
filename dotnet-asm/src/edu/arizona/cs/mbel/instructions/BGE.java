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
 * Branch greater than or equal to.<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ...
 *
 * @author Michael Stepp
 */
public class BGE extends BranchInstruction
{
	// BGE <int32>
	// BGE.S <int8>
	// BGE.UN <int32>
	// BGE.UN.S <int8>
	public static final int BGE = 0x3C;
	public static final int BGE_S = 0x2F;
	public static final int BGE_UN = 0x41;
	public static final int BGE_UN_S = 0x34;
	protected static final int OPCODE_LIST[] = {
			BGE,
			BGE_S,
			BGE_UN,
			BGE_UN_S
	};

	/**
	 * Makes a BGE object with the given target handle, possibly unsigned or in short form
	 *
	 * @param shortF true iff this is a short form instruction
	 * @param un     true iff this is an unsigned comparison
	 * @param ih     the target handle
	 */
	public BGE(boolean shortF, boolean un, InstructionHandle ih) throws InstructionInitException
	{
		super((un ? (shortF ? BGE_UN_S : BGE_UN) : (shortF ? BGE_S : BGE)), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BGE_S || getOpcode() == BGE_UN_S);
	}

	public boolean isUnsignedOrUnordered()
	{
		return (getOpcode() == BGE_UN || getOpcode() == BGE_UN_S);
	}

	public String getName()
	{
		return "bge" + (isUnsignedOrUnordered() ? (isShort() ? ".un.s" : ".un") : (isShort() ? ".s" : ""));
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

	public BGE(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		setTarget((isShort() ? parse.getMSILInputStream().readINT8() : parse.getMSILInputStream().readINT32()));
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BGE));
	}
}
