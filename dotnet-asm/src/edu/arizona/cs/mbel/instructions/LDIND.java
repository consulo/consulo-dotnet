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

import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * Load indirect.<br>
 * Stack transition:<br>
 * ..., address --> ..., value
 *
 * @author Michael Stepp
 */
public class LDIND extends UnalignedPrefixInstruction
{
	public static final int LDIND_I1 = 0x46;
	public static final int LDIND_I2 = 0x48;
	public static final int LDIND_I4 = 0x4A;
	public static final int LDIND_I8 = 0x4C;
	public static final int LDIND_U1 = 0x47;
	public static final int LDIND_U2 = 0x49;
	public static final int LDIND_U4 = 0x4B;
	public static final int LDIND_R4 = 0x4E;
	public static final int LDIND_R8 = 0x4F;
	public static final int LDIND_I = 0x4D;
	public static final int LDIND_REF = 0x50;
	protected static final int OPCODE_LIST[] = {
			LDIND_I1,
			LDIND_I2,
			LDIND_I4,
			LDIND_I8,
			LDIND_U1,
			LDIND_U2,
			LDIND_U4,
			LDIND_R4,
			LDIND_R8,
			LDIND_I,
			LDIND_REF
	};

	/**
	 * Makes a LDIND object with the given opcode (one of LDIND_I1, LDIND_I2, etc), with no unaligned prefix nor volatile prefix.
	 */
	public LDIND(int op) throws InstructionInitException
	{
		super(false, op, OPCODE_LIST);
	}

	/**
	 * Makes a LDIND object with the given opcode (one of LDIND_I1, LDIND_I2, etc) with
	 * the given unaligned prefix alignment, possibly with a volatile prefix.
	 *
	 * @param alignment the unaligned prefix alignment
	 * @param hasV      true iff this LDIND has a volatile prefix
	 */
	public LDIND(int alignment, boolean hasV, int op) throws InstructionInitException
	{
		super(alignment, hasV, op, OPCODE_LIST);
	}

	public String getName()
	{
		String str[] = {
				".i1",
				".i2",
				".i4",
				".i8",
				".u1",
				".u2",
				".u4",
				".r4",
				".r8",
				".i",
				".ref"
		};
		for(int i = 0; i < str.length; i++)
		{
			if(OPCODE_LIST[i] == getOpcode())
			{
				return "ldind" + str[i];
			}
		}
		return "";
	}

	public LDIND(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(false, opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof LDIND));
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
