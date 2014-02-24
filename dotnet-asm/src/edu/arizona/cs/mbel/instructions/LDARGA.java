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
 * Load argument address.<br>
 * Stack transition:<br>
 * ... --> ..., address of argument argNum
 *
 * @author Michael Stepp
 */
public class LDARGA extends Instruction implements ShortFormInstruction
{
	public static final int LDARGA = 0x0AFE;
	public static final int LDARGA_S = 0x0F;
	protected static final int OPCODE_LIST[] = {
			LDARGA,
			LDARGA_S
	};
	private int argumentNumber; // uint16 or uint8

	/**
	 * Makes a LDARGA object with the given argument number, possibly in short form
	 *
	 * @param shortF true iff this is a short form instruction (i.e. 0<=num<256)
	 * @param num    the argument number
	 */
	public LDARGA(boolean shortF, int num) throws InstructionInitException
	{
		super((shortF ? LDARGA_S : LDARGA), OPCODE_LIST);
		argumentNumber = (num & 0xFFFF);
		if(isShort() && argumentNumber > 0xFF)
		{
			throw new InstructionInitException("LDARGA: short instruction must have 1-byte argument");
		}
	}

	public boolean isShort()
	{
		return (getOpcode() == LDARGA_S);
	}

	public String getName()
	{
		return (isShort() ? "ldarga.s" : "ldarga");
	}

	public int getLength()
	{
		if(getOpcode() == LDARGA)
		{
			return (super.getLength() + 2);
		}
		else
		{
			return (super.getLength() + 1);
		}
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		if(isShort())
		{
			buffer.put(argumentNumber);
		}
		else
		{
			buffer.putINT16(argumentNumber);
		}
	}

	public LDARGA(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		if(opcode == LDARGA)
		{
			argumentNumber = parse.getMSILInputStream().readUINT16();
		}
		else
		{
			argumentNumber = parse.getMSILInputStream().readUINT8();
		}
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDARGA)))
		{
			return false;
		}
		LDARGA ldarga = (LDARGA) o;
		return (argumentNumber == ldarga.argumentNumber);
	}

/*
   public void output(){
      System.out.print(getName() + " " +argumentNumber);
   }
*/
}
