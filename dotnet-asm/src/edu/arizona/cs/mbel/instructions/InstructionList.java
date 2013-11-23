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
 * Represents a contiguous list of instructions, as in a method.
 * The underlying structure is a linked list of InstructionHandle objects.
 * There is also a factory method to parse an entire instruction list, using the Instruction.parseInstruction method.
 * There are also methods to assign byte offsets to the InstructionHandles once user manipulation is complete.
 *
 * @author Michael Stepp
 */
public class InstructionList
{
	private InstructionHandle handleFirst, handleLast;
	private int numHandles;

	/**
	 * Creates an InstructionList with no instructions in it.
	 */
	public InstructionList()
	{
		handleFirst = new InstructionHandle(null);
		handleLast = new InstructionHandle(null);
		handleLast.next = handleFirst.next = handleLast;
		handleFirst.prev = handleLast.prev = handleFirst;

		numHandles = 0;
	}

	/**
	 * Parses an InstructionList from a file
	 *
	 * @param parse    the ClassParser for this module
	 * @param CodeSize the size in bytes of the instrucitons on disk; used as a stopping point
	 */
	public InstructionList(edu.arizona.cs.mbel.mbel.ClassParser parse, long CodeSize) throws java.io.IOException,
			edu.arizona.cs.mbel.parse.MSILParseException
	{
		this();

		edu.arizona.cs.mbel.MSILInputStream in = parse.getMSILInputStream();

		long START = in.getCurrent();
		long pos = 0;
		while(pos < CodeSize)
		{
			edu.arizona.cs.mbel.instructions.InstructionHandle ih = append(edu.arizona.cs.mbel.instructions.Instruction.readInstruction(parse));
			ih.updatePosition((int) pos);
			pos = in.getCurrent() - START;
		}

		edu.arizona.cs.mbel.instructions.Instruction instr = null;
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			instr = iter.getInstruction();
			if(instr instanceof BranchInstruction)
			{
				BranchInstruction branch = (BranchInstruction) instr;
				int target = branch.getTarget();
				int start = iter.getPosition() + iter.getInstruction().getLength(); // relative to NEXT instruction!
				if(target > 0)
				{
					boolean got = false;

					for(InstructionHandle iter2 = iter.next; iter2 != handleLast; iter2 = iter2.next)
					{
						if((start + target) == iter2.getPosition())
						{
							branch.setTargetHandle(iter2);
							got = true;
							break;
						}
					}

					if(!got)
					{
						throw new edu.arizona.cs.mbel.parse.MSILParseException("InstructionList: Cannot resolve branch target");
					}
				}
				else
				{
					boolean got = false;

					for(InstructionHandle iter2 = iter.next; iter2 != handleFirst; iter2 = iter2.prev)
					{
						if((start + target) == iter2.getPosition())
						{
							branch.setTargetHandle(iter2);
							got = true;
							break;
						}
					}
					if(!got)
					{
						throw new edu.arizona.cs.mbel.parse.MSILParseException("InstructionList: Cannot resolve branch target");
					}
				}
			}
			else if(instr instanceof SWITCH)
			{
				SWITCH swit = (SWITCH) instr;
				int[] target = swit.getTargets();
				int start = iter.next.getPosition(); // relative to NEXT instruction!
				InstructionHandle[] ihs = new InstructionHandle[target.length];

				for(int i = 0; i < ihs.length; i++)
				{
					boolean got = false;
					if(target[i] > 0)
					{
						for(InstructionHandle iter2 = iter.next; iter2 != handleLast; iter2 = iter2.next)
						{
							if((start + target[i]) <= iter2.getPosition())
							{
								ihs[i] = iter2;
								got = true;
								break;
							}
						}
					}
					else
					{
						for(InstructionHandle iter2 = iter.next; iter2 != handleFirst; iter2 = iter2.prev)
						{
							if((start + target[i]) >= iter2.getPosition())
							{
								ihs[i] = iter2;
								got = true;
								break;
							}
						}
					}

					if(!got)
					{
						throw new edu.arizona.cs.mbel.parse.MSILParseException("InstructionList: Cannot resolve switch target");
					}
				}
				swit.setTargetHandles(ihs);
			}
		}
	}

	public java.util.Iterator iterator()
	{
		return new InstructionIterator(handleFirst, handleLast);
	}

	/**
	 * Returns the InstructionHandle whose byte offset corresponds to position (not to be used until after a call to setPositions)
	 *
	 * @param position the byte offset to search for (starts before any prefixes on the instruction)
	 * @return the InstructionHandle starting at the given byte offset, or null if there is none
	 */
	public InstructionHandle getHandleAt(int position)
	{
		// gets the InstructionHandle withthe given position (or null if not a valid position)
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			if(iter.getPosition() == position)
			{
				return iter;
			}
		}
		return null;
	}

	/**
	 * Returns the InstrucitonHandle that would end at byte offset 'position' (i.e. the next byte after this instruction is 'position')
	 *
	 * @param position the byte offset right after the target instruction
	 * @return the InstructionHandle of the desired instruction, or null if none
	 */
	public InstructionHandle getHandleEndingAt(int position)
	{
		// gets the InstructionHandle ih such that ih.next would start at 'position'
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			if((iter.getPosition() + iter.getInstruction().getLength()) == position)
			{
				return iter;
			}
		}
		return null;
	}

	/**
	 * Adds an instruction to the end of the list
	 *
	 * @return the new handle of the added instruction
	 */
	public InstructionHandle append(Instruction i)
	{
		InstructionHandle handle = new InstructionHandle(i);
		handle.next = handleLast;
		handle.prev = handleLast.prev;
		handleLast.prev.next = handle;
		handleLast.prev = handle;
		numHandles++;

		return handle;
	}

	/**
	 * Adds an InstructionHandle to the end of the list
	 *
	 * @return handle
	 */
	public InstructionHandle append(InstructionHandle handle)
	{
		handle.next = handleLast;
		handle.prev = handleLast.prev;
		handleLast.prev.next = handle;
		handleLast.prev = handle;
		numHandles++;

		return handle;
	}

	/**
	 * Adds this instruction as the first instruction in the list
	 *
	 * @return the new handle of the added instruction
	 */
	public InstructionHandle prepend(Instruction i)
	{
		InstructionHandle handle = new InstructionHandle(i);
		handle.prev = handleFirst;
		handle.next = handleFirst.next;
		handleFirst.next.prev = handle;
		handleFirst.next = handle;
		numHandles++;
		return handle;
	}

	/**
	 * Adds this InstructionHandle as the first handle in the list
	 *
	 * @return handle
	 */
	public InstructionHandle prepend(InstructionHandle handle)
	{
		handle.prev = handleFirst;
		handle.next = handleFirst.next;
		handleFirst.next.prev = handle;
		handleFirst.next = handle;
		numHandles++;
		return handle;
	}


	/**
	 * Returns true iff this InstrucitonList contains the given handle
	 */
	public boolean contains(InstructionHandle ih)
	{
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			if(iter == ih)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true iff this InstructionList contains the given instruction
	 */
	public boolean contains(Instruction i)
	{
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			if(iter.getInstruction().equals(i))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes all the instructions in the list
	 */
	public void clear()
	{
		handleFirst.next = handleLast;
		handleLast.prev = handleFirst;
		numHandles = 0;
	}

	/**
	 * Deletes the given handle from the list.
	 */
	public void delete(InstructionHandle ih) throws TargetLostException
	{
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			if(iter == ih)
			{
				if(ih.hasTargeters())
				{
					throw new TargetLostException(new InstructionHandle[]{ih});
				}

				iter.prev.next = iter.next;
				iter.next.prev = iter.prev;
				numHandles--;
				return;
			}
		}
	}

	/**
	 * Deletes the first occurrence of the given instruction from the list
	 */
	public void delete(Instruction i) throws TargetLostException
	{
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			if(iter.getInstruction().equals(i))
			{
				if(iter.hasTargeters())
				{
					throw new TargetLostException(new InstructionHandle[]{iter});
				}
				iter.prev.next = iter.next;
				iter.next.prev = iter.prev;
				numHandles--;
				return;
			}
		}
	}

	/**
	 * Searches for 'from' and 'to'. If both are found and 'to' is later in the list than 'from',
	 * all instructions between 'from' and 'to' (inclusive) are removed from the list.
	 * The handle comparison is done by reference.
	 */
	public void delete(InstructionHandle from, InstructionHandle to) throws TargetLostException
	{
		InstructionHandle ih1 = null, ih2 = null;
		int count = 0;
		int targets = 0;
		for(ih1 = handleFirst.next; ih1 != handleLast; ih1 = ih1.next)
		{
			if(ih1 == from)
			{
				break;
			}
		}
		if(ih1 == handleLast)
		{
			return;
		}

		for(ih2 = ih1; ih2 != handleLast; ih2 = ih2.next)
		{
			if(ih2.hasTargeters())
			{
				targets++;
			}
			count++;
			if(ih2 == to)
			{
				break;
			}
		}
		if(ih2 == handleLast)
		{
			return;
		}

		if(targets > 0)
		{
			InstructionHandle[] lost = new InstructionHandle[targets];
			int i = 0;
			for(InstructionHandle iter = ih1; ; iter = iter.next)
			{
				if(iter.hasTargeters())
				{
					lost[i++] = iter;
				}
				if(iter == ih2)
				{
					break;
				}
			}
			throw new TargetLostException(lost);
		}

		ih1.prev.next = ih2.next;
		ih2.next.prev = ih1.prev;
		numHandles -= count;
	}

	/**
	 * Searches for 'from' and 'to'. If both are found and 'to' is later in the list than 'from',
	 * all instructions between 'from' and 'to' (inclusive) are removed from the list.
	 */
	public void delete(Instruction from, Instruction to) throws TargetLostException
	{
		InstructionHandle ih1 = null, ih2 = null;
		int count = 0;
		int targets = 0;
		for(ih1 = handleFirst.next; ih1 != handleLast; ih1 = ih1.next)
		{
			if(ih1.getInstruction().equals(from))
			{
				break;
			}
		}
		if(ih1 == handleLast)
		{
			return;
		}

		for(ih2 = ih1; ih2 != handleLast; ih2 = ih2.next)
		{
			if(ih2.hasTargeters())
			{
				targets++;
			}
			count++;
			if(ih2.getInstruction().equals(to))
			{
				break;
			}
		}
		if(ih2 == handleLast)
		{
			return;
		}

		if(targets > 0)
		{
			InstructionHandle[] lost = new InstructionHandle[targets];
			int i = 0;
			for(InstructionHandle iter = ih1; ; iter = iter.next)
			{
				if(iter.hasTargeters())
				{
					lost[i++] = iter;
				}
				if(iter == ih2)
				{
					break;
				}
			}
			throw new TargetLostException(lost);
		}

		ih1.prev.next = ih2.next;
		ih2.next.prev = ih1.prev;
		numHandles -= count;
	}

	/**
	 * Returns the number of instructions in this list
	 */
	public int getLength()
	{
		return numHandles;
	}

	/**
	 * Returns the handle of the last instruction in this list
	 */
	public InstructionHandle getEnd()
	{
		InstructionHandle end = handleLast.prev;
		if(end == handleFirst)
		{
			return null;
		}
		else
		{
			return end;
		}
	}

	/**
	 * Returns the handle of the first instruction in this list
	 */
	public InstructionHandle getStart()
	{
		InstructionHandle start = handleFirst.next;
		if(start == handleLast)
		{
			return null;
		}
		else
		{
			return start;
		}
	}

	/**
	 * Returns an array of the handles in this list, in order.
	 */
	public InstructionHandle[] getInstructionHandles()
	{
		if(numHandles == 0)
		{
			return new InstructionHandle[0];
		}

		InstructionHandle[] all = new InstructionHandle[numHandles];
		all[0] = handleFirst.next;
		for(int i = 1; i < numHandles; i++)
		{
			all[i] = all[i - 1].next;
		}

		return all;
	}

	/**
	 * Returns the list of byte offset for each instruction (must be used after setPositions)
	 */
	public int[] getInstructionPositions()
	{
		int[] all = new int[numHandles];
		InstructionHandle iter = handleFirst.next;
		for(int i = 0; i < numHandles; i++)
		{
			all[i] = iter.getPosition();
			iter = iter.next;
		}

		return all;
	}

	/**
	 * Returns the instructions in this list, in order
	 */
	public Instruction[] getInstructions()
	{
		Instruction[] all = new Instruction[numHandles];
		InstructionHandle iter = handleFirst.next;
		for(int i = 0; i < numHandles; i++)
		{
			all[i] = iter.getInstruction();
			iter = iter.next;
		}
		return all;
	}

	/**
	 * Returns true iff the list has no instructions in it
	 */
	public boolean isEmpty()
	{
		return (numHandles == 0);
	}

	/**
	 * Sets the byte offset in the InstructionHandles of this list.
	 * These are calculated starting from 0 and using Instruction.getLength()
	 */
	public void setPositions()
	{
		int pos = 0;
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			pos += iter.updatePosition(pos);
		}

		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			Instruction instr = iter.getInstruction();
			if(instr instanceof BranchInstruction)
			{
				BranchInstruction branch = (BranchInstruction) instr;
				InstructionHandle target = branch.getTargetHandle();
				int start = iter.getPosition() + branch.getLength();
				int offset = target.getPosition() - start;
				branch.setTarget(offset);
			}
			else if(instr instanceof SWITCH)
			{
				SWITCH swit = (SWITCH) instr;
				InstructionHandle targets[] = swit.getTargetHandles();
				int start = iter.getPosition() + swit.getLength();
				int offsets[] = new int[targets.length];
				for(int i = 0; i < targets.length; i++)
				{
					offsets[i] = targets[i].getPosition() - start;
				}
				swit.setTargets(offsets);
			}
		}
	}

	/**
	 * Write this InstructionList out to a buffer
	 */
	public void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		// do not call until after setPositions()
		for(InstructionHandle iter = handleFirst.next; iter != handleLast; iter = iter.next)
		{
			iter.getInstruction().emit(buffer, emitter);
		}
	}

	/**
	 * Returns the total size in bytes of all the instructions in this list put together.
	 * Must be called after setPositions.
	 */
	public int getSizeInBytes()
	{
		if(getLength() == 0)
		{
			return 0;
		}
		int result = handleLast.prev.getPosition() + handleLast.prev.getInstruction().getLength();
		return result;
	}

/*
   public void output(){
      for (InstructionHandle iter=handleFirst.next;iter!=handleLast;iter=iter.next){
         System.out.print("    ");
         iter.output();
         if (iter.getInstruction() instanceof BranchInstruction){
            BranchInstruction br = (BranchInstruction)iter.getInstruction();
            System.out.print(" [");
            br.getTargetHandle().output();
            System.out.print("]");
         }else if (iter.getInstruction() instanceof SWITCH){
            SWITCH swit = (SWITCH)iter.getInstruction();
            InstructionHandle[] ihs = swit.getTargetHandles();
            for (int i=0;i<ihs.length;i++){
               System.out.print(" [");
               ihs[i].output();
               System.out.print("]");
            }
         }
         System.out.println();
      }
   }
*/
}
