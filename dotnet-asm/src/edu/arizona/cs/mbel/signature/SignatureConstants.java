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
 * Interface with constants used in the various signature types
 *
 * @author Michael Stepp
 */
public interface SignatureConstants
{
	public static final byte ELEMENT_TYPE_TYPEONLY = 0x00;
	// I invented this one ^
	public static final byte ELEMENT_TYPE_END = 0x00;
	public static final byte ELEMENT_TYPE_VOID = 0x01;
	public static final byte ELEMENT_TYPE_BOOLEAN = 0x02;
	public static final byte ELEMENT_TYPE_CHAR = 0x03;
	public static final byte ELEMENT_TYPE_I1 = 0x04;
	public static final byte ELEMENT_TYPE_U1 = 0x05;
	public static final byte ELEMENT_TYPE_I2 = 0x06;
	public static final byte ELEMENT_TYPE_U2 = 0x07;
	public static final byte ELEMENT_TYPE_I4 = 0x08;
	public static final byte ELEMENT_TYPE_U4 = 0x09;
	public static final byte ELEMENT_TYPE_I8 = 0x0a;
	public static final byte ELEMENT_TYPE_U8 = 0x0b;
	public static final byte ELEMENT_TYPE_R4 = 0x0c;
	public static final byte ELEMENT_TYPE_R8 = 0x0d;
	public static final byte ELEMENT_TYPE_STRING = 0x0e;
	public static final byte ELEMENT_TYPE_PTR = 0x0f;  // followed by <type> token
	public static final byte ELEMENT_TYPE_BYREF = 0x10;  // followed by <type> token
	public static final byte ELEMENT_TYPE_VALUETYPE = 0x11;  // followed by <type> token
	public static final byte ELEMENT_TYPE_CLASS = 0x12;  // followed by <type> token
	public static final byte ELEMENT_TYPE_VAR = 0x13;  // followed by generic parameter number
	public static final byte ELEMENT_TYPE_ARRAY = 0x14;  // <type> <rank> <boundsCount> <bound1> ... <loCount> <lo1> ...
	public static final byte ELEMENT_TYPE_GENERIC_INST = 0x15;  // <type> <type-arg-count> <type-1> ... <type-n>
	public static final byte ELEMENT_TYPE_TYPEDBYREF = 0x16;
	public static final byte ELEMENT_TYPE_I = 0x18;  // System.IntPtr
	public static final byte ELEMENT_TYPE_U = 0x19;  // System.UIntPtr
	public static final byte ELEMENT_TYPE_FNPTR = 0x1b;  // Followed by full method signature
	public static final byte ELEMENT_TYPE_OBJECT = 0x1c;  // System.Object
	public static final byte ELEMENT_TYPE_SZARRAY = 0x1d;  // Single-dim array with 0 lower bound
	public static final byte ELEMENT_TYPE_MVAR = 0x1e;  // followed by method generic parameter number
	public static final byte ELEMENT_TYPE_CMOD_REQD = 0x1f;  // Required modifier : followed by a TypeDef or TypeRef token
	public static final byte ELEMENT_TYPE_CMOD_OPT = 0x20;  // Optional modifier : followed by a TypeDef or TypeRef token
	public static final byte ELEMENT_TYPE_INTERNAL = 0x21;  // Implemented within the CLI
	public static final byte ELEMENT_TYPE_MODIFIER = 0x40;  // Ordered with following element types
	public static final byte ELEMENT_TYPE_SENTINEL = 0x41;  // Sentinel for varargs method signature
	public static final byte ELEMENT_TYPE_PINNED = 0x45;  // Denotes a local variable that points at a pinned object
}
