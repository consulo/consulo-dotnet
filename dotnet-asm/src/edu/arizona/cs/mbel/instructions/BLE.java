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
 * Branch on Less Than or Equal To.<br>
 * Stack transition:<br>
 * ..., value1, value2 --> ...
 *
 * @author Michael Stepp
 */
public class BLE extends BranchInstruction
{
	// BLE <int32>
	// BLE.S <int8>
	// BLE.UN <int32>
	// BLE.UN.S <int8>
	public static final int BLE = 0x3E;
	public static final int BLE_S = 0x31;
	public static final int BLE_UN = 0x43;
	public static final int BLE_UN_S = 0x36;
	protected static final int OPCODE_LIST[] = {
			BLE,
			BLE_S,
			BLE_UN,
			BLE_UN_S
	};

	/**
	 * Makes a BLE object with the given target, possibly unsigned and/or short form.
	 *
	 * @param shortF true iff this is a short form instruction (i.e. the target is within 128 bytes from this instruction)
	 * @param un     true iff the comparison should be done unsigned (or unordered)
	 * @param ih     the handle of the branch target
	 */
	public BLE(boolean shortF, boolean un, InstructionHandle ih) throws InstructionInitException
	{
		super((un ? (shortF ? BLE_UN_S : BLE_UN) : (shortF ? BLE_S : BLE)), OPCODE_LIST, ih);
	}

	public boolean isShort()
	{
		return (getOpcode() == BLE_S || getOpcode() == BLE_UN_S);
	}

	public boolean isUnsignedOrUnordered()
	{
		return (getOpcode() == BLE_UN || getOpcode() == BLE_UN_S);
	}

	public String getName()
	{
		return "ble" + (isUnsignedOrUnordered() ? (isShort() ? ".un.s" : ".un") : (isShort() ? ".s" : ""));
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

	public BLE(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		setTarget((isShort() ? parse.getMSILInputStream().readINT8() : parse.getMSILInputStream().readINT32()));
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof BLE));
	}
}
