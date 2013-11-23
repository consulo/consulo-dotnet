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
 * Store argument.<br>
 * Stack transition:<br>
 * ..., value --> ...
 *
 * @author Michael Stepp
 */
public class STARG extends Instruction implements ShortFormInstruction
{
	public static final int STARG = 0x0BFE;
	public static final int STARG_S = 0x10;
	protected static final int OPCODE_LIST[] = {
			STARG,
			STARG_S
	};
	private int argNumber; // uint16 or uint8

	/**
	 * Makes a STARG object with the given argument number, possibly short form.
	 *
	 * @param shortF true iff this is a short form instruction (i.e. 0<=value<256)
	 * @param value  the argument number
	 */
	public STARG(boolean shortF, int value) throws InstructionInitException
	{
		super((shortF ? STARG_S : STARG), OPCODE_LIST);
		argNumber = (value & 0xFFFF);
		if(isShort() && argNumber > 0xFF)
		{
			throw new InstructionInitException("STARG: short instruction must have 1-byte argument");
		}
	}

	public boolean isShort()
	{
		return (getOpcode() == STARG_S);
	}

	/**
	 * Returns the argument number.
	 */
	public int getArgumentNumber()
	{
		return argNumber;
	}

	public String getName()
	{
		return (isShort() ? "starg.s" : "starg");
	}

	public int getLength()
	{
		return (super.getLength() + (isShort() ? 1 : 2));
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		if(isShort())
		{
			buffer.put(argNumber);
		}
		else
		{
			buffer.putINT16(argNumber);
		}
	}

	public STARG(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		if(opcode == STARG)
		{
			argNumber = parse.getMSILInputStream().readUINT16();
		}
		else
		{
			argNumber = parse.getMSILInputStream().readUINT8();
		}
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof STARG)))
		{
			return false;
		}
		STARG starg = (STARG) o;
		return (argNumber == starg.argNumber);
	}

/*
   public void output(){
      System.out.print(getName() + " " + argNumber);
   }
*/
}
