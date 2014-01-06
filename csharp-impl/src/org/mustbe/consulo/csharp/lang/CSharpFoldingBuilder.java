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

package org.mustbe.consulo.csharp.lang;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.macro.MacroActiveBlockInfo;
import org.mustbe.consulo.csharp.lang.parser.macro.MacroesInfo;
import org.mustbe.consulo.csharp.lang.psi.CSharpBodyWithBraces;
import org.mustbe.consulo.csharp.lang.psi.CSharpRecursiveElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpBlockStatementImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMacroBlockStartImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMacroBodyImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingNamespaceListImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingNamespaceStatementImpl;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
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

		MacroesInfo temp = null;
		val containingFile = psi.getContainingFile();
		if(containingFile != null)
		{
			temp = containingFile.getUserData(MacroesInfo.MACROES_INFO_KEY);
		}
		if(temp == null)
		{
			temp = MacroesInfo.EMPTY;
		}

		val macroesInfo = temp;
		psi.accept(new CSharpRecursiveElementVisitor()
		{
			@Override
			public void visitMacroBlockStart(CSharpMacroBlockStartImpl start)
			{
				MacroActiveBlockInfo macroActiveBlockInfo = macroesInfo.findStartActiveBlock(start.getFirstChild().getTextOffset());
				if(macroActiveBlockInfo == null || macroActiveBlockInfo.getStopOffset() == -1 || macroActiveBlockInfo.getElementType() !=
						CSharpTokens.MACRO_REGION_KEYWORD)
				{
					return;
				}
				assert containingFile != null;
				PsiElement elementAt = containingFile.findElementAt(macroActiveBlockInfo.getStopOffset());
				// it ill return keyword #endregion of #endif

				if(elementAt == null)
				{
					return;
				}

				PsiElement parent = elementAt.getParent();
				foldingList.add(new FoldingDescriptor(start, new TextRange(start.getTextRange().getStartOffset(),
						parent.getTextRange().getEndOffset())));
			}

			@Override
			public void visitMacroBody(CSharpMacroBodyImpl block)
			{
				foldingList.add(new FoldingDescriptor(block, block.getTextRange()));
			}

			@Override
			public void visitUsingNamespaceList(CSharpUsingNamespaceListImpl list)
			{
				CSharpUsingNamespaceStatementImpl[] statements = list.getStatements();
				if(statements.length <= 1)
				{
					return;
				}

				CSharpUsingNamespaceStatementImpl statement = statements[0];
				DotNetReferenceExpression namespaceReference = statement.getNamespaceReference();
				if(namespaceReference == null)
				{
					return;
				}

				ASTNode usingKeyword = statement.getNode().findChildByType(CSharpTokens.USING_KEYWORD);

				assert usingKeyword != null;

				int startOffset = usingKeyword.getTextRange().getEndOffset() + 1;
				int endOffset = statements[statements.length - 1].getLastChild().getTextRange().getEndOffset();

				foldingList.add(new FoldingDescriptor(list, new TextRange(startOffset, endOffset)));
			}

			@Override
			public void visitBlockStatement(CSharpBlockStatementImpl statement)
			{
				super.visitBlockStatement(statement);

				if(!(statement.getParent() instanceof DotNetMethodDeclaration))
				{
					return;
				}
				addBodyWithBraces(foldingList, statement);
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
		if(psi instanceof CSharpUsingNamespaceListImpl)
		{
			return "...";
		}
		else if(psi instanceof CSharpBlockStatementImpl)
		{
			return "{...}";
		}
		else if(psi instanceof CSharpMacroBodyImpl)
		{
			return "<non active block>";
		}
		else if(psi instanceof CSharpMacroBlockStartImpl)
		{
			PsiElement startElement = ((CSharpMacroBlockStartImpl) psi).getStartElement();

			PsiElement value = ((CSharpMacroBlockStartImpl) psi).getValue();
			String valueText = value == null ? "<empty>" : value.getText();
			if(startElement != null)
			{
				IElementType elementType = startElement.getNode().getElementType();
				if(elementType == CSharpTokens.MACRO_IF_KEYWORD)
				{
					return "#if " + valueText;
				}
				else if(elementType == CSharpTokens.MACRO_REGION_KEYWORD)
				{
					String text = psi.getText();
					String textOfMessage = text.substring(startElement.getTextLength(), text.length());
					return textOfMessage.trim();
				}
			}
			return "##";
		}
		return null;
	}

	@Override
	public boolean isCollapsedByDefault(@NotNull ASTNode astNode)
	{
		PsiElement psi = astNode.getPsi();
		if(psi instanceof CSharpUsingNamespaceListImpl)
		{
			return true;
		}
		else if(psi instanceof CSharpMacroBodyImpl)
		{
			return true;
		}

		return false;
	}
}
