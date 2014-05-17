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
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodCallParameterList;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodCallParameterListOwner;
import org.mustbe.consulo.csharp.lang.psi.CSharpReferenceExpression;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;

/**
 * @author VISTALL
 * @since 17.05.14
 */
public class CSharpConstructorSuperCallImpl extends CSharpElementImpl implements CSharpMethodCallParameterListOwner
{
	public CSharpConstructorSuperCallImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitConstructorSuperCall(this);
	}

	@Nullable
	@Override
	public CSharpMethodCallParameterList getParameterList()
	{
		return findChildByClass(CSharpMethodCallParameterList.class);
	}

	@Nullable
	@Override
	public PsiElement resolveToCallable()
	{
		return null;
	}

	@NotNull
	public CSharpReferenceExpression getExpression()
	{
		return findNotNullChildByClass(CSharpReferenceExpression.class);
	}

	@Override
	public ResolveResult[] multiResolve(boolean incompleteCode)
	{
		return getExpression().multiResolve(incompleteCode);
	}

	@NotNull
	@Override
	public DotNetExpression[] getParameterExpressions()
	{
		CSharpMethodCallParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetExpression.EMPTY_ARRAY : parameterList.getExpressions();
	}
}
