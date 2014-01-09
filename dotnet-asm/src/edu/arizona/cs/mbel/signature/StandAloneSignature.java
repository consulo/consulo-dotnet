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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import edu.arizona.cs.mbel.mbel.CustomAttribute;

/**
 * This class provides an abstract base state for MBEL objects that
 * come from a StandAloneSig table's signature. Such objects may have CustomAttributes
 * assigned to them, so this class provides methods to manage those attributes.
 *
 * @author Michael Stepp
 */
public abstract class StandAloneSignature extends Signature implements CustomAttributeOwner
{
	private List<CustomAttribute> myCustomAttributes = Collections.emptyList();

	protected StandAloneSignature()
	{
	}

	/**
	 * Adds a CustomAttribute to this MemberRef
	 */
	@Override
	public void addCustomAttribute(@NotNull CustomAttribute ca)
	{
		if(myCustomAttributes == Collections.<CustomAttribute>emptyList())
		{
			myCustomAttributes = new ArrayList<CustomAttribute>();
		}
		myCustomAttributes.add(ca);
	}

	/**
	 * Returns a non-null array of the CustomAttributes on this MemberRef
	 */
	@Override
	public CustomAttribute[] getCustomAttributes()
	{
		return myCustomAttributes.toArray(new CustomAttribute[myCustomAttributes.size()]);
	}

	/**
	 * Removes a CustomAttribute from thie MemberRef
	 */
	@Override
	public void removeCustomAttribute(@NotNull CustomAttribute ca)
	{
		myCustomAttributes.remove(ca);
	}
}
