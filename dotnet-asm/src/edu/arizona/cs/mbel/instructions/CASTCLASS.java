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
 * Casts an object to a given class type.<br>
 * Stack transition:<br>
 * ..., obj1 --> ..., obj2
 *
 * @author Michael Stepp
 */
public class CASTCLASS extends Instruction
{
	public static final int CASTCLASS = 0x74;
	protected static final int OPCODE_LIST[] = {CASTCLASS};
	private edu.arizona.cs.mbel.mbel.AbstractTypeReference classRef;

	/**
	 * Makes a CASTCLASS object with the given type reference
	 *
	 * @param ref the type reference for this instruction
	 */
	public CASTCLASS(edu.arizona.cs.mbel.mbel.AbstractTypeReference ref) throws InstructionInitException
	{
		super(CASTCLASS, OPCODE_LIST);
		classRef = ref;
	}

	public String getName()
	{
		return "castclass";
	}

	/**
	 * Returns the type reference for this instruction
	 */
	public edu.arizona.cs.mbel.mbel.AbstractTypeReference getType()
	{
		return classRef;
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getTypeToken(classRef);
		buffer.putTOKEN(token);
	}

	public CASTCLASS(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long classToken = parse.getMSILInputStream().readTOKEN();
		classRef = parse.getClassRef(classToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof CASTCLASS)))
		{
			return false;
		}
		CASTCLASS castclass = (CASTCLASS) o;
		return (classRef == castclass.classRef);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      classRef.output();
   }
*/
}
