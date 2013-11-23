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
 * This class provides an abstract base state for MBEL objects that
 * come from a StandAloneSig table's signature. Such objects may have CustomAttributes
 * assigned to them, so this class provides methods to manage those attributes.
 *
 * @author Michael Stepp
 */
public abstract class StandAloneSignature extends Signature
{
	private java.util.Vector standAloneSigAttributes;

	protected StandAloneSignature()
	{
		standAloneSigAttributes = new java.util.Vector(10);
	}

	public void addStandAloneSigAttribute(edu.arizona.cs.mbel.mbel.CustomAttribute ca)
	{
		if(ca != null)
		{
			standAloneSigAttributes.add(ca);
		}
	}

	public edu.arizona.cs.mbel.mbel.CustomAttribute[] getStandAloneSigAttributes()
	{
		edu.arizona.cs.mbel.mbel.CustomAttribute[] cas = new edu.arizona.cs.mbel.mbel.CustomAttribute[standAloneSigAttributes.size()];
		for(int i = 0; i < cas.length; i++)
		{
			cas[i] = (edu.arizona.cs.mbel.mbel.CustomAttribute) standAloneSigAttributes.get(i);
		}
		return cas;
	}

	public void removeStandAloneSigAttribute(edu.arizona.cs.mbel.mbel.CustomAttribute ca)
	{
		if(ca != null)
		{
			standAloneSigAttributes.remove(ca);
		}
	}
}
