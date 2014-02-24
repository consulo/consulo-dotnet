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

package edu.arizona.cs.mbel.mbel;

/**
 * This class represents a CustomAttribute on any metadata table that may hold one.
 * The other MBEL classes that correspond to metadat tables will contain vectors of
 * CustomAttributes inside them.
 * It has a refrence to the method that is the CustomAttribute constructor, and a raw
 * byte blob that is the values passed to the constructor in this instance.
 * The raw byte blob is currently unparsable.
 *
 * @author Michael Stepp
 */
public class CustomAttribute
{
	private byte[] signature;
	private MethodDefOrRef constructor;

	/**
	 * Makes a new CustomAttribute witht he given parameter blob and constructor
	 *
	 * @param blob   the parameter list blob for this instance
	 * @param method the constructor method of this attribute class
	 */
	protected CustomAttribute(byte[] blob, MethodDefOrRef method)
	{
		signature = blob;
		constructor = method;
	}

	/**
	 * Returns the raw parameter blob of this instance
	 */
	public byte[] getSignature()
	{
		return signature;
	}

	/**
	 * Returns the method reference of the constructor of this Attribute class
	 */
	public MethodDefOrRef getConstructor()
	{
		return constructor;
	}
}
