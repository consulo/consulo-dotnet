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

package org.mustbe.consulo.msil.lang.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceExpressionImpl extends MsilElementImpl implements DotNetReferenceExpression
{
	public MsilReferenceExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromParent)
	{
		return null;
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean b)
	{
		return new ResolveResult[0];
	}

	@Nullable
	@Override
	public PsiElement getQualifier()
	{
		return null;
	}

	@Nullable
	@Override
	public String getReferenceName()
	{
		return null;
	}

	@Override
	public PsiElement getElement()
	{
		return null;
	}

	@Override
	public TextRange getRangeInElement()
	{
		return null;
	}

	@Nullable
	@Override
	public PsiElement resolve()
	{
		return null;
	}

	@NotNull
	@Override
	public String getCanonicalText()
	{
		return null;
	}

	@Override
	public PsiElement handleElementRename(String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element)
	{
		return false;
	}

	@NotNull
	@Override
	public Object[] getVariants()
	{
		return new Object[0];
	}

	@Override
	public boolean isSoft()
	{
		return false;
	}
}
