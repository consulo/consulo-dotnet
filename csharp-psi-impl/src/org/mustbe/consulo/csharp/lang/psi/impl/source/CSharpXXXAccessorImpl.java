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
import org.mustbe.consulo.csharp.lang.psi.CSharpPropertyDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpSoftTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.impl.light.builder.CSharpLightLocalVariableBuilder;
import org.mustbe.consulo.dotnet.psi.DotNetStatement;
import org.mustbe.consulo.dotnet.psi.DotNetXXXAccessor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class CSharpXXXAccessorImpl extends CSharpMemberImpl implements DotNetXXXAccessor
{
	public CSharpXXXAccessorImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
	@Override
	public PsiElement getNameIdentifier()
	{
		return findNotNullChildByType(CSharpTokenSets.XXX_ACCESSOR_START);
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent,
			@NotNull PsiElement place)
	{
		if(getAccessorType() == CSharpSoftTokens.SET_KEYWORD)
		{
			CSharpPropertyDeclaration propertyDeclaration = PsiTreeUtil.getParentOfType(this, CSharpPropertyDeclaration.class);
			if(propertyDeclaration == null)
			{
				return true;
			}

			CSharpLightLocalVariableBuilder builder = new CSharpLightLocalVariableBuilder(propertyDeclaration)
					.withName(VALUE)
					.withParent(this)
					.withTypeRef(propertyDeclaration.toTypeRef());

			if(!processor.execute(builder, state))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitXXXAccessor(this);
	}

	@NotNull
	@Override
	public IElementType getAccessorType()
	{
		return getNameIdentifier().getNode().getElementType();
	}

	@Nullable
	@Override
	public PsiElement getCodeBlock()
	{
		return findChildByClass(DotNetStatement.class);
	}
}
