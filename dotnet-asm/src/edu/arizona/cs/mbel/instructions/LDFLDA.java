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
import edu.arizona.cs.mbel.mbel.FieldRef;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * Load field address.<br>
 * Stack transition:<br>
 * ..., obj --> ..., address
 *
 * @author Michael Stepp
 */
public class LDFLDA extends Instruction
{
	public static final int LDFLDA = 0x7C;
	protected static final int OPCODE_LIST[] = {LDFLDA};
	private FieldRef field;

	/**
	 * Makes a LDFLDA object for the given field reference.
	 *
	 * @param ref the field to load
	 */
	public LDFLDA(FieldRef ref) throws InstructionInitException
	{
		super(LDFLDA, OPCODE_LIST);
		field = ref;
	}

	/**
	 * Returns a reference to the field in this instruction.
	 */
	public FieldRef getField()
	{
		return field;
	}

	public String getName()
	{
		return "ldflda";
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getFieldRefToken(field);
		buffer.putTOKEN(token);
	}

	public LDFLDA(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long fieldToken = parse.getMSILInputStream().readTOKEN();
		field = parse.getFieldRef(fieldToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDFLDA)))
		{
			return false;
		}
		LDFLDA ldflda = (LDFLDA) o;
		return (field == ldflda.field);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      field.output();
   }
*/
}
