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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpSoftTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeRuntimeType;
import org.mustbe.consulo.dotnet.psi.DotNetNativeType;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 13.12.13.
 */
public class CSharpNativeTypeImpl extends CSharpElementImpl implements DotNetNativeType
{
	private static final Map<IElementType, CSharpNativeRuntimeType> ELEMENT_TYPE_TO_TYPE = new HashMap<IElementType, CSharpNativeRuntimeType>()
	{
		{
			put(CSharpTokens.BOOL_KEYWORD, CSharpNativeRuntimeType.BOOL);
			put(CSharpTokens.DOUBLE_KEYWORD, CSharpNativeRuntimeType.DOUBLE);
			put(CSharpTokens.FLOAT_KEYWORD, CSharpNativeRuntimeType.FLOAT);
			put(CSharpTokens.CHAR_KEYWORD, CSharpNativeRuntimeType.CHAR);
			put(CSharpTokens.OBJECT_KEYWORD, CSharpNativeRuntimeType.OBJECT);
			put(CSharpTokens.STRING_KEYWORD, CSharpNativeRuntimeType.STRING);
			put(CSharpTokens.SBYTE_KEYWORD, CSharpNativeRuntimeType.SBYTE);
			put(CSharpTokens.BYTE_KEYWORD, CSharpNativeRuntimeType.BYTE);
			put(CSharpTokens.INT_KEYWORD, CSharpNativeRuntimeType.INT);
			put(CSharpTokens.UINT_KEYWORD, CSharpNativeRuntimeType.UINT);
			put(CSharpTokens.LONG_KEYWORD, CSharpNativeRuntimeType.LONG);
			put(CSharpTokens.ULONG_KEYWORD, CSharpNativeRuntimeType.ULONG);
			put(CSharpTokens.VOID_KEYWORD, CSharpNativeRuntimeType.VOID);
			put(CSharpTokens.SHORT_KEYWORD, CSharpNativeRuntimeType.SHORT);
			put(CSharpTokens.USHORT_KEYWORD, CSharpNativeRuntimeType.USHORT);
		}
	};

	public CSharpNativeTypeImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitNativeType(this);
	}

	@Override
	public DotNetRuntimeType toRuntimeType()
	{
		IElementType elementType = getTypeElement().getNode().getElementType();
		if(elementType == CSharpSoftTokens.VAR_KEYWORD)
		{
			return DotNetRuntimeType.AUTO_TYPE;
		}
		return ELEMENT_TYPE_TO_TYPE.get(elementType);
	}

	@NotNull
	@Override
	public PsiElement getTypeElement()
	{
		return findNotNullChildByFilter(CSharpTokenSets.PRIMITIVE_TYPES);
	}
}
