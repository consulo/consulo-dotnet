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
 * Interface with constants for method definition flags
 *
 * @author Michael Stepp
 */
public interface MethodAttributes
{
	public static final int MemberAccessMask = 0x0007;
	public static final int Compilercontrolled = 0x0000;
	public static final int Private = 0x0001;
	public static final int FamANDAssem = 0x0002;
	public static final int Assem = 0x0003;
	public static final int Family = 0x0004;
	public static final int FamORAssem = 0x0005;
	public static final int Public = 0x0006;

	public static final int Static = 0x0010;
	public static final int Final = 0x0020;
	public static final int Virtual = 0x0040;
	public static final int HideBySig = 0x0080;

	public static final int VtableLayoutMask = 0x0100;
	public static final int ReuseSlot = 0x0000;
	public static final int NewSlot = 0x0100;

	public static final int Abstract = 0x0400;
	public static final int SpecialName = 0x0800;
	public static final int PInvokeImpl = 0x2000;
	public static final int UnmanagedExport = 0x0008;
	public static final int RTSpecialName = 0x1000;
	public static final int HasSecurity = 0x4000;
	public static final int RequireSecObject = 0x8000;
}
