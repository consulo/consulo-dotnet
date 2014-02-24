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
 * Load object. Copies a ValueType to the stack.<br>
 * Stack transition:<br>
 * ..., addressOfValType --> ..., value
 *
 * @author Michael Stepp
 */
public class LDOBJ extends UnalignedPrefixInstruction
{
	public static final int LDOBJ = 0x71;
	protected static final int OPCODE_LIST[] = {LDOBJ};
	private AbstractTypeReference classRef;

	/**
	 * Makes a LDOBJ object for the given value type with no unaligned prefix nor volatile prefix.
	 *
	 * @param ref a reference to the value type for this instruction
	 */
	public LDOBJ(AbstractTypeReference ref) throws InstructionInitException
	{
		super(false, LDOBJ, OPCODE_LIST);
		classRef = ref;
	}

	/**
	 * Makes a LDOBJ object for the given value type with the given unaligned prefix alignment, possibly with a volatile prefix.
	 *
	 * @param alignment the unaligned prefix alignment
	 * @param hasV      true iff this LDOBJ has a volatile prefix
	 * @param ref       a reference to the value type for this instruction
	 */
	public LDOBJ(int alignment, boolean hasV, AbstractTypeReference ref) throws InstructionInitException
	{
		super(alignment, hasV, LDOBJ, OPCODE_LIST);
		classRef = ref;
	}

	/**
	 * Returns the type reference of the ValueType for this instruction
	 */
	public AbstractTypeReference getValueType()
	{
		return classRef;
	}

	public String getName()
	{
		return "ldobj";
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

	public LDOBJ(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(false, opcode, OPCODE_LIST);
		long classToken = parse.getMSILInputStream().readTOKEN();
		classRef = parse.getClassRef(classToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDOBJ)))
		{
			return false;
		}
		LDOBJ ldobj = (LDOBJ) o;
		return (classRef == ldobj.classRef);
	}

/*
   public void output(){
      if (hasUnalignedPrefix())
         System.out.print("unaligned<" + getAlignment() +">.");
      if (hasVolatilePrefix())
         System.out.print("volatile.");
      System.out.print(getName()+" ");
      classRef.output();
   }
*/
}
