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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingListImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtilCore;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 17.12.13.
 */
public class CSharpPsiScopesUtil extends PsiScopesUtilCore
{
	public static boolean processUsing(@NotNull PsiElement element, @NotNull PsiScopeProcessor processor, @NotNull ResolveState state)
	{
		List<PsiElement> list = new SmartList<PsiElement>();

		PsiElement it = element.getParent().getFirstChild();
		while(it != null)
		{
			if(it instanceof CSharpUsingListImpl)
			{
				list.add(it);
			}
			else if(it == element)
			{
				break;
			}
			it = it.getNextSibling();
		}

		Collections.reverse(list);

		for(PsiElement psiElement : list)
		{
			if(!CSharpPsiScopesUtil.treeWalkUp(processor, psiElement, psiElement, state))
			{
				return false;
			}
		}

		return true;
	}
}
