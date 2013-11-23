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
 * This is an interface full of constants used for
 * the 'Action' field of a DeclSecurity object. Each
 * constant is a 2-byte integer.
 *
 * @author Michael Stepp
 */
public interface SecurityActions
{
	public static final int Request = 0x0001;
	public static final int Demand = 0x0002;
	public static final int Assert = 0x0003;
	public static final int Deny = 0x0004;
	public static final int PermitOnly = 0x0005;
	public static final int LinkDemand = 0x0006;
	public static final int InheritanceDemand = 0x0007;
	public static final int RequestMinimum = 0x0008;
	public static final int RequestOptional = 0x0009;
	public static final int RequestRefuse = 0x000A;
	public static final int PreJITGrant = 0x000B;
	public static final int PreJITDeny = 0x000C;
	public static final int NonCASDemand = 0x000D;
	public static final int NonCASLinkDemand = 0x000E;
	public static final int NonCASInheritanceDemand = 0x000F;
}
