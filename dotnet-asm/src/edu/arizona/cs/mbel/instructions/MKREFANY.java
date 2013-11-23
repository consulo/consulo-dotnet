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
 * Make reference to any type. Pushes a typed reference on the stack.<br>
 * Stack transition:<br>
 * ..., ptr --> ..., typedRef
 *
 * @author Michael Stepp
 */
public class MKREFANY extends Instruction
{
	public static final int MKREFANY = 0xC6;
	protected static final int OPCODE_LIST[] = {MKREFANY};
	private edu.arizona.cs.mbel.mbel.AbstractTypeReference classRef;

	/**
	 * Makes a MKREFANY object for the given type.
	 *
	 * @param ref the type reference
	 */
	public MKREFANY(edu.arizona.cs.mbel.mbel.AbstractTypeReference ref) throws InstructionInitException
	{
		super(MKREFANY, OPCODE_LIST);
		classRef = ref;
	}

	/**
	 * Returns the type reference for this instruction.
	 */
	public edu.arizona.cs.mbel.mbel.AbstractTypeReference getType()
	{
		return classRef;
	}

	public String getName()
	{
		return "mkrefany";
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

	public MKREFANY(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long classToken = parse.getMSILInputStream().readTOKEN();
		classRef = parse.getClassRef(classToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof MKREFANY)))
		{
			return false;
		}
		MKREFANY mkrefany = (MKREFANY) o;
		return (classRef == mkrefany.classRef);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      classRef.output();
   }
*/
}
