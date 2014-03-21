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

import java.util.Vector;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.mbel.TypeGroup;

/**
 * This class describes the list of local vars in a method signature.
 * This class is one of the Signature classes, but I removed the trailing
 * "Signature" in its name for convenience because it'll probably be used a lot.
 *
 * @author Michael Stepp
 */
public class LocalVarList extends StandAloneSignature implements CallingConvention
{
	private Vector localVars;    // [count]

	/**
	 * Makes a LocalVarList from the given localVars
	 *
	 * @param locals an array of LocalVars (can be null. also, any null elements in the array will not be added)
	 */
	public LocalVarList(LocalVar[] locals)
	{
		if(locals == null)
		{
			localVars = new Vector(5);
		}
		else
		{
			localVars = new Vector(locals.length + 5);
			for(LocalVar local : locals)
			{
				if(local != null)
				{
					localVars.add(local);
				}
			}
		}
	}

	private LocalVarList()
	{
	}

	/**
	 * Factory method for parsing a local var list from a binary blob
	 *
	 * @param buffer the buffer to read from
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return a LocalVarListSignature representing the given blob, or null if there was a parse error
	 */
	public static LocalVarList parse(ByteBuffer buffer, TypeGroup group)
	{
		LocalVarList blob = new LocalVarList();

		byte data = buffer.get();
		if(data != LOCAL_SIG)
		{
			return null;
		}

		int count = readCodedInteger(buffer);

		blob.localVars = new Vector(count);
		LocalVar var = null;
		for(int i = 0; i < count; i++)
		{
			var = LocalVar.parse(buffer, group);
			if(var == null)
			{
				return null;
			}
			blob.localVars.add(var);
		}
		return blob;
	}

	/**
	 * Returns the number of local vars
	 */
	public int getCount()
	{
		return localVars.size();
	}

	/**
	 * Returns the local vars in this list (should have size getCount())
	 */
	public LocalVar[] getLocalVars()
	{
		LocalVar[] locals = new LocalVar[localVars.size()];
		for(int i = 0; i < locals.length; i++)
		{
			locals[i] = (LocalVar) localVars.get(i);
		}
		return locals;
	}

	public void addLocalVar(LocalVar v)
	{
		if(v != null)
		{
			localVars.add(v);
		}
	}

	public void removeLocalVar(LocalVar v)
	{
		localVars.remove(v);
	}
}
