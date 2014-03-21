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
 * This class describes the type of an mbel field
 *
 * @author Michael Stepp
 */
public class FieldSignature extends StandAloneSignature implements CallingConvention
{
	private Vector customMods;   // CustomModifierSignatures
	private TypeSignature type;

	/**
	 * Convenience constructor for making a FieldSignature with no custom modifiers.
	 *
	 * @param sig the type of this field
	 */
	public FieldSignature(TypeSignature sig) throws SignatureException
	{
		this(null, sig);
	}

	/**
	 * Makes a field signature with the given custom modifiers and type
	 *
	 * @param mods an array of CustomModifers to be applied to this field (can be null)
	 * @param sig  the type signature of this field
	 */
	public FieldSignature(CustomModifierSignature[] mods, TypeSignature sig) throws SignatureException
	{
		if(sig == null)
		{
			throw new SignatureException("FieldSignature: null type specified");
		}
		type = sig;
		customMods = new Vector(10);
		if(mods != null)
		{
			for(CustomModifierSignature mod : mods)
			{
				if(mod != null)
				{
					customMods.add(mod);
				}
			}
		}
	}

	private FieldSignature()
	{
	}

	/**
	 * Factory method for parsing a field signature from a raw binary blob
	 *
	 * @param buffer the buffer to read from
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return a FieldSignature representing the given blob, or null if there was a parse error
	 */
	public static FieldSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		FieldSignature blob = new FieldSignature();

		byte data = buffer.get();
		if((data & CALL_CONV_MASK) != FIELD)
		{
			return null;
		}

		blob.customMods = new Vector(10);
		int pos = buffer.getPosition();
		CustomModifierSignature temp = CustomModifierSignature.parse(buffer, group);
		while(temp != null)
		{
			blob.customMods.add(temp);
			pos = buffer.getPosition();
			temp = CustomModifierSignature.parse(buffer, group);
		}
		buffer.setPosition(pos);

		blob.type = TypeSignatureParser.parse(buffer, group);
		if(blob.type == null)
		{
			return null;
		}
		return blob;
	}

	/**
	 * Returns the custom modifiers applied to this field
	 */
	public CustomModifierSignature[] getCustomModifiers()
	{
		CustomModifierSignature[] sigs = new CustomModifierSignature[customMods.size()];
		for(int i = 0; i < sigs.length; i++)
		{
			sigs[i] = (CustomModifierSignature) customMods.get(i);
		}

		return sigs;
	}

	/**
	 * Returns the type signature fo this field
	 */
	public TypeSignature getType()
	{
		return type;
	}
}
