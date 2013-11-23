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
 * Method call.<br>
 * Stack transition:<br>
 * ..., arg1, ... argN --> ..., retVal (maybe)
 *
 * @author Michael Stepp
 */
public class CALL extends TailPrefixInstruction
{
	// CALL method
	public static final int CALL = 0x28;
	protected static final int OPCODE_LIST[] = {CALL};
	private MethodDefOrRef method;

	/**
	 * Makes a CALL object with the given method reference (with no tail prefix)
	 *
	 * @param ref the method reference to call
	 */
	public CALL(MethodDefOrRef ref) throws InstructionInitException
	{
		this(false, ref);
	}

	/**
	 * Makes a CALL object with the given method reference, possibly with a tail prefix.
	 *
	 * @param has true iff this CALL has a tail prefix
	 * @param ref the method reference to call
	 */
	public CALL(boolean has, MethodDefOrRef ref) throws InstructionInitException
	{
		super(has, CALL, OPCODE_LIST);
		method = ref;
	}

	/**
	 * Returns the method reference for this CALL
	 */
	public MethodDefOrRef getMethod()
	{
		return method;
	}

	public String getName()
	{
		return "call";
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

	public CALL(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long methodToken = parse.getMSILInputStream().readTOKEN();
		method = parse.getMethodDefOrRef(methodToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof CALL)))
		{
			return false;
		}
		CALL call = (CALL) o;
		return (method == call.method);
	}

/*
   public void output(){
      if (hasTailPrefix())
         System.out.print("tail.");
      System.out.print(getName()+" ");
      method.output();
   }
*/
}
