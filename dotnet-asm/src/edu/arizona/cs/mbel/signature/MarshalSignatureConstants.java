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
 * Interface with constants defining types used in field marshalling descriptors
 *
 * @author Michael Stepp
 */
interface MarshalSignatureConstants
{
	public static final byte NATIVE_TYPE_VOID = 0x01;
	public static final byte NATIVE_TYPE_BOOLEAN = 0x02;
	public static final byte NATIVE_TYPE_I1 = 0x03;
	public static final byte NATIVE_TYPE_U1 = 0x04;
	public static final byte NATIVE_TYPE_I2 = 0x05;
	public static final byte NATIVE_TYPE_U2 = 0x06;
	public static final byte NATIVE_TYPE_I4 = 0x07;
	public static final byte NATIVE_TYPE_U4 = 0x08;
	public static final byte NATIVE_TYPE_I8 = 0x09;
	public static final byte NATIVE_TYPE_U8 = 0x0a;
	public static final byte NATIVE_TYPE_R4 = 0x0b;
	public static final byte NATIVE_TYPE_R8 = 0x0c;
	public static final byte NATIVE_TYPE_SYSCHAR = 0x0d;
	public static final byte NATIVE_TYPE_VARIANT = 0x0e;
	public static final byte NATIVE_TYPE_CURRENCY = 0x0f;
	public static final byte NATIVE_TYPE_PTR = 0x10;
	public static final byte NATIVE_TYPE_DECIMAL = 0x11;
	public static final byte NATIVE_TYPE_DATE = 0x12;
	public static final byte NATIVE_TYPE_BSTR = 0x13;
	public static final byte NATIVE_TYPE_LPSTR = 0x14;
	public static final byte NATIVE_TYPE_LPWSTR = 0x15;
	public static final byte NATIVE_TYPE_LPTSTR = 0x16;
	public static final byte NATIVE_TYPE_FIXEDSYSSTRING = 0x17;
	public static final byte NATIVE_TYPE_OBJECTREF = 0x18;
	public static final byte NATIVE_TYPE_IUNKNOWN = 0x19;
	public static final byte NATIVE_TYPE_IDISPATCH = 0x1a;
	public static final byte NATIVE_TYPE_STRUCT = 0x1b;
	public static final byte NATIVE_TYPE_INTF = 0x1c;
	public static final byte NATIVE_TYPE_SAFEARRAY = 0x1d;
	public static final byte NATIVE_TYPE_FIXEDARRAY = 0x1e;
	public static final byte NATIVE_TYPE_INT = 0x1f;
	public static final byte NATIVE_TYPE_UINT = 0x20;
	public static final byte NATIVE_TYPE_NESTEDSTRUCT = 0x21;
	public static final byte NATIVE_TYPE_BYVALSTR = 0x22;
	public static final byte NATIVE_TYPE_ANSIBSTR = 0x23;
	public static final byte NATIVE_TYPE_TBSTR = 0x24;
	public static final byte NATIVE_TYPE_VARIANTBOOL = 0x25;
	public static final byte NATIVE_TYPE_FUNC = 0x26;
	public static final byte NATIVE_TYPE_LPVOID = 0x27;
	public static final byte NATIVE_TYPE_ASANY = 0x28;
	public static final byte NATIVE_TYPE_ARRAY = 0x2a;
	public static final byte NATIVE_TYPE_LPSTRUCT = 0x2b;
	public static final byte NATIVE_TYPE_CUSTOMMARSHALER = 0x2c;
	public static final byte NATIVE_TYPE_ERROR = 0x2d;
}
