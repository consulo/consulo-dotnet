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

package consulo.csharp.cfs.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.impl.source.tree.injected.Place;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import consulo.annotations.RequiredReadAction;
import consulo.csharp.cfs.lang.CfsTokens;
import consulo.dotnet.psi.DotNetCallArgumentList;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.util.ArrayUtil2;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public class CfsItem extends ASTWrapperPsiElement
{
	public CfsItem(@NotNull ASTNode node)
	{
		super(node);
	}

	@RequiredReadAction
	public int getIndex()
	{
		PsiElement childByType = findChildByType(CfsTokens.INDEX);
		try
		{
			return childByType == null ? -1 : Integer.parseInt(childByType.getText());
		}
		catch(NumberFormatException e)
		{
			return -1;
		}
	}

	@RequiredReadAction
	@Override
	public PsiReference getReference()
	{
		final int index = getIndex();
		if(index == -1)
		{
			return null;
		}

		PsiElement element = null;
		Place shreds = InjectedLanguageUtil.getShreds(getContainingFile());
		for(PsiLanguageInjectionHost.Shred shred : shreds)
		{
			PsiLanguageInjectionHost host = shred.getHost();
			if(host == null)
			{
				continue;
			}
			DotNetCallArgumentList callArgumentList = PsiTreeUtil.getTopmostParentOfType(host, DotNetCallArgumentList.class);
			if(callArgumentList == null)
			{
				continue;
			}
			int k = -1;
			DotNetExpression[] expressions = callArgumentList.getExpressions();
			for(int i = 0; i < expressions.length; i++)
			{
				DotNetExpression expression = expressions[i];
				if(PsiTreeUtil.isAncestor(expression, host, false))
				{
					k = i;
					break;
				}
			}
			assert k != -1;

			element = ArrayUtil2.safeGet(expressions, k + index + 1);
			break;
		}

		return new PsiReferenceBase.Immediate<PsiElement>(this, true, element)
		{
			@Override
			protected TextRange calculateDefaultRangeInElement()
			{
				return new TextRange(0, CfsItem.this.getTextLength());
			}

			@Override
			public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
			{
				return null;
			}
		};
	}
}
