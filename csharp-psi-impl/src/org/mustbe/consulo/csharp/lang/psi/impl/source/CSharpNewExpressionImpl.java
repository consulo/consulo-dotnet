/*
 * Copyright 2013-2014 must-be.org
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
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpFieldOrPropertySetBlock;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodCallParameterList;
import org.mustbe.consulo.csharp.lang.psi.CSharpNewExpression;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpAnonymTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpArrayTypeRef;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpNewExpressionImpl extends CSharpElementImpl implements CSharpNewExpression
{
	public CSharpNewExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitNewExpression(this);
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromParent)
	{
		DotNetType type = getNewType();
		if(type == null)
		{
			CSharpFieldOrPropertySetBlock fieldOrPropertySetBlock = getFieldOrPropertySetBlock();
			if(fieldOrPropertySetBlock == null)
			{
				return DotNetTypeRef.ERROR_TYPE;
			}
			return new CSharpAnonymTypeRef(getContainingFile(), fieldOrPropertySetBlock.getSets());
		}
		else
		{
			DotNetTypeRef typeRef = type.toTypeRef();
			for(PsiElement ignored : findChildrenByType(CSharpTokens.LBRACKET))
			{
				typeRef = new CSharpArrayTypeRef(typeRef, 0);
			}
			return typeRef;
		}
	}

	@Nullable
	@Override
	public DotNetType getNewType()
	{
		return findChildByClass(DotNetType.class);
	}

	@Nullable
	@Override
	public CSharpFieldOrPropertySetBlock getFieldOrPropertySetBlock()
	{
		return findChildByClass(CSharpFieldOrPropertySetBlock.class);
	}

	@Nullable
	@Override
	public CSharpMethodCallParameterList getParameterList()
	{
		return findChildByClass(CSharpMethodCallParameterList.class);
	}
}
