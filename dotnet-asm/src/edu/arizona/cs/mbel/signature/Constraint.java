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


package edu.arizona.cs.mbel.signature;

/**
 * This class decsribes a constraint on a local var.
 * As of right now, the only possible constraint is ELEMENT_TYPE_PINNED,
 * so this class is kinda useless.
 *
 * @author Michael Stepp
 */
public class Constraint extends Signature
{
	private byte elementType;

	/**
	 * Makes a new Constraint (with type PINNED)
	 */
	public Constraint()
	{
		elementType = ELEMENT_TYPE_PINNED;
	}

	/**
	 * Factory method for parsing a constraint from a raw binary blob
	 *
	 * @param buffer the buffer to read from
	 * @return a Constraint representing the given blob, or null if there was a parse error
	 */
	public static Constraint parse(edu.arizona.cs.mbel.ByteBuffer buffer)
	{
		Constraint blob = new Constraint();
		blob.elementType = buffer.get();
		if(blob.elementType != ELEMENT_TYPE_PINNED)
		{
			return null;
		}
		return blob;
	}

	/**
	 * Returns the type of constraint this is (as of now, always ELEMENT_TYPE_PINNED)
	 */
	public byte getElementType()
	{
		return elementType;
	}

	/**
	 * Writes this signature out to a buffer in raw binary form
	 *
	 * @param buffer the buffer to write to
	 */
	public void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		buffer.put(elementType);
	}

	public String toString()
	{
		return "Constraint[PINNED]";
	}
}
