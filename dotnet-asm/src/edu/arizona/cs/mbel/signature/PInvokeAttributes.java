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
 * Interface with constants for platform invocation flags
 *
 * @author Michael Stepp
 */
public interface PInvokeAttributes
{
	public static final int NoMangle = 0x0001;
	public static final int CharSetMask = 0x0006;
	public static final int CharSetNotSpec = 0x0;
	public static final int CharSetAnsi = 0x0002;
	public static final int CharSetUnicode = 0x0004;
	public static final int CharSetAuto = 0x0006;
	public static final int SupportsLastError = 0x0040;
	public static final int CallConvMask = 0x0700;
	public static final int CallConvWinapi = 0x0100;
	public static final int CallConvCdecl = 0x0200;
	public static final int CallConvStdcall = 0x0300;
	public static final int CallConvThiscall = 0x0400;
	public static final int CallConvFastcall = 0x0500;
}
