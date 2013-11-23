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
 * Calls a virtual method.<br>
 * Stack transition:<br>
 * ..., obj, arg1, ... argN --> ..., returnVal (maybe)
 *
 * @author Michael Stepp
 */
public class CALLVIRT extends TailPrefixInstruction
{
	public static final int CALLVIRT = 0x6F;
	protected static final int OPCODE_LIST[] = {CALLVIRT};
	private edu.arizona.cs.mbel.mbel.MethodDefOrRef method;

	/**
	 * Makes a CALLVIRT object for the given method reference (with no tail prefix)
	 *
	 * @param ref the method reference
	 */
	public CALLVIRT(edu.arizona.cs.mbel.mbel.MethodDefOrRef ref) throws InstructionInitException
	{
		this(false, ref);
	}

	/**
	 * Makes a CALLVIRT object for the given method reference, possibly with a tail prefix.
	 *
	 * @param has true iff this CALLVIRT has a tail prefix
	 * @param ref the method reference
	 */
	public CALLVIRT(boolean has, edu.arizona.cs.mbel.mbel.MethodDefOrRef ref) throws InstructionInitException
	{
		super(has, CALLVIRT, OPCODE_LIST);
		method = ref;
	}

	/**
	 * Returns the method reference for this instruction
	 */
	public edu.arizona.cs.mbel.mbel.MethodDefOrRef getMethod()
	{
		return method;
	}

	public String getName()
	{
		return "callvirt";
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getMethodRefToken(method);
		buffer.putTOKEN(token);
	}

	public CALLVIRT(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long methodToken = parse.getMSILInputStream().readTOKEN();
		method = parse.getMethodDefOrRef(methodToken);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof CALLVIRT)))
		{
			return false;
		}
		CALLVIRT callvirt = (CALLVIRT) o;
		return (method == callvirt.method);
	}

/*
   public void output(){
      System.out.print((hasTailPrefix() ? "tail." : "") + getName()+" ");
      method.output();
   }
*/
}
