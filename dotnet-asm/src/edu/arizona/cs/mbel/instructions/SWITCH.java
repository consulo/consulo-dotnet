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
 * Switch instruction (jump table).<br>
 * Stack transition:<br>
 * ..., value --> ...
 *
 * @author Michael Stepp
 */
public class SWITCH extends Instruction implements InstructionTargeter
{
	public static final int SWITCH = 0x45;
	protected static final int OPCODE_LIST[] = {SWITCH};
	private int[] targets;
	private InstructionHandle[] handles;

	/**
	 * Makes a new SWITCH object with the given list of jump targets.
	 */
	public SWITCH(InstructionHandle[] ihs) throws InstructionInitException
	{
		super(SWITCH, OPCODE_LIST);
		if(ihs == null || ihs.length == 0)
		{
			throw new InstructionInitException("SWITCH: Null handle list given");
		}
		targets = new int[ihs.length];
		handles = ihs;

		for(int i = 0; i < handles.length; i++)
		{
			if(handles[i] != null)
			{
				handles[i].addTargeter(this);
			}
		}
	}

	/**
	 * Returns the integer offsets of the switch targets.
	 */
	protected int[] getTargets()
	{
		return targets;
	}

	/**
	 * Sets the integer offsets of the switch targets.
	 */
	protected void setTargets(int[] tars)
	{
		targets = tars;
	}

	/**
	 * Returns the instruction handles for the switch jump targets.
	 */
	public InstructionHandle[] getTargetHandles()
	{
		return handles;
	}

	/**
	 * Sets the instruction handles for the switch jump targets/
	 */
	public void setTargetHandles(InstructionHandle[] ihs)
	{
		if(ihs == null || ihs.length == 0)
		{
			return;
		}
		for(int i = 0; i < handles.length; i++)
		{
			if(handles[i] != null)
			{
				handles[i].removeTargeter(this);
			}
		}

		handles = ihs;
		targets = new int[ihs.length];
		for(int i = 0; i < handles.length; i++)
		{
			if(handles[i] != null)
			{
				handles[i].addTargeter(this);
			}
		}
	}

	public boolean containsTarget(InstructionHandle ih)
	{
		for(int i = 0; i < handles.length; i++)
		{
			if(handles[i] == ih)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the first instance of oldh, if found
	 */
	public void updateTarget(InstructionHandle oldh, InstructionHandle newh)
	{
		for(int i = 0; i < handles.length; i++)
		{
			if(handles[i] == oldh)
			{
				if(oldh != null)
				{
					oldh.removeTargeter(this);
				}
				if(newh != null)
				{
					newh.addTargeter(this);
				}
				handles[i] = newh;
				return;
			}
		}
	}

	public String getName()
	{
		return "switch";
	}

	public int getLength()
	{
		return (super.getLength() + (targets.length + 1) * 4);
	}

	protected void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		buffer.putINT32(targets.length);
		for(int i = 0; i < targets.length; i++)
		{
			buffer.putINT32(targets[i]);
		}
	}

	public SWITCH(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long numTargets = parse.getMSILInputStream().readUINT32();
		if(numTargets <= 0)
		{
			throw new InstructionInitException("SWITCH.parse: Number of targets is <=0");
		}
		targets = new int[(int) numTargets];
		handles = new InstructionHandle[(int) numTargets];
		for(int i = 0; i < numTargets; i++)
		{
			targets[i] = parse.getMSILInputStream().readINT32();
		}
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof SWITCH)))
		{
			return false;
		}
		SWITCH swit = (SWITCH) o;
		InstructionHandle[] hisHandles = swit.getTargetHandles();
		if(hisHandles.length != handles.length)
		{
			return false;
		}
		for(int i = 0; i < handles.length; i++)
		{
			if(handles[i] != hisHandles[i])
			{
				return false;
			}
		}
		return true;
	}

/*
   public void output(){
      System.out.print(getName() + " <" + targets.length + ">");
   }
*/
}
