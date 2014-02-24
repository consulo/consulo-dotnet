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
 * Store static field.<br>
 * Stack transition:<br>
 * ..., val --> ...
 *
 * @author Michael Stepp
 */
public class STSFLD extends VolatilePrefixInstruction
{
	public static final int STSFLD = 0x80;
	protected static final int OPCODE_LIST[] = {STSFLD};
	private FieldRef field;

	/**
	 * Makes a STSFLD object for the given Field reference with no volatile prefix.
	 */
	public STSFLD(FieldRef ref) throws InstructionInitException
	{
		super(false, STSFLD, OPCODE_LIST);
		field = ref;
	}

	/**
	 * Makes a STSFLD object for the given Field reference, possibly with a volatile prefix.
	 *
	 * @param hasV true iff this STSFLD has a volatile prefix
	 */
	public STSFLD(boolean hasV, FieldRef ref) throws InstructionInitException
	{
		super(hasV, STSFLD, OPCODE_LIST);
		field = ref;
	}

	/**
	 * Returns the FieldRef to which this STSFLD refers
	 */
	public FieldRef getField()
	{
		return field;
	}

	public String getName()
	{
		return "stsfld";
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

	public STSFLD(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(false, opcode, OPCODE_LIST);
		long fieldToken = parse.getMSILInputStream().readTOKEN();
		field = parse.getFieldRef(fieldToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof STSFLD)))
		{
			return false;
		}
		STSFLD stsfld = (STSFLD) o;
		return (stsfld.field == field);
	}

/*
   public void output(){
      if (hasVolatilePrefix())
         System.out.print("volatile.");
      System.out.print(getName()+" ");
      field.output();
   }
*/
}
