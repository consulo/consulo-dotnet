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
 * Abstract parent of all branch instructions. Every branch instruction has a target,
 * which is an InstructionHandle. This mirrors a byte offset value which is the position
 * of that target in the instruciton stream. Users will only deal with handles, MBEL will deal with offsets.
 *
 * @author Michael Stepp
 */
public abstract class BranchInstruction extends Instruction implements ShortFormInstruction, InstructionTargeter
{
	private InstructionHandle handle;
	private int target;

	/**
	 * Makes a BranchInstruction with the given target handle
	 */
	public BranchInstruction(int op, int[] opcodes, InstructionHandle ih) throws InstructionInitException
	{
		super(op, opcodes);
		handle = ih;
		if(handle != null)
		{
			handle.addTargeter(this);
		}
	}

	protected BranchInstruction(int op, int[] opcodes) throws InstructionInitException
	{
		super(op, opcodes);
	}

	/**
	 * Returns the target handle of this BranchInstruction
	 */
	public InstructionHandle getTargetHandle()
	{
		return handle;
	}

	/**
	 * Sets the target handle of this BranchInstruction
	 */
	public void setTargetHandle(InstructionHandle ih)
	{
		if(handle != null)
		{
			handle.removeTargeter(this);
		}
		if(ih != null)
		{
			ih.addTargeter(this);
		}
		handle = ih;
	}

	public boolean containsTarget(InstructionHandle ih)
	{
		return (handle == ih);
	}

	public void updateTarget(InstructionHandle oldh, InstructionHandle newh)
	{
		if(handle == oldh)
		{
			if(oldh != null)
			{
				oldh.removeTargeter(this);
			}
			if(newh != null)
			{
				newh.addTargeter(this);
			}
			handle = newh;
		}
	}

	/**
	 * Returns the byte offset of the branch target
	 */
	protected int getTarget()
	{
		return target;
	}

	/**
	 * Sets the byte offset of the branch target (only for internal methods)
	 */
	protected void setTarget(int tar)
	{
		target = tar;
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof BranchInstruction)))
		{
			return false;
		}
		BranchInstruction bi = (BranchInstruction) o;
		return (handle == bi.handle);
	}
}
