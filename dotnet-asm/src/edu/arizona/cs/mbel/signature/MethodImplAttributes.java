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
 * Interface with constants for method implementation flags
 *
 * @author Michael Stepp
 */
public interface MethodImplAttributes
{
	public static final int CodeTypeMask = 0x0003;
	public static final int IL = 0x0000;
	public static final int Native = 0x0001;
	public static final int OPTIL = 0x0002;
	public static final int Runtime = 0x0003;

	public static final int ManagedMask = 0x0004;
	public static final int Unmanaged = 0x0004;
	public static final int Managed = 0x0000;

	public static final int ForwardRef = 0x0010;
	public static final int PreserveSig = 0x0080;
	public static final int InternalCall = 0x1000;
	public static final int Synchronized = 0x0020;
	public static final int NoInlining = 0x0008;
	public static final int NoOptimization = 0x0040;
	public static final int MaxMethodImplVal = 0xffff;
}
