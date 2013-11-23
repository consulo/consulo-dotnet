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
 * Load field.<br>
 * Stack transition:<br>
 * ..., obj --> ..., value
 *
 * @author Michael Stepp
 */
public class LDFLD extends UnalignedPrefixInstruction
{
	public static final int LDFLD = 0x7B;
	protected static final int OPCODE_LIST[] = {LDFLD};
	private edu.arizona.cs.mbel.mbel.FieldRef field;

	/**
	 * Makes a LDFLD object from the given field reference with no unaligned prefix nor volatile prefix
	 *
	 * @param ref the field reference
	 */
	public LDFLD(edu.arizona.cs.mbel.mbel.FieldRef ref) throws InstructionInitException
	{
		super(false, LDFLD, OPCODE_LIST);
		field = ref;
	}

	/**
	 * Makes a LDFLD object from the given field reference with the given unaligned
	 * prefix alignment, possibly with a volatile prefix.
	 *
	 * @param alignment the unaligned prefix alignment
	 * @param hasV      true iff this LDFLD has a volatile prefix
	 * @param ref       the field reference
	 */
	public LDFLD(int alignment, boolean hasV, edu.arizona.cs.mbel.mbel.FieldRef ref) throws InstructionInitException
	{
		super(alignment, hasV, LDFLD, OPCODE_LIST);
		field = ref;
	}

	/**
	 * Returns the field reference for this instruction.
	 */
	public edu.arizona.cs.mbel.mbel.FieldRef getField()
	{
		return field;
	}

	public String getName()
	{
		return "ldfld";
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getFieldRefToken(field);
		buffer.putTOKEN(token);
	}

	public LDFLD(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(false, opcode, OPCODE_LIST);
		long fieldToken = parse.getMSILInputStream().readTOKEN();
		field = parse.getFieldRef(fieldToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDFLD)))
		{
			return false;
		}
		LDFLD ldfld = (LDFLD) o;
		return (field == ldfld.field);
	}

/*
   public void output(){
      if (hasUnalignedPrefix())
         System.out.print("unaligned<" + getAlignment() +">.");
      if (hasVolatilePrefix())
         System.out.print("volatile.");
      System.out.print(getName()+" ");
      field.output();
   }
*/
}
