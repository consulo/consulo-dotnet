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
 * Make new array.<br>
 * Stack transition:<br>
 * ..., numElems --> ..., array
 *
 * @author Michael Stepp
 */
public class NEWARR extends Instruction
{
	public static final int NEWARR = 0x8D;
	protected static final int OPCODE_LIST[] = {NEWARR};
	private AbstractTypeReference classRef; // element type

	/**
	 * Makes a NEWARR object with the given element type.
	 *
	 * @param ref the element type of the array
	 */
	public NEWARR(AbstractTypeReference ref) throws InstructionInitException
	{
		super(NEWARR, OPCODE_LIST);
		classRef = ref;
	}

	/**
	 * Returns the element type for this new array.
	 */
	public AbstractTypeReference getType()
	{
		return classRef;
	}

	public String getName()
	{
		return "newarr";
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

	public NEWARR(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long classToken = parse.getMSILInputStream().readTOKEN();
		classRef = parse.getClassRef(classToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof NEWARR)))
		{
			return false;
		}
		NEWARR newarr = (NEWARR) o;
		return (classRef == newarr.classRef);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      classRef.output();
   }
*/
}
