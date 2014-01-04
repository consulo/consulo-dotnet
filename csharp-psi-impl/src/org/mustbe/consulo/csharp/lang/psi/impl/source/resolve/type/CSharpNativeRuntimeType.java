/*
 * Copyright 2013 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpNativeRuntimeType implements DotNetRuntimeType
{
	public static final CSharpNativeRuntimeType BOOL = new CSharpNativeRuntimeType("bool", "System.Boolean");
	public static final CSharpNativeRuntimeType DOUBLE = new CSharpNativeRuntimeType("double", "System.Double");
	public static final CSharpNativeRuntimeType FLOAT = new CSharpNativeRuntimeType("float", "System.Float");
	public static final CSharpNativeRuntimeType CHAR = new CSharpNativeRuntimeType("char", "System.Char");
	public static final CSharpNativeRuntimeType OBJECT = new CSharpNativeRuntimeType("object", "System.Object");
	public static final CSharpNativeRuntimeType STRING = new CSharpNativeRuntimeType("string", "System.String");
	public static final CSharpNativeRuntimeType SBYTE = new CSharpNativeRuntimeType("sbyte", "System.SByte");
	public static final CSharpNativeRuntimeType BYTE =  new CSharpNativeRuntimeType("byte", "System.Byte");
	public static final CSharpNativeRuntimeType INT = new CSharpNativeRuntimeType("int", "System.Int32");
	public static final CSharpNativeRuntimeType UINT =  new CSharpNativeRuntimeType("uint", "System.UInt32");
	public static final CSharpNativeRuntimeType LONG = new CSharpNativeRuntimeType("long", "System.Int64");
	public static final CSharpNativeRuntimeType ULONG = new CSharpNativeRuntimeType("ulong", "System.UInt64");
	public static final CSharpNativeRuntimeType VOID =  new CSharpNativeRuntimeType("void", "System.Void");
	public static final CSharpNativeRuntimeType SHORT = new CSharpNativeRuntimeType("short", "System.Int16");
	public static final CSharpNativeRuntimeType USHORT =  new CSharpNativeRuntimeType("ushort", "System.UInt16");
	public static final CSharpNativeRuntimeType DECIMAL = new CSharpNativeRuntimeType("decimal", "System.Decimal");

	private final String myPresentableText;
	private final String myWrapperQualifiedClass;

	private CSharpNativeRuntimeType(String presentableText, String wrapperQualifiedClass)
	{
		myPresentableText = presentableText;
		myWrapperQualifiedClass = wrapperQualifiedClass;
	}

	@Nullable
	@Override
	public String getPresentableText()
	{
		return myPresentableText;
	}

	@Nullable
	@Override
	public String getQualifiedText()
	{
		return myPresentableText;
	}

	@Override
	public boolean isNullable()
	{
		return false;
	}

	@Nullable
	@Override
	public PsiElement toPsiElement()
	{
		return null;
	}

	@NotNull
	public String getWrapperQualifiedClass()
	{
		return myWrapperQualifiedClass;
	}
}
