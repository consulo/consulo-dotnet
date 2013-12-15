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

package org.mustbe.consulo.csharp.lang.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import lombok.val;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public class CSharpFormattingBlock extends AbstractBlock implements CSharpElements, CSharpTokens, CSharpTokenSets
{
	public CSharpFormattingBlock(@NotNull ASTNode node)
	{
		super(node, null, null);
	}

	@Override
	protected List<Block> buildChildren()
	{
		val psi = getNode().getPsi();
		val elementType = getNode().getElementType();
		val list = new ArrayList<Block>();

		addBlocks(list);

		return list.isEmpty() ? Collections.<Block>emptyList() : list;
	}

	private void addBlocks(ArrayList<Block> list)
	{
		ASTNode[] children = getNode().getChildren(null);
		for(val it : children)
		{
			IElementType elementType = it.getElementType();
			if(elementType == WHITE_SPACE || it.getPsi() instanceof PsiErrorElement)
			{
				continue;
			}

			if(KEYWORDS.contains(elementType) || elementType == IDENTIFIER || elementType == REFERENCE_EXPRESSION || elementType == MODIFIER_LIST)
			{
				if(elementType == MODIFIER_LIST)
				{
					ASTNode[] c = it.getChildren(MODIFIERS);
					if(c.length == 0)
					{
						continue;
					}
				}

				list.add(new CSharpFormattingLeafBlock(it));
			}
			else
			{
				list.add(new CSharpFormattingBlock(it));
			}
		}
	}


	@Override
	public Indent getIndent()
	{
		val elementType = getNode().getElementType();
		if(elementType == NAMESPACE_DECLARATION ||
				elementType == TYPE_DECLARATION ||
				elementType == METHOD_DECLARATION ||
				elementType == FIELD_DECLARATION ||
				elementType == PROPERTY_DECLARATION ||
				elementType == PROPERTY_ACCESSOR ||
				elementType == EVENT_DECLARATION ||
				elementType == EVENT_ACCESSOR ||
				elementType == CONSTRUCTOR_DECLARATION)
		{
			PsiElement psiElement = getNode().getPsi().getParent();
			if(psiElement instanceof PsiFile)
			{
				return Indent.getNoneIndent();
			}
			return Indent.getNormalIndent();
		}
		/*else if(elementType == CODE_BLOCK)
		{
			return Indent.getNormalIndent();
		}*/
		else if(elementType == LBRACE || elementType == RBRACE)
		{
			return Indent.getNoneIndent();
		}
		else if(elementType == MODIFIER_LIST)
		{
			return Indent.getNoneIndent();
		}
	/*	else if(elementType == CSharpParserDefinition.FILE_ELEMENT_TYPE)
		{
			return Indent.getNoneIndent();
		}  */
		else if(elementType == CSharpStubElements.FILE)
		{
			return Indent.getNoneIndent();
		}
		else
		{
			return Indent.getNoneIndent();
		}
	}

	@Nullable
	@Override
	protected Indent getChildIndent()
	{
		if(getNode().getElementType() == CSharpStubElements.FILE)
		{
			return Indent.getNoneIndent();
		}
		return Indent.getNormalIndent();
	}

	@Nullable
	@Override
	public Spacing getSpacing(@Nullable Block block, @NotNull Block block2)
	{
		return null;
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}
}
