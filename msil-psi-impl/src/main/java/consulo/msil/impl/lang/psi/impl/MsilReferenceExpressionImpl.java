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

package consulo.msil.impl.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.document.util.TextRange;
import consulo.dotnet.psi.DotNetReferenceExpression;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.ResolveResult;
import consulo.language.util.IncorrectOperationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceExpressionImpl extends MsilElementImpl implements DotNetReferenceExpression
{
	public MsilReferenceExpressionImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitElement(this);
	}

	@Nonnull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromParent)
	{
		return DotNetTypeRef.ERROR_TYPE;
	}

	@RequiredReadAction
	@Nonnull
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

	@RequiredReadAction
	@Override
	public PsiElement getElement()
	{
		return null;
	}

	@RequiredReadAction
	@Override
	public TextRange getRangeInElement()
	{
		return null;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement resolve()
	{
		return null;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public String getCanonicalText()
	{
		return null;
	}

	@RequiredWriteAction
	@Override
	public PsiElement handleElementRename(String s) throws IncorrectOperationException
	{
		return null;
	}

	@RequiredWriteAction
	@Override
	public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException
	{
		return null;
	}

	@RequiredReadAction
	@Override
	public boolean isReferenceTo(PsiElement element)
	{
		return false;
	}

	@RequiredReadAction
	@Override
	public boolean isSoft()
	{
		return false;
	}
}
