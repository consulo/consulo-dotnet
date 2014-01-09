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
 * This class holds the information needed for a method parameter.
 * Both ParameterSignature and ReturnTypeSignature may containt an
 * instance of this class. This class is only used inside method
 * definition signatures, not callsite signatures.
 *
 * @author Michael Stepp
 */
public class ParameterInfo extends BaseCustomAttributeOwner implements ParamAttributes
{
	private long ParamRID = -1L;
	private int Flags;
	private String Name;
	private MarshalSignature fieldMarshal;
	private byte[] defaultValue;

	/**
	 * Constructs a ParameterInfo object with the given name and flags
	 *
	 * @param name  the name of this parameter
	 * @param flags a bit vector of flags (defined in ParamAttributes)
	 */
	public ParameterInfo(String name, int flags)
	{
		Name = name;
		Flags = flags;
	}

	/**
	 * Returns a bit vector of flags for this parameter (defined in ParameterAttributes)
	 */
	public int getFlags()
	{
		return Flags;
	}

	/**
	 * Sets the flags for this parameter
	 */
	public void setFlags(int flags)
	{
		Flags = flags;
	}

	/**
	 * Returns the Param RID for this paramater (used by emitter)
	 */
	public long getParamRID()
	{
		return ParamRID;
	}

	/**
	 * Sets the Param RID for this parameter (used by emitter)
	 */
	public void setParamRID(long rid)
	{
		if(ParamRID == -1L)
		{
			ParamRID = rid;
		}
	}

	/**
	 * Returns the default value for this parameter (null implies no default value)
	 */
	public byte[] getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Sets the default value for this parameter.
	 *
	 * @param blob the new default value. if this is null or has 0 length, default value is removed
	 */
	public void setDefaultValue(byte[] blob)
	{
		if(blob == null || blob.length == 0)
		{
			Flags &= ~HasDefault;
		}
		else
		{
			Flags |= HasDefault;
		}

		defaultValue = blob;
	}

	/**
	 * Returns the field marshalling informationfor this parameter (may be null)
	 */
	public MarshalSignature getFieldMarshal()
	{
		return fieldMarshal;
	}

	/**
	 * Sets the field marshalling information for this parameter (passing null removes field marshal)
	 */
	public void setFieldMarshal(MarshalSignature sig)
	{
		if(sig == null)
		{
			Flags &= ~HasFieldMarshal;
		}
		else
		{
			Flags |= HasFieldMarshal;
		}

		fieldMarshal = sig;
	}

	/**
	 * Returns the name of this parameter
	 */
	public String getName()
	{
		return Name;
	}

	/**
	 * Sets the name of this parameter
	 */
	public void setName(String name)
	{
		Name = name;
	}
}
