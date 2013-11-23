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
 * Indirect method call. Calls method on stack with given signature.<br>
 * Stack transition:<br>
 * ...arg1, ... argN, ftn --> ..., retVal (maybe)
 *
 * @author Michael Stepp
 */
public class CALLI extends TailPrefixInstruction
{
	// CALLI callsitedescr
	public static final int CALLI = 0x29;
	protected static final int OPCODE_LIST[] = {CALLI};
	private edu.arizona.cs.mbel.signature.MethodSignature signature;

	/**
	 * Makes a new CALLI object with the given method callsite signature (with no tail prefix)
	 *
	 * @param sig the method callsite signature
	 */
	public CALLI(edu.arizona.cs.mbel.signature.MethodSignature sig) throws InstructionInitException
	{
		this(false, sig);
	}

	/**
	 * Makes a new CALLI object with the given method callsite signature, possibly with a tail prefix.
	 *
	 * @param has true iff this CALLI has a tail prefix
	 * @param sig the method callsite signature
	 */
	public CALLI(boolean has, edu.arizona.cs.mbel.signature.MethodSignature sig) throws InstructionInitException
	{
		super(has, CALLI, OPCODE_LIST);
		signature = sig;
	}

	/**
	 * Returns the method callsite signature for this CALLI
	 */
	public edu.arizona.cs.mbel.signature.MethodSignature getMethodSignature()
	{
		return signature;
	}

	public String getName()
	{
		return "calli";
	}

	public int getLength()
	{
		return (super.getLength() + 4);
	}

	protected void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		super.emit(buffer, emitter);
		long token = emitter.getStandAloneSigToken(signature);
		buffer.putTOKEN(token);
	}

	public CALLI(int opcode, edu.arizona.cs.mbel.mbel.ClassParser parse) throws java.io.IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
		long callsiteDescriptor = parse.getMSILInputStream().readTOKEN();
		signature = parse.getStandAloneSignature(callsiteDescriptor);
	}

	public boolean equals(Object o)
	{
		if(!(super.equals(o) && (o instanceof CALLI)))
		{
			return false;
		}
		CALLI calli = (CALLI) o;
		return (signature == calli.signature);
	}

/*
   public void output(){
      System.out.print(getName()+" ");
      signature.output();
   }
*/
}
