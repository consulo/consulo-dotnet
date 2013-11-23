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
 * Branch on greater than.<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ...
 *
 * @author Michael Stepp
 */
public class BGT extends BranchInstruction
{
	// BGT <int32>
	// BGT.S <int8>
	// BGT.UN <int32>
	// BGT.UN.S <int8>
	public static final int BGT = 0x3D;
	public static final int BGT_S = 0x30;
	public static final int BGT_UN = 0x42;
	public static final int BGT_UN_S = 0x35;
	protected static final int OPCODE_LIST[] = {
			BGT,
			BGT_S,
			BGT_UN,
			BGT_UN_S
	};

	/**
	 * Makes a BGT object with the given target handle, possibly unsigned or in short form
	 *
	 * @param shortF true iff this is a short form instruction
	 * @param un     true iff this is an unsigned comparison
	 * @param ig     the target handle
	 */
	public BGT(boolean shortF, boolean un, InstructionHandle ih) throws InstructionInitException
	{
		super((un ? (shortF ? BGT_UN_S : BGT_UN) : (shortF ? BGT_S : BGT)), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BGT_S || getOpcode() == BGT_UN_S);
	}

	public boolean isUnsignedOrUnordered()
	{
		return (getOpcode() == BGT_UN || getOpcode() == BGT_UN_S);
	}

	public String getName()
	{
		return "bgt" + (isUnsignedOrUnordered() ? (isShort() ? ".un.s" : ".un") : (isShort() ? ".s" : ""));
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

	public BGT(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		setTarget((isShort() ? parse.getMSILInputStream().readINT8() : parse.getMSILInputStream().readINT32()));
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BGT));
	}
}
