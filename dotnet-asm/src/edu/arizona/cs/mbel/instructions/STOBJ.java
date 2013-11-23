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
 * Store object.<br>
 * Stack transition:<br>
 * ..., addr, valObj --> ...
 *
 * @author Michael Stepp
 */
public class STOBJ extends UnalignedPrefixInstruction
{
	public static final int STOBJ = 0x81;
	protected static final int OPCODE_LIST[] = {STOBJ};
	private AbstractTypeReference classRef;

	/**
	 * Makes a STOBJ for the given type (with no unaligned prefix nor volatile prefix)
	 *
	 * @param ref the type
	 */
	public STOBJ(AbstractTypeReference ref) throws InstructionInitException
	{
		super(false, STOBJ, OPCODE_LIST);
		classRef = ref;
	}

	/**
	 * Makes a STOBJ for the given type with an unaligned prefix, possibly with a volatile prefix
	 *
	 * @param alignment the unaligned prefix alignment value (0<=alignment<256)
	 * @param hasV      true iff this STOBJ has a volatile prefix
	 * @param ref       the type
	 */
	public STOBJ(int alignment, boolean hasV, AbstractTypeReference ref) throws InstructionInitException
	{
		super(alignment, hasV, STOBJ, OPCODE_LIST);
		classRef = ref;
	}

	/**
	 * Returns the type for this STOBJ
	 */
	public AbstractTypeReference getType()
	{
		return classRef;
	}

	public String getName()
	{
		return "stobj";
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

	public STOBJ(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(false, opcode, OPCODE_LIST);
		long classToken = parse.getMSILInputStream().readTOKEN();
		classRef = parse.getClassRef(classToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof STOBJ)))
		{
			return false;
		}
		STOBJ stobj = (STOBJ) o;
		return (classRef == stobj.classRef);
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
