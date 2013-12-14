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

package org.mustbe.consulo.csharp.lang;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpBodyWithBraces;
import org.mustbe.consulo.csharp.lang.psi.CSharpRecursiveElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpCodeBlockImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingListImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingStatementImpl;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import lombok.val;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public class CSharpFoldingBuilder implements FoldingBuilder
{
	@NotNull
	@Override
	public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode astNode, @NotNull Document document)
	{
		val foldingList = new ArrayList<FoldingDescriptor>();

		PsiElement psi = astNode.getPsi();

		psi.accept(new CSharpRecursiveElementVisitor()
		{
			@Override
			public void visitUsingList(CSharpUsingListImpl list)
			{
				CSharpUsingStatementImpl[] statements = list.getStatements();
				if(statements.length == 0)
				{
					return;
				}

				CSharpUsingStatementImpl statement = statements[0];

				ASTNode usingKeyword = statement.getNode().findChildByType(CSharpTokens.USING_KEYWORD);

				assert usingKeyword != null;

				foldingList.add(new FoldingDescriptor(list,
						new TextRange(usingKeyword.getTextRange().getEndOffset() + 1, list.getNode().getTextRange().getEndOffset())));
			}

			@Override
			public void visitCodeBlock(CSharpCodeBlockImpl block)
			{
				super.visitCodeBlock(block);

				addBodyWithBraces(foldingList, block);
			}
		});
		return foldingList.toArray(new FoldingDescriptor[foldingList.size()]);
	}

	private void addBodyWithBraces(List<FoldingDescriptor> list, CSharpBodyWithBraces bodyWithBraces)
	{
		PsiElement leftBrace = bodyWithBraces.getLeftBrace();
		PsiElement rightBrace = bodyWithBraces.getRightBrace();
		if(leftBrace == null || rightBrace == null)
		{
			return;
		}

		list.add(new FoldingDescriptor(bodyWithBraces, new TextRange(leftBrace.getTextRange().getStartOffset(),
				rightBrace.getTextRange().getStartOffset() + rightBrace.getTextLength())));
	}

	@Nullable
	@Override
	public String getPlaceholderText(@NotNull ASTNode astNode)
	{
		PsiElement psi = astNode.getPsi();
		if(psi instanceof CSharpUsingListImpl)
		{
			return "...";
		}
		else if(psi instanceof CSharpCodeBlockImpl)
		{
			return "{...}";
		}
		return null;
	}

	@Override
	public boolean isCollapsedByDefault(@NotNull ASTNode astNode)
	{
		PsiElement psi = astNode.getPsi();
		if(psi instanceof CSharpUsingListImpl)
		{
			return true;
		}
		return false;
	}
}
