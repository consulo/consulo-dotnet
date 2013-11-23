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
 * An iterator for an InstructionList. The objects that next() returns are InstructionHandles.
 * The remove() method is not implemented, since removing handles can cause TargetLostExceptions.
 *
 * @author Michael Stepp
 */
public class InstructionIterator implements java.util.Iterator
{
	private InstructionHandle head, tail, current;

	protected InstructionIterator(InstructionHandle h, InstructionHandle t)
	{
		head = h;
		tail = t;
		current = head;
	}

	public boolean hasNext()
	{
		return (current.next != tail);
	}

	public Object next()
	{
		if(!hasNext())
		{
			throw new java.util.NoSuchElementException("Iteration has passed end of list");
		}
		current = current.next;
		return current;
	}

	public void remove()
	{
		throw new java.lang.UnsupportedOperationException();
	}
}
