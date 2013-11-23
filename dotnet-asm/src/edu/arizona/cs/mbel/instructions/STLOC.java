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
 * Store local variable.<br>
 * Stack transition:<br>
 * ..., value --> ...
 *
 * @author Michael Stepp
 */
public class STLOC extends Instruction implements ShortFormInstruction
{
	public static final int STLOC = 0x0EFE;
	public static final int STLOC_S = 0x13;
	public static final int STLOC_0 = 0x0A;
	public static final int STLOC_1 = 0x0B;
	public static final int STLOC_2 = 0x0C;
	public static final int STLOC_3 = 0x0D;
	protected static final int OPCODE_LIST[] = {
			STLOC,
			STLOC_S,
			STLOC_0,
			STLOC_1,
			STLOC_2,
			STLOC_3
	};
	private int indexValue; // uint16 or uint8

	/**
	 * Makes a STLOC with the given index, possibly in short form.
	 *
	 * @param shortF true iff this is a short form instruction (8-bit index)
	 * @param index  the index of the local variable (should be >3)
	 */
	public STLOC(boolean shortF, int index) throws InstructionInitException
	{
		super((shortF ? STLOC_S : STLOC), OPCODE_LIST);
		indexValue = (index & 0xFFFF);
		if(isShort() && indexValue > 0xFF)
		{
			throw new InstructionInitException("STLOC: short instruction must have 1-byte argument");
		}
	}

	/**
	 * Makes a STLOC of the given opcode.
	 *
	 * @param op the opcode of the right instruction
	 */
	public STLOC(int op) throws InstructionInitException
	{
		super(op, OPCODE_LIST);
	}

	public boolean isShort()
	{
		return (getOpcode() == STLOC_S);
	}

	/**
	 * Returns the index of the local variable.
	 */
	public int getIndexValue()
	{
		int op = getOpcode();
		switch(op)
		{
			case STLOC:
				return indexValue;
			case STLOC_S:
				return indexValue;
			case STLOC_0:
				return 0;
			case STLOC_1:
				return 1;
			case STLOC_2:
				return 2;
			case STLOC_3:
				return 3;
			default:
				return -1;
		}
	}

	public String getName()
	{
		int op = getOpcode();
		switch(op)
		{
			case STLOC:
				return "stloc";
			case STLOC_S:
				return "stloc.s";
			case STLOC_0:
				return "stloc.0";
			case STLOC_1:
				return "stloc.1";
			case STLOC_2:
				return "stloc.2";
			case STLOC_3:
				return "stloc.3";
			default:
				return "";
		}
	}

	public int getLength()
	{
		if(getOpcode() == STLOC)
		{
			return (super.getLength() + 2);
		}
		else if(getOpcode() == STLOC_S)
		{
			return (super.getLength() + 1);
		}
		else
		{
			return super.getLength();
		}
	}

	protected void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		if(getOpcode() == STLOC)
		{
			buffer.putINT16(indexValue);
		}
		else if(getOpcode() == STLOC_S)
		{
			buffer.put(indexValue);
		}
	}

	public STLOC(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		if(opcode == STLOC)
		{
			indexValue = parse.getMSILInputStream().readUINT16();
		}
		else if(opcode == STLOC_S)
		{
			indexValue = parse.getMSILInputStream().readUINT8();
		}
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof STLOC)))
		{
			return false;
		}
		STLOC stloc = (STLOC) o;
		if(getOpcode() == STLOC || getOpcode() == STLOC_S)
		{
			return (indexValue == stloc.indexValue);
		}
		else
		{
			return true;
		}
	}

/*
   public void output(){
      System.out.print(getName());
      if (getOpcode()==STLOC || getOpcode()==STLOC_S)
         System.out.print(" " + indexValue);
   }
*/
}
