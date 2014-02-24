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
 * An interface with constants for Serialization flags
 *
 * @author Michael Stepp
 */
public interface SerializationTypeConstants
{
	public static final byte SERIALIZATION_TYPE_BOOLEAN = SignatureConstants.ELEMENT_TYPE_BOOLEAN;
	public static final byte SERIALIZATION_TYPE_CHAR = SignatureConstants.ELEMENT_TYPE_CHAR;
	public static final byte SERIALIZATION_TYPE_I1 = SignatureConstants.ELEMENT_TYPE_I1;
	public static final byte SERIALIZATION_TYPE_U1 = SignatureConstants.ELEMENT_TYPE_U1;
	public static final byte SERIALIZATION_TYPE_I2 = SignatureConstants.ELEMENT_TYPE_I2;
	public static final byte SERIALIZATION_TYPE_U2 = SignatureConstants.ELEMENT_TYPE_U2;
	public static final byte SERIALIZATION_TYPE_I4 = SignatureConstants.ELEMENT_TYPE_I4;
	public static final byte SERIALIZATION_TYPE_U4 = SignatureConstants.ELEMENT_TYPE_U4;
	public static final byte SERIALIZATION_TYPE_I8 = SignatureConstants.ELEMENT_TYPE_I8;
	public static final byte SERIALIZATION_TYPE_U8 = SignatureConstants.ELEMENT_TYPE_U8;
	public static final byte SERIALIZATION_TYPE_R4 = SignatureConstants.ELEMENT_TYPE_R4;
	public static final byte SERIALIZATION_TYPE_R8 = SignatureConstants.ELEMENT_TYPE_R8;
	public static final byte SERIALIZATION_TYPE_STRING = SignatureConstants.ELEMENT_TYPE_STRING;
	public static final byte SERIALIZATION_TYPE_SZARRAY = SignatureConstants.ELEMENT_TYPE_SZARRAY;
	public static final byte SERIALIZATION_TYPE_TYPE = 0x50;
	public static final byte SERIALIZATION_TYPE_TAGGED_OBJECT = 0x51;
	public static final byte SERIALIZATION_TYPE_FIELD = 0x53;
	public static final byte SERIALIZATION_TYPE_PROPERTY = 0x54;
	public static final byte SERIALIZATION_TYPE_ENUM = 0x55;
}
