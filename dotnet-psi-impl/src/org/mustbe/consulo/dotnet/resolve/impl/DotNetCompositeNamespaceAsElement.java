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

package org.mustbe.consulo.dotnet.resolve.impl;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.DeprecationInfo;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 11.02.14
 */
@Deprecated
@DeprecationInfo(value = "Use DotNetPsiSearcher class instead of DotNetPsiFacade", until = "1.0")
public class DotNetCompositeNamespaceAsElement extends LightElement implements DotNetNamespaceAsElement
{
	private final DotNetNamespaceAsElement[] myArray;

	public DotNetCompositeNamespaceAsElement(Project project, DotNetNamespaceAsElement[] array)
	{
		super(PsiManager.getInstance(project), Language.ANY);
		myArray = array;
		assert myArray.length != 0;
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement
			place)
	{
		for(DotNetNamespaceAsElement dotNetNamespaceAsElement : myArray)
		{
			if(!dotNetNamespaceAsElement.processDeclarations(processor, state, lastParent, place))
			{
				return false;
			}
		}
		return true;
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return myArray[0].getPresentableParentQName();
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		return myArray[0].getPresentableQName();
	}

	@Override
	public String toString()
	{
		return getPresentableQName();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}

	@NotNull
	@Override
	public Collection<? extends PsiElement> getChildren(@NotNull GlobalSearchScope globalSearchScope, boolean withChildNamespaces)
	{
		return Collections.emptyList();
	}

	@NotNull
	@Override
	public PsiElement[] findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, boolean withChildNamespaces)
	{
		return PsiElement.EMPTY_ARRAY;
	}
}
