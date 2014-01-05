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

package org.mustbe.consulo.csharp.lang.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElements;
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

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public class CSharpFormattingLeafBlock extends AbstractBlock implements CSharpElements, CSharpTokens, CSharpTokenSets
{
	protected CSharpFormattingLeafBlock(@NotNull ASTNode node)
	{
		super(node, null, null);
	}

	@Override
	protected List<Block> buildChildren()
	{
		ASTNode[] astNodes = getNode().getChildren(null);
		if(astNodes.length == 0)
		{
			return Collections.emptyList();
		}
		List<Block> blocks = new ArrayList<Block>(astNodes.length);
		for(ASTNode it : astNodes)
		{
			IElementType elementType = it.getElementType();
			if(elementType == WHITE_SPACE || it.getPsi() instanceof PsiErrorElement)
			{
				continue;
			}

			blocks.add(new CSharpFormattingLeafBlock(it));
		}
		return blocks;
	}

	@Override
	public Indent getIndent()
	{
		IElementType elementType = getNode().getElementType();
		if(CSharpTokenSets.COMMENTS.contains(elementType))
		{
			PsiElement parent = getNode().getPsi().getParent();
			if(!(parent instanceof PsiFile))
			{
				return Indent.getNormalIndent();
			}
		}
		return Indent.getNoneIndent();
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
		return true;
	}
}
