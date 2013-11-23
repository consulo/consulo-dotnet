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
 * Load argument.<br>
 * Stack transition:<br>
 * ... --> ..., value
 *
 * @author Michael Stepp
 */
public class LDARG extends Instruction implements ShortFormInstruction
{
	public static final int LDARG = 0x09FE;
	public static final int LDARG_S = 0x0E;
	public static final int LDARG_0 = 0x02;
	public static final int LDARG_1 = 0x03;
	public static final int LDARG_2 = 0x04;
	public static final int LDARG_3 = 0x05;

	protected static final int OPCODE_LIST[] = {
			LDARG,
			LDARG_S,
			LDARG_0,
			LDARG_1,
			LDARG_2,
			LDARG_3
	};
	private int argumentNumber; // uint16 or uint8

	/**
	 * Makes a LDARG object corresponding to the given opcode (only to be used for ldarg.0 -- ldarg.3)
	 *
	 * @param op the opcode
	 */
	public LDARG(int op) throws InstructionInitException
	{
		// only for LDARG.0 -- LDARG.3
		super(op, OPCODE_LIST);
		if(op == LDARG || op == LDARG_S)
		{
			throw new InstructionInitException("LDARG: LDLOC(int op) not for use with ldarg or ldarg.s");
		}
	}

	/**
	 * Makes a LDARG object with the given argument number, possibly in short form (only for ldarg and ldarg.s)
	 *
	 * @param shortF true iff this is a short form instruction (i.e. 0<=num<256)
	 * @param num    the argument number
	 */
	public LDARG(boolean shortF, int num) throws InstructionInitException
	{
		super((shortF ? LDARG_S : LDARG), OPCODE_LIST);
		argumentNumber = (num & 0xFFFF);
		if(isShort() && argumentNumber > 0xFF)
		{
			throw new InstructionInitException("LDARG: short instruction must have 1-byte argument");
		}
	}

	/**
	 * Returns true iff this ldarg instruction is one of the types that has an explicit argument (i.e. ldarg or ldarg.s)
	 */
	public boolean hasArgument()
	{
		return (getOpcode() == LDARG || getOpcode() == LDARG_S);
	}

	public boolean isShort()
	{
		return (getOpcode() == LDARG_S);
	}

	public int getArgumentNumber()
	{
		if(hasArgument())
		{
			return argumentNumber;
		}

		int op = getOpcode();
		if(op == LDARG_0)
		{
			return 0;
		}
		else if(op == LDARG_1)
		{
			return 1;
		}
		else if(op == LDARG_2)
		{
			return 2;
		}
		else if(op == LDARG_3)
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
		String str[] = {
				"",
				".s",
				".0",
				".1",
				".2",
				".3"
		};
		for(int i = 0; i < OPCODE_LIST.length; i++)
		{
			if(getOpcode() == OPCODE_LIST[i])
			{
				return ("ldarg" + str[i]);
			}
		}

		return "";
	}

	public int getLength()
	{
		if(getOpcode() == LDARG)
		{
			return (super.getLength() + 2);
		}
		else if(getOpcode() == LDARG_S)
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
		if(getOpcode() == LDARG)
		{
			buffer.putINT16(argumentNumber);
		}
		else if(getOpcode() == LDARG_S)
		{
			buffer.put(argumentNumber);
		}
	}

	public LDARG(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		if(opcode == LDARG)
		{
			argumentNumber = parse.getMSILInputStream().readUINT16();
		}
		else if(opcode == LDARG_S)
		{
			argumentNumber = parse.getMSILInputStream().readUINT8();
		}
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDARG)))
		{
			return false;
		}
		LDARG ldarg = (LDARG) o;
		if(getOpcode() == LDARG || getOpcode() == LDARG_S)
		{
			return (argumentNumber == ldarg.argumentNumber);
		}
		return true;
	}

/*
   public void output(){
      System.out.print(getName());
      if (getOpcode()==LDARG || getOpcode()==LDARG_S)
         System.out.print(" " + argumentNumber);
   }
*/
}
