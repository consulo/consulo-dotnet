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
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpFileFactory;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpUsingNamespaceListImpl extends CSharpElementImpl
{
	public CSharpUsingNamespaceListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
	public CSharpUsingNamespaceStatementImpl[] getStatements()
	{
		return findChildrenByClass(CSharpUsingNamespaceStatementImpl.class);
	}

	public void addUsing(@NotNull String qName)
	{
		CSharpUsingNamespaceStatementImpl newStatement = CSharpFileFactory.createUsingStatement(getProject(), qName);

		CSharpUsingNamespaceStatementImpl[] statements = getStatements();

		CSharpUsingNamespaceStatementImpl last = statements[statements.length - 1];

		LeafPsiElement leafPsiElement = new LeafPsiElement(TokenType.WHITE_SPACE, "\n");

		getNode().addChild(leafPsiElement);

		addAfter(newStatement, leafPsiElement);
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement
			place)
	{
		for(CSharpUsingNamespaceStatementImpl cSharpUsingStatement : getStatements())
		{
			if(!CSharpResolveUtil.treeWalkUp(processor, cSharpUsingStatement, cSharpUsingStatement, state))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitUsingNamespaceList(this);
	}
}
