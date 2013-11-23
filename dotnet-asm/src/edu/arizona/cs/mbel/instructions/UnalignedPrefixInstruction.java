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
 * To be extended by instructions that may have an .unaligned prefix attached to them.
 * Note that unaligned implies volatile, so UnalignedPrefixInstruction extends
 * VolatilePrefixInstruction.
 *
 * @author Michael Stepp
 */
public abstract class UnalignedPrefixInstruction extends VolatilePrefixInstruction
{
	public static final int UNALIGNED = 0x12FE;

	private boolean hasUnalignedPrefix;
	private int alignment;

	protected UnalignedPrefixInstruction(int align, boolean hasV, int op, int[] opcodes) throws InstructionInitException
	{
		super(hasV, op, opcodes);
		hasUnalignedPrefix = true;
		alignment = align;
	}

	protected UnalignedPrefixInstruction(boolean hasV, int op, int[] opcodes) throws InstructionInitException
	{
		super(hasV, op, opcodes);
		hasUnalignedPrefix = false;
	}

	public boolean hasUnalignedPrefix()
	{
		return hasUnalignedPrefix;
	}

	protected void setUnalignedPrefix(boolean has)
	{
		hasUnalignedPrefix = has;
	}

	public int getAlignment()
	{
		return alignment;
	}

	protected void setAlignment(int align)
	{
		alignment = (align & 0xFF);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof UnalignedPrefixInstruction)))
		{
			return false;
		}
		UnalignedPrefixInstruction upi = (UnalignedPrefixInstruction) o;
		if(hasUnalignedPrefix != upi.hasUnalignedPrefix)
		{
			return false;
		}
		if(hasUnalignedPrefix)
		{
			if(alignment != upi.alignment)
			{
				return false;
			}
		}
		return true;
	}
}
