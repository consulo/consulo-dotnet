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
 * Load static field address.<br>
 * Stack transition:<br>
 * ... --> ..., address
 *
 * @author Michael Stepp
 */
public class LDSFLDA extends Instruction
{
	public static final int LDSFLDA = 0x7F;
	protected static final int OPCODE_LIST[] = {LDSFLDA};
	private FieldRef field;

	/**
	 * Makes a LDSFLDA object for the given static field.
	 *
	 * @param ref a reference to a static field
	 */
	public LDSFLDA(FieldRef ref) throws InstructionInitException
	{
		super(LDSFLDA, OPCODE_LIST);
		field = ref;
	}

	/**
	 * Returns a reference to the static field for this instruction
	 */
	public FieldRef getField()
	{
		return field;
	}

	public String getName()
	{
		return "ldsflda";
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

	public LDSFLDA(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long fieldToken = parse.getMSILInputStream().readTOKEN();
		field = parse.getFieldRef(fieldToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDSFLDA)))
		{
			return false;
		}
		LDSFLDA ldsflda = (LDSFLDA) o;
		return (field == ldsflda.field);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      field.output();
   }
*/
}
