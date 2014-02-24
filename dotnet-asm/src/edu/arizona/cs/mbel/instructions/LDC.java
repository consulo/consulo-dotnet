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
 * Load constant.<br>
 * Stack transition:<br>
 * ... --> ..., num
 *
 * @author Michael Stepp
 */
public class LDC extends Instruction implements ShortFormInstruction
{
	public static final int LDC_I4 = 0x20;
	public static final int LDC_I8 = 0x21;
	public static final int LDC_R4 = 0x22;
	public static final int LDC_R8 = 0x23;
	public static final int LDC_I4_0 = 0x16;
	public static final int LDC_I4_1 = 0x17;
	public static final int LDC_I4_2 = 0x18;
	public static final int LDC_I4_3 = 0x19;
	public static final int LDC_I4_4 = 0x1A;
	public static final int LDC_I4_5 = 0x1B;
	public static final int LDC_I4_6 = 0x1C;
	public static final int LDC_I4_7 = 0x1D;
	public static final int LDC_I4_8 = 0x1E;
	public static final int LDC_I4_M1 = 0x15;
	public static final int LDC_I4_S = 0x1F;

	protected static final int OPCODE_LIST[] = {
			LDC_I4,
			LDC_I8,
			LDC_R4,
			LDC_R8,
			LDC_I4_0,
			LDC_I4_1,
			LDC_I4_2,
			LDC_I4_3,
			LDC_I4_4,
			LDC_I4_5,
			LDC_I4_6,
			LDC_I4_7,
			LDC_I4_8,
			LDC_I4_M1,
			LDC_I4_S
	};
	private long iConstant;
	private float r4Constant;
	private double r8Constant;

	/**
	 * Makes a LDC of the given type, with the value stored in value. Not to be used for R4 or R8.
	 *
	 * @param op    the opcode of the correct LDC instruction
	 * @param value the value to use, in long form
	 */
	public LDC(int op, long value) throws InstructionInitException
	{
		// this constructor automatically makes the
		// most optimal version of itself given the argument
		super(op, OPCODE_LIST);
		iConstant = value;
	}

	/**
	 * Makes a LDC_R4 object with the given float value.
	 *
	 * @param f the float value to load
	 */
	public LDC(float f) throws InstructionInitException
	{
		super(LDC_R4, OPCODE_LIST);
		r4Constant = f;
	}

	/**
	 * Makes a LDC_R8 object with the given double value.
	 *
	 * @param d the double to load
	 */
	public LDC(double d) throws InstructionInitException
	{
		super(LDC_R8, OPCODE_LIST);
		r8Constant = d;
	}

	/**
	 * Returns the value to load in a Number object (R8 becomes Double, R4 becomes Float, all others are Long)
	 */
	public Number getConstantValue()
	{
		int op = getOpcode();
		if(op == LDC_R4)
		{
			return new Float(r4Constant);
		}
		else if(op == LDC_R8)
		{
			return new Double(r8Constant);
		}
		else
		{
			return new Long(iConstant);
		}
	}

	public boolean isShort()
	{
		return (getOpcode() == LDC_I4_S);
	}

	public String getName()
	{
		int op = getOpcode();
		if(op == LDC_I4)
		{
			return "ldc.i4";
		}
		else if(op == LDC_I8)
		{
			return "ldc.i8";
		}
		else if(op == LDC_R4)
		{
			return "ldc.r4";
		}
		else if(op == LDC_R8)
		{
			return "ldc.r8";
		}
		else if(op == LDC_I4_S)
		{
			return "ldc.i4.s";
		}
		else if(op == LDC_I4_0)
		{
			return "ldc.i4.0";
		}
		else if(op == LDC_I4_1)
		{
			return "ldc.i4.1";
		}
		else if(op == LDC_I4_2)
		{
			return "ldc.i4.2";
		}
		else if(op == LDC_I4_3)
		{
			return "ldc.i4.3";
		}
		else if(op == LDC_I4_4)
		{
			return "ldc.i4.4";
		}
		else if(op == LDC_I4_5)
		{
			return "ldc.i4.5";
		}
		else if(op == LDC_I4_6)
		{
			return "ldc.i4.6";
		}
		else if(op == LDC_I4_7)
		{
			return "ldc.i4.7";
		}
		else if(op == LDC_I4_8)
		{
			return "ldc.i4.8";
		}
		else
		{
			return "ldc.i4.m1";
		}
	}

	public int getLength()
	{
		if(getOpcode() == LDC_I4 || getOpcode() == LDC_R4)
		{
			return (super.getLength() + 4);
		}
		else if(getOpcode() == LDC_I8 || getOpcode() == LDC_R8)
		{
			return (super.getLength() + 8);
		}
		else if(getOpcode() == LDC_I4_S)
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
		if(getOpcode() == LDC_I4)
		{
			buffer.putINT32((int) iConstant);
		}
		else if(getOpcode() == LDC_I8)
		{
			buffer.putINT64(iConstant);
		}
		else if(getOpcode() == LDC_R4)
		{
			buffer.putR4(r4Constant);
		}
		else if(getOpcode() == LDC_R8)
		{
			buffer.putR8(r8Constant);
		}
		else if(getOpcode() == LDC_I4_S)
		{
			buffer.put((byte) (iConstant & 0xFF));
		}
	}

	public LDC(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		if(opcode == LDC_I4)
		{
			iConstant = ((long) parse.getMSILInputStream().readINT32());
		}
		else if(opcode == LDC_I8)
		{
			iConstant = parse.getMSILInputStream().readINT64();
		}
		else if(opcode == LDC_R4)
		{
			r4Constant = parse.getMSILInputStream().readR4();
		}
		else if(opcode == LDC_R8)
		{
			r8Constant = parse.getMSILInputStream().readR8();
		}
		else if(opcode == LDC_I4_S)
		{
			iConstant = (long) parse.getMSILInputStream().readINT8();
		}
		else if(opcode == LDC_I4_M1)
		{
			iConstant = (-1L);
		}

		for(int i = 0; i <= 8; i++)
		{
			if(opcode == (LDC_I4_0 + i))
			{
				iConstant = ((long) i);
			}
		}
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDC)))
		{
			return false;
		}
		LDC ldc = (LDC) o;
		switch(getOpcode())
		{
			case LDC_I4:
			case LDC_I8:
			case LDC_I4_S:
				return (iConstant == ldc.iConstant);
			case LDC_R4:
				return (r4Constant == ldc.r4Constant);
			case LDC_R8:
				return (r8Constant == ldc.r8Constant);
			default:
				return true;
		}
	}

/*
   public void output(){
      System.out.print(getName());
      if (getOpcode()==LDC_I4 || getOpcode()==LDC_I8 || getOpcode()==LDC_I4_S)
         System.out.print(" " + iConstant);
      else if (getOpcode()==LDC_R4)
         System.out.print(" " + r4Constant);
      else if (getOpcode()==LDC_R8)
         System.out.print(" " + r8Constant);
   }
*/
}
