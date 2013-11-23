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
 * Load local variable address.<br>
 * Stack transition:<br>
 * ... --> ..., address
 *
 * @author Michael Stepp
 */
public class LDLOCA extends Instruction implements ShortFormInstruction
{
	public static final int LDLOCA = 0x0DFE;
	public static final int LDLOCA_S = 0x12;
	protected static final int OPCODE_LIST[] = {
			LDLOCA,
			LDLOCA_S
	};
	private int indexValue; // uint16 or uint8

	/**
	 * Makes a LDLOCA object with the given local variable index, possibly in short form.
	 *
	 * @param shortF true iff this is a short form instruction (i.e. 0<=index<256)
	 * @param index  the index of the local variable
	 */
	public LDLOCA(boolean shortF, int index) throws InstructionInitException
	{
		super((shortF ? LDLOCA_S : LDLOCA), OPCODE_LIST);
		indexValue = index;
		if(isShort() && indexValue > 0xFF)
		{
			throw new InstructionInitException("LDLOCA: short instruction must have 1-byte argument");
		}
	}

	public boolean isShort()
	{
		return (getOpcode() == LDLOCA_S);
	}

	public int getIndexValue()
	{
		return indexValue;
	}

	public String getName()
	{
		return (isShort() ? "ldloca.s" : "ldloca");
	}

	public int getLength()
	{
		if(getOpcode() == LDLOCA)
		{
			return (super.getLength() + 2);
		}
		else
		{
			return (super.getLength() + 1);
		}
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		if(isShort())
		{
			buffer.put(indexValue);
		}
		else
		{
			buffer.putINT16(indexValue);
		}
	}

	public LDLOCA(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		if(opcode == LDLOCA)
		{
			indexValue = parse.getMSILInputStream().readUINT16();
		}
		else
		{
			indexValue = parse.getMSILInputStream().readUINT8();
		}
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDLOCA)))
		{
			return false;
		}
		LDLOCA ldloca = (LDLOCA) o;
		return (indexValue == ldloca.indexValue);
	}

/*
   public void output(){
      System.out.print(getName() + " "+indexValue);
   }
*/
}
