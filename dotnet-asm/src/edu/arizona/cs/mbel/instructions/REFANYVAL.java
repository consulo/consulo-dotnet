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
 * Reference any value. Loads the address stored in a typed reference.<br>
 * Stack transition:<br>
 * ..., TypedRef --> ..., address
 *
 * @author Michael Stepp
 */
public class REFANYVAL extends Instruction
{
	public static final int REFANYVAL = 0xC2;
	protected static final int OPCODE_LIST[] = {REFANYVAL};
	private AbstractTypeReference classRef;

	/**
	 * Makes a REFANYVAL object for the given type reference
	 *
	 * @param ref the type reference
	 */
	public REFANYVAL(AbstractTypeReference ref) throws InstructionInitException
	{
		super(REFANYVAL, OPCODE_LIST);
		classRef = ref;
	}

	/**
	 * Returns the type reference for this instruction
	 */
	public AbstractTypeReference getType()
	{
		return classRef;
	}

	public String getName()
	{
		return "refanyval";
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getTypeToken(classRef);
		buffer.putTOKEN(token);
	}

	public REFANYVAL(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long typeToken = parse.getMSILInputStream().readTOKEN();
		classRef = parse.getClassRef(typeToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof REFANYVAL)))
		{
			return false;
		}
		REFANYVAL refanyval = (REFANYVAL) o;
		return (classRef == refanyval.classRef);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      classRef.output();
   }
*/
}
