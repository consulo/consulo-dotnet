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

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpNativeRuntimeType implements DotNetRuntimeType
{
	private static final Map<IElementType, CSharpNativeRuntimeType> ELEMENT_TYPE_TO_TYPE = new HashMap<IElementType, CSharpNativeRuntimeType>()
	{
		{
			put(CSharpTokens.BOOL_KEYWORD, new CSharpNativeRuntimeType("bool", "System.Boolean"));
			put(CSharpTokens.DOUBLE_KEYWORD, new CSharpNativeRuntimeType("double", "System.Double"));
			put(CSharpTokens.FLOAT_KEYWORD, new CSharpNativeRuntimeType("float", "System.Float"));
			put(CSharpTokens.CHAR_KEYWORD, new CSharpNativeRuntimeType("char", "System.Char"));
			put(CSharpTokens.OBJECT_KEYWORD, new CSharpNativeRuntimeType("object", "System.Object"));
			put(CSharpTokens.STRING_KEYWORD, new CSharpNativeRuntimeType("string", "System.String"));
			put(CSharpTokens.SBYTE_KEYWORD, new CSharpNativeRuntimeType("sbyte", "System.SByte"));
			put(CSharpTokens.BYTE_KEYWORD, new CSharpNativeRuntimeType("byte", "System.Byte"));
			put(CSharpTokens.INT_KEYWORD, new CSharpNativeRuntimeType("int", "System.Int32"));
			put(CSharpTokens.UINT_KEYWORD, new CSharpNativeRuntimeType("uint", "System.UInt32"));
			put(CSharpTokens.LONG_KEYWORD, new CSharpNativeRuntimeType("long", "System.Long"));
			put(CSharpTokens.ULONG_KEYWORD, new CSharpNativeRuntimeType("ulong", "System.ULong"));
			put(CSharpTokens.VOID_KEYWORD, new CSharpNativeRuntimeType("void", "System.Void"));
		}
	};

	@NotNull
	public static DotNetRuntimeType toType(IElementType elementType)
	{
		CSharpNativeRuntimeType cSharpNativeRuntimeType = ELEMENT_TYPE_TO_TYPE.get(elementType);
		assert cSharpNativeRuntimeType != null : elementType;
		return cSharpNativeRuntimeType;
	}

	private final String myPresentableText;
	private final String myWrapperQualifiedClass;

	public CSharpNativeRuntimeType(String presentableText, String wrapperQualifiedClass)
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
}
