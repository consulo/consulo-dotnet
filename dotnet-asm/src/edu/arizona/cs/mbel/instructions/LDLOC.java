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
 * Load local variable.<br>
 * Stack transition:<br>
 * ... --> ..., value
 *
 * @author Michael Stepp
 */
public class LDLOC extends Instruction implements ShortFormInstruction
{
	public static final int LDLOC = 0x0CFE;
	public static final int LDLOC_S = 0x11;
	public static final int LDLOC_0 = 0x06;
	public static final int LDLOC_1 = 0x07;
	public static final int LDLOC_2 = 0x08;
	public static final int LDLOC_3 = 0x09;
	protected static final int OPCODE_LIST[] = {
			LDLOC,
			LDLOC_S,
			LDLOC_0,
			LDLOC_1,
			LDLOC_2,
			LDLOC_3
	};
	private int indexValue; // uint16 or uint8

	/**
	 * Makes a LDLOC object with the given local var index, possibly short form (only to be used for ldloc and ldloc.s)
	 *
	 * @param shortF true iff this is a short form instruction (i.e. 0<=index<256)
	 * @param index  the local var index
	 */
	public LDLOC(boolean shortF, int index) throws InstructionInitException
	{
		// for ldloc and ldloc.s
		super((shortF ? LDLOC_S : LDLOC), OPCODE_LIST);
		indexValue = (index & 0xFFFF);
		if(isShort() && indexValue > 0xFF)
		{
			throw new InstructionInitException("LDLOC: short instruction must have 1-byte argument");
		}
	}

	/**
	 * Makes a LDLOC object corresponding to the given opcode (only for ldloc.0 -- ldloc.3)
	 *
	 * @param op the opcode for this instruction. Must be one of {LDLOC_0,LDLOC_1,LDLOC_2,LDLOC_3}
	 */
	public LDLOC(int op) throws InstructionInitException
	{
		// for ldloc.0 -- ldloc.3
		super(op, OPCODE_LIST);
		if(op == LDLOC || op == LDLOC_S)
		{
			throw new InstructionInitException("LDLOC: LDLOC(int op) not for use with ldloc or ldloc.s");
		}
	}

	public boolean isShort()
	{
		return (getOpcode() == LDLOC_S);
	}

	public int getIndexValue()
	{
		int op = getOpcode();
		if(op == LDLOC || op == LDLOC_S)
		{
			return indexValue;
		}
		else if(op == LDLOC_0)
		{
			return 0;
		}
		else if(op == LDLOC_1)
		{
			return 1;
		}
		else if(op == LDLOC_2)
		{
			return 2;
		}
		else if(op == LDLOC_3)
		{
			return 3;
		}
		else
		{
			return 0;
		}
	}

	public String getName()
	{
		int op = getOpcode();
		if(op == LDLOC)
		{
			return "ldloc";
		}
		else if(op == LDLOC_S)
		{
			return "ldloc.s";
		}
		else if(op == LDLOC_0)
		{
			return "ldloc.0";
		}
		else if(op == LDLOC_1)
		{
			return "ldloc.1";
		}
		else if(op == LDLOC_2)
		{
			return "ldloc.2";
		}
		else if(op == LDLOC_3)
		{
			return "ldloc.3";
		}
		else
		{
			return "";
		}
	}

	public int getLength()
	{
		if(getOpcode() == LDLOC)
		{
			return (super.getLength() + 2);
		}
		else if(getOpcode() == LDLOC_S)
		{
			return (super.getLength() + 1);
		}
		else
		{
			return super.getLength();
		}
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);

		if(getOpcode() == LDLOC)
		{
			buffer.putINT16(indexValue);
		}
		else if(getOpcode() == LDLOC_S)
		{
			buffer.put(indexValue);
		}
	}

	public LDLOC(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		if(opcode == LDLOC)
		{
			indexValue = parse.getMSILInputStream().readUINT16();
		}
		else if(opcode == LDLOC_S)
		{
			indexValue = parse.getMSILInputStream().readUINT8();
		}
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDLOC)))
		{
			return false;
		}
		LDLOC ldloc = (LDLOC) o;
		if(getOpcode() == LDLOC || getOpcode() == LDLOC_S)
		{
			return (indexValue == ldloc.indexValue);
		}
		return true;
	}

/*
   public void output(){
      System.out.print(getName());
      if (getOpcode()==LDLOC || getOpcode()==LDLOC_S)
         System.out.print(" " + indexValue);
   }
*/
}
