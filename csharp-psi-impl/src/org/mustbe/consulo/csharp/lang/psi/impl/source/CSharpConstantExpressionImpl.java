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

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeRuntimeType;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class CSharpConstantExpressionImpl extends CSharpElementImpl implements DotNetExpression
{
	public CSharpConstantExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitConstantExpression(this);
	}

	@NotNull
	@Override
	public DotNetRuntimeType toRuntimeType()
	{
		PsiElement byType = findChildByType(CSharpTokenSets.CONSTANT_LITERALS);
		assert byType != null;
		IElementType elementType = byType.getNode().getElementType();
		if(elementType == CSharpTokens.STRING_LITERAL)
		{
			return CSharpNativeRuntimeType.STRING;
		}
		else if(elementType == CSharpTokens.VERBATIM_STRING_LITERAL)
		{
			return CSharpNativeRuntimeType.STRING;
		}
		else if(elementType == CSharpTokens.UINTEGER_LITERAL)
		{
			return CSharpNativeRuntimeType.UINT;
		}
		else if(elementType == CSharpTokens.ULONG_LITERAL)
		{
			return CSharpNativeRuntimeType.ULONG;
		}
		else if(elementType == CSharpTokens.INTEGER_LITERAL)
		{
			return CSharpNativeRuntimeType.INT;
		}
		else if(elementType == CSharpTokens.LONG_LITERAL)
		{
			return CSharpNativeRuntimeType.LONG;
		}
		else if(elementType == CSharpTokens.FLOAT_LITERAL)
		{
			return CSharpNativeRuntimeType.FLOAT;
		}
		else if(elementType == CSharpTokens.DOUBLE_LITERAL)
		{
			return CSharpNativeRuntimeType.DOUBLE;
		}
		else if(elementType == CSharpTokens.NULL_LITERAL)
		{
			return DotNetRuntimeType.AUTO_TYPE;
		}
		else if(elementType == CSharpTokens.BOOL_LITERAL)
		{
			return CSharpNativeRuntimeType.BOOL;
		}
		return DotNetRuntimeType.ERROR_TYPE;
	}
}
