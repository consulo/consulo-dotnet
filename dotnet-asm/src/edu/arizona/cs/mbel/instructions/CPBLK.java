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
 * Copy block (memcpy).<br>
 * Stack transition:<br>
 * ..., destAddress, srcAddress, size --> ...
 *
 * @author Michael Stepp
 */
public class CPBLK extends UnalignedPrefixInstruction
{
	public static final int CPBLK = 0x17FE;
	protected static final int OPCODE_LIST[] = {CPBLK};

	/**
	 * Makes a CPBLK with no unaligned prefix nor volatile prefix.
	 */
	public CPBLK() throws InstructionInitException
	{
		super(false, CPBLK, OPCODE_LIST);
	}

	/**
	 * Makes a CPBLK with the given unaligned prefix alignment, possibly with a volatile prefix.
	 *
	 * @param alignment the unaligned prefix alignment
	 * @param hasV      true iff this CPBLK has a volatile prefix
	 */
	public CPBLK(int alignment, boolean hasV) throws InstructionInitException
	{
		super(alignment, hasV, CPBLK, OPCODE_LIST);
	}

	public String getName()
	{
		return "cpblk";
	}

	public CPBLK(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(false, opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof CPBLK));
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
