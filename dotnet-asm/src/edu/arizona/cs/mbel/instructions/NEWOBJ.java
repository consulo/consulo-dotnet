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
import edu.arizona.cs.mbel.mbel.MethodDefOrRef;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * Create new object.<br>
 * Stack transition:<br>
 * ..., arg1, ... argN --> ..., obj
 *
 * @author Michael Stepp
 */
public class NEWOBJ extends Instruction
{
	public static final int NEWOBJ = 0x73;
	protected static final int OPCODE_LIST[] = {NEWOBJ};
	private MethodDefOrRef method; // for constructor

	/**
	 * Makes a NEWOBJ object with the given constructor method.
	 *
	 * @param ref the method reference of the constructor for this type.
	 */
	public NEWOBJ(MethodDefOrRef ref) throws InstructionInitException
	{
		super(NEWOBJ, OPCODE_LIST);
		method = ref;
	}

	public MethodDefOrRef getMethod()
	{
		return method;
	}

	public String getName()
	{
		return "newobj";
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getMethodRefToken(method);
		buffer.putTOKEN(token);
	}

	public NEWOBJ(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long methodToken = parse.getMSILInputStream().readTOKEN();
		method = parse.getMethodDefOrRef(methodToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof NEWOBJ)))
		{
			return false;
		}
		NEWOBJ newobj = (NEWOBJ) o;
		return (method == newobj.method);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      method.output();
   }
*/
}
