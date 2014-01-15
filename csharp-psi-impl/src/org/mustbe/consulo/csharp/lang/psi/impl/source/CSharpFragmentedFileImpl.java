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
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiCodeFragment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpFragmentedFileImpl extends CSharpFileImpl implements PsiCodeFragment
{
	private final PsiElement myOriginal;
	private GlobalSearchScope myResolveScope;

	public CSharpFragmentedFileImpl(@NotNull FileViewProvider viewProvider, PsiElement scope)
	{
		super(viewProvider);
		myOriginal = scope;
		myResolveScope = scope.getResolveScope();
	}

	@Override
	public void forceResolveScope(GlobalSearchScope searchScope)
	{
		myResolveScope = searchScope;
	}

	@Override
	public GlobalSearchScope getForcedResolveScope()
	{
		return myResolveScope;
	}
}
