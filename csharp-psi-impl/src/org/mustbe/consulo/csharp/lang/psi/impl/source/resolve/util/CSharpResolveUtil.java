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

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpNamespaceDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpAttributeListImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFileImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingListImpl;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 17.12.13.
 */
@Logger
public class CSharpResolveUtil
{
	public static final Key<Boolean> QUALIFIED = Key.create("qualified");

	public static boolean treeWalkUp(@NotNull PsiScopeProcessor processor, @NotNull PsiElement entrance, @Nullable PsiElement maxScope)
	{
		return treeWalkUp(processor, entrance, maxScope, ResolveState.initial());
	}

	public static boolean treeWalkUp(@NotNull final PsiScopeProcessor processor, @NotNull final PsiElement entrance,
			@Nullable final PsiElement maxScope, @NotNull final ResolveState state)
	{
		if(!entrance.isValid())
		{
			LOGGER.error(new PsiInvalidElementAccessException(entrance));
		}
		boolean q = processor.getHint(QUALIFIED) == Boolean.TRUE;

		PsiElement prevParent = entrance;
		PsiElement scope = entrance;

		while(scope != null)
		{
			ProgressIndicatorProvider.checkCanceled();

			if(q && scope instanceof PsiFile)
			{
				break;
			}

			if(!scope.processDeclarations(processor, state, prevParent, entrance))
			{
				return false; // resolved
			}

			if(scope instanceof CSharpNamespaceDeclaration || scope instanceof CSharpTypeDeclaration || scope instanceof CSharpAttributeListImpl)
			{
				if(scope instanceof CSharpAttributeListImpl && scope.getParent() instanceof CSharpFileImpl || !(scope instanceof
						CSharpAttributeListImpl))
				{
					if(!processUsing(scope, processor, state))
					{
						return false;
					}
				}
			}

			if(scope == maxScope)
			{
				break;
			}
			prevParent = scope;
			scope = prevParent.getContext();
			if(scope != null && scope != prevParent.getParent() && !scope.isValid())
			{
				break;
			}

		}

		return true;
	}

	@Deprecated
	public static boolean processUsingOld(@NotNull PsiElement element, @NotNull PsiScopeProcessor processor, @NotNull ResolveState state)
	{
		return true;
	}

	private static boolean processUsing(@NotNull PsiElement element, @NotNull PsiScopeProcessor processor, @NotNull ResolveState state)
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
			if(!treeWalkUp(processor, psiElement, psiElement, state))
			{
				return false;
			}
		}

		return true;
	}
}
