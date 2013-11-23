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
 * Interface containing constants describing TypeDefs
 *
 * @author Michael Stepp
 */
public interface TypeAttributes
{
	public static final int VisibilityMask = 0x00000007;
	public static final int NotPublic = 0x00000000;
	public static final int Public = 0x00000001;
	public static final int NestedPublic = 0x00000002;
	public static final int NestedPrivate = 0x00000003;
	public static final int NestedFamily = 0x00000004;
	public static final int NestedAssembly = 0x00000005;
	public static final int NestedFamANDAssem = 0x00000006;
	public static final int NestedFamORAssem = 0x00000007;

	public static final int LayoutMask = 0x00000018;
	public static final int AutoLayout = 0x00000000;
	public static final int SequentialLayout = 0x00000008;
	public static final int ExplicitLayout = 0x00000010;

	public static final int ClassSemanticsMask = 0x00000020;
	public static final int Class = 0x00000000;
	public static final int Interface = 0x00000020;

	public static final int Abstract = 0x00000080;
	public static final int Sealed = 0x00000100;
	public static final int SpecialName = 0x00000400;
	public static final int Import = 0x00001000;
	public static final int Serializable = 0x00002000;

	public static final int StringFormatMask = 0x00030000;
	public static final int AnsiClass = 0x00000000;
	public static final int UnicodeClass = 0x00010000;
	public static final int AutoClass = 0x00020000;

	public static final int BeforeFieldInit = 0x00100000;
	public static final int RTSpecialName = 0x00000800;
	public static final int HasSecurity = 0x00040000;
}
