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
 * Branch if less than.<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ...
 *
 * @author Michael Stepp
 */
public class BLT extends BranchInstruction
{
	// BLT <int32>
	// BLT.S <int8>
	// BLT.UN <int32>
	// BLT.UN.S <int8>
	public static final int BLT = 0x3F;
	public static final int BLT_S = 0x32;
	public static final int BLT_UN = 0x44;
	public static final int BLT_UN_S = 0x37;
	protected static final int OPCODE_LIST[] = {
			BLT,
			BLT_S,
			BLT_UN,
			BLT_UN_S
	};

	/**
	 * Makes a BLT object with the given target handle, possibly unsigned or in short form
	 *
	 * @param shortF true iff this is a short form instruction
	 * @param un     true iff this is an unsigned comparison
	 * @param ih     the target handle
	 */
	public BLT(boolean shortF, boolean un, InstructionHandle ih) throws InstructionInitException
	{
		super((un ? (shortF ? BLT_UN_S : BLT_UN) : (shortF ? BLT_S : BLT)), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BLT_S || getOpcode() == BLT_UN_S);
	}

	public boolean isUnsignedOrUnordered()
	{
		return (getOpcode() == BLT_UN || getOpcode() == BLT_UN_S);
	}

	public String getName()
	{
		return "blt" + (isUnsignedOrUnordered() ? (isShort() ? ".un.s" : ".un") : (isShort() ? ".s" : ""));
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

	public BLT(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		setTarget((isShort() ? parse.getMSILInputStream().readINT8() : parse.getMSILInputStream().readINT32()));
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BLT));
	}
}
