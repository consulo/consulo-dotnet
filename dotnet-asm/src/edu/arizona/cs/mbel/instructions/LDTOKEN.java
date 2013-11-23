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
 * Load metadata token.<br>
 * Stack transition:<br>
 * ... --> ..., RuntimeHandle
 *
 * @author Michael Stepp
 */
public class LDTOKEN extends Instruction
{
	public static final int LDTOKEN = 0xD0;
	protected static final int OPCODE_LIST[] = {LDTOKEN};
	private LoadableType loadableType;// methoddef, methodref, typedef, typeref, fieldref, fielddef

	/**
	 * Makes a LDTOKEN object with the given loadable type.
	 *
	 * @param type an MBEL object that has one of the metadata tokens that may be loaded with this instruction
	 */
	public LDTOKEN(LoadableType type) throws InstructionInitException
	{
		super(OPCODE_LIST[0], OPCODE_LIST);
		loadableType = type;
	}

	/**
	 * Returns the loadable type for this instruction
	 */
	public LoadableType getLoadableType()
	{
		return loadableType;
	}

	public String getName()
	{
		return "ldtoken";
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getLoadableTypeToken(loadableType);
		buffer.putTOKEN(token);
	}

	public LDTOKEN(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long metadataToken = parse.getMSILInputStream().readTOKEN();
		loadableType = parse.getLoadableType(metadataToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof LDTOKEN)))
		{
			return false;
		}
		LDTOKEN ldtoken = (LDTOKEN) o;
		return (loadableType == ldtoken.loadableType);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      loadableType.output();
   }
*/
}
