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

package edu.arizona.cs.mbel.emit;

/**
 * This class is used with a ByteBuffer to hold a list of positions within the ByteBuffer
 * that need to be augmented by a given value (i.e. patched). It is a linked list of Patch
 * objects, each containing an address (offset in the buffer) of the patch point.
 *
 * @author Michael Stepp
 */
class PatchList
{
	public Patch first;
	private int length;

	/**
	 * The node class of the linked list
	 */
	protected class Patch
	{
		public Patch next;
		public long address;

		public Patch(Patch n, long add)
		{
			next = n;
			address = add;
		}
	}

	/**
	 * Makes an empty PatchList
	 */
	public PatchList()
	{
		first = null;
		length = 0;
	}

	/**
	 * Concatenates 'list' to the end of this list.
	 * This does a shallow copy (i.e. copy by reference)
	 */
	public void concat(PatchList list)
	{
		// shallow copy
		if(first == null)
		{
			first = list.first;
			length += list.length;
			return;
		}

		Patch i = null;
		for(i = first; i.next != null; i = i.next)
		{
		}
		i.next = list.first;
		length += list.length;
	}

	/**
	 * Adds a patch to this patch list.
	 *
	 * @param address the position of the patch within the ByteBuffer
	 */
	public void addPatch(long address)
	{
		first = new Patch(first, address);
		length++;
	}

	/**
	 * Returns an array of the address of the patch points, in the order they were added
	 */
	public long[] getAddresses()
	{
		long addresses[] = new long[length];
		int i = 0;
		for(Patch p = first; p != null; p = p.next)
		{
			addresses[i++] = p.address;
		}
		return addresses;
	}
}

