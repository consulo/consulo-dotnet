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
 * To be extended by instructions that may have a .volatile prefix before them.
 *
 * @author Michael Stepp
 */
public abstract class VolatilePrefixInstruction extends Instruction
{
	public static final int VOLATILE = 0x13FE;

	private boolean hasVolatilePrefix;

	protected VolatilePrefixInstruction(boolean has, int op, int[] opcodes) throws InstructionInitException
	{
		super(op, opcodes);
		hasVolatilePrefix = has;
	}

	public boolean hasVolatilePrefix()
	{
		return hasVolatilePrefix;
	}

	protected void setVolatilePrefix(boolean has)
	{
		hasVolatilePrefix = has;
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof VolatilePrefixInstruction)))
		{
			return false;
		}
		VolatilePrefixInstruction vpi = (VolatilePrefixInstruction) o;
		return (hasVolatilePrefix == vpi.hasVolatilePrefix);
	}
}
