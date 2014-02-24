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
import edu.arizona.cs.mbel.mbel.AbstractTypeReference;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * Boxes a ValueType.<br>
 * Stack transition:<br>
 * ..., valueType --> ..., obj
 *
 * @author Michael Stepp
 */
public class BOX extends Instruction
{
	public static final int BOX = 0x8C;
	protected static final int OPCODE_LIST[] = {BOX};
	private AbstractTypeReference valueType;

	/**
	 * Makes a BOX object for the given ValueType
	 *
	 * @param ref the type reference of the ValueType
	 */
	public BOX(AbstractTypeReference ref) throws InstructionInitException
	{
		super(BOX, OPCODE_LIST);
		valueType = ref;
	}

	/**
	 * Returns the tyep reference for the ValueType
	 */
	public AbstractTypeReference getValueType()
	{
		return valueType;
	}

	public String getName()
	{
		return "box";
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getTypeToken(valueType);
		buffer.putTOKEN(token);
	}

	public BOX(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long valueTypeToken = parse.getMSILInputStream().readTOKEN();
		valueType = parse.getClassRef(valueTypeToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof BOX)))
		{
			return false;
		}
		BOX box = (BOX) o;
		return (valueType == box.valueType);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      valueType.output();
   }
*/
}
