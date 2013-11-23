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

import java.util.Vector;

/**
 * A doubly-linked-list wrapper around an instruction. Handles also hold the byte offset
 * position of the instruction they carry, within its instruction list. Branch instructions
 * will refer to handles, not to instructions.
 *
 * @author Michael Stepp
 */
public class InstructionHandle
{
	private Vector targeters;
	private Instruction instruction;
	protected InstructionHandle next, prev;
	protected int position;

	/**
	 * Creates a handle for the given instruction. The byte offset defaults to -1.
	 */
	public InstructionHandle(Instruction instr)
	{
		instruction = instr;
		next = prev = null;
		position = -1;
		targeters = new Vector(10);
	}

	/**
	 * Inserts the given instruction handle after this one in the linked list.
	 * If null is passed, null is returned and nothing else happens.
	 */
	public InstructionHandle insertAfter(InstructionHandle ih)
	{
		if(ih == null)
		{
			return null;
		}
		next.prev = ih;
		ih.next = next;
		ih.prev = this;
		next = ih;
		return ih;
	}

	/**
	 * Inserts the given instruction after this one in the linked list
	 */
	public InstructionHandle insertAfter(Instruction i)
	{
		InstructionHandle ih = new InstructionHandle(i);
		return insertAfter(ih);
	}

	/**
	 * Returns an array of all the targeters registered on this handle
	 */
	public InstructionTargeter[] getTargeters()
	{
		InstructionTargeter[] tar = new InstructionTargeter[targeters.size()];
		for(int i = 0; i < tar.length; i++)
		{
			tar[i] = (InstructionTargeter) targeters.get(i);
		}
		return tar;
	}

	/**
	 * Adds a targeter to this handle. If this targeter is already registered on this
	 * handle, nothing happens (search by reference). If the targeter is added,
	 * tar.updateTarget(null, this) is called.
	 */
	public void addTargeter(InstructionTargeter tar)
	{
		if(tar == null)
		{
			return;
		}
		for(Object targeter : targeters)
		{
			if(targeter == tar)
			{
				return;
			}
		}
		targeters.add(tar);
		tar.updateTarget(null, this);
	}

	/**
	 * Returns true if this handle is being targetted by any InstructionTargeters
	 */
	public boolean hasTargeters()
	{
		return (targeters.size() > 0);
	}

	/**
	 * Removes all the InstructionTargeters from this handle
	 */
	public void removeAllTargeters()
	{
		for(Object targeter : targeters)
		{
			InstructionTargeter tar = (InstructionTargeter) targeter;
			tar.updateTarget(this, null);
		}
		targeters.removeAllElements();
	}

	/**
	 * Removes the given InstructionTargeter from this handle (compares by reference).
	 * This method will also call tar.updateTarget(this, null).
	 */
	public void removeTargeter(InstructionTargeter tar)
	{
		if(tar == null)
		{
			return;
		}
		for(int i = 0; i < targeters.size(); i++)
		{
			if(targeters.get(i) == tar)
			{
				targeters.removeElementAt(i);
				// must remove before calling tar.updateTarget
				tar.updateTarget(this, null);
				break;
			}
		}
	}

	/**
	 * Resets the instruction inside this handle
	 */
	public void setInstruction(Instruction i)
	{
		instruction = i;
	}

	/**
	 * Returns the next handle after this one in the linked-list.
	 */
	public InstructionHandle getNext()
	{
		return next;
	}

	/**
	 * Returns the previous handle to this one in the linked-list.
	 */
	public InstructionHandle getPrev()
	{
		return prev;
	}

	/**
	 * Returns the byte offest position of this handle within its InstructionList
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * Returns the instruction inside this handle.
	 */
	public Instruction getInstruction()
	{
		return instruction;
	}

	/**
	 * Sets the offset of this handle, and returns the next byte offset after this handle (i.e. offset+mylength)
	 * Used by InstructionList.setPositions.
	 */
	protected int updatePosition(int offset)
	{
		position = offset;
		return instruction.getLength();
	}
   
/*
   public void output(){
      System.out.print(position + ": ");
      instruction.output();
   }
*/
}
