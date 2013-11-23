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
 * Store indirect.<br>
 * Stack transition:<br>
 * ..., addr, val --> ...
 *
 * @author Michael Stepp
 */
public class STIND extends UnalignedPrefixInstruction
{
	public static final int STIND_I1 = 0x52;
	public static final int STIND_I2 = 0x53;
	public static final int STIND_I4 = 0x54;
	public static final int STIND_I8 = 0x55;
	public static final int STIND_R4 = 0x56;
	public static final int STIND_R8 = 0x57;
	public static final int STIND_I = 0xDF;
	public static final int STIND_REF = 0x51;
	protected static final int OPCODE_LIST[] = {
			STIND_I1,
			STIND_I2,
			STIND_I4,
			STIND_I8,
			STIND_R4,
			STIND_R8,
			STIND_I,
			STIND_REF
	};

	/**
	 * Makes a STIND with no unaligned prefix nor volatile prefix
	 */
	public STIND(int opcode) throws InstructionInitException
	{
		super(false, opcode, OPCODE_LIST);
	}

	/**
	 * Makes a STIND with the given unaligned prefix alignment, possibly with a volatile prefix.
	 *
	 * @param alignment the unaligned prefix alignment
	 * @param hasV      true iff this STIND has a volatile prefix
	 */
	public STIND(int alignment, boolean hasV, int opcode) throws InstructionInitException
	{
		super(alignment, hasV, opcode, OPCODE_LIST);
	}

	public String getName()
	{
		String str[] = {
				"stind.i1",
				"stind.i2",
				"stind.i4",
				"stind.i8",
				"stind.r4",
				"stind.r8",
				"stind.i",
				"stind.ref"
		};
		for(int i = 0; i < str.length; i++)
		{
			if(OPCODE_LIST[i] == getOpcode())
			{
				return str[i];
			}
		}
		return "";
	}

	public STIND(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(false, opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof STIND));
	}

/*
   public void output(){
      if (hasUnalignedPrefix())
         System.out.print("unaligned<" + getAlignment() +">.");
      if (hasVolatilePrefix())
         System.out.print("volatile.");
      System.out.print(getName());
   }
*/
}
