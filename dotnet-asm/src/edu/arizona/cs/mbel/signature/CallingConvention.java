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
 * Interface with flags for method signature calling conventions
 *
 * @author Michael Stepp
 */
public interface CallingConvention
{
	public static final byte DEFAULT = 0x00;
	public static final byte C = 0x01;
	public static final byte STDCALL = 0x02;
	public static final byte THISCALL = 0x03;
	public static final byte FASTCALL = 0x04;
	public static final byte VARARG = 0x05;
	public static final byte FIELD = 0x06;
	public static final byte LOCAL_SIG = 0x07;
	public static final byte PROPERTY = 0x08;
	public static final byte UNMANAGED = 0x09;
	public static final byte GENERIC = 0x10;
	public static final byte CALL_CONV_MASK = 0x0F;
	// one of the above is ORed with any of the below
	public static final byte HASTHIS = 0x20;
	public static final byte EXPLICITTHIS = 0x40;
}
