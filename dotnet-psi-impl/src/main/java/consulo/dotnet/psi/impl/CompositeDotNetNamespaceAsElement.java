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

package consulo.dotnet.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.resolve.DotNetNamespaceAsElement;
import consulo.language.Language;
import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import consulo.util.collection.ContainerUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public class CompositeDotNetNamespaceAsElement extends BaseDotNetNamespaceAsElement
{
	private List<DotNetNamespaceAsElement> myList;

	public CompositeDotNetNamespaceAsElement(Project project, @Nonnull String qName, List<DotNetNamespaceAsElement> list)
	{
		super(project, Language.ANY, qName);
		myList = list;
	}

	@Override
	public boolean isWritable()
	{
		for(DotNetNamespaceAsElement namespaceAsElement : myList)
		{
			if(namespaceAsElement.isWritable())
			{
				return true;
			}
		}
		return false;
	}

	@RequiredReadAction
	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
	{
		for(DotNetNamespaceAsElement dotNetNamespaceAsElement : myList)
		{
			if(!dotNetNamespaceAsElement.processDeclarations(processor, state, lastParent, place))
			{
				return false;
			}
		}
		return true;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public Collection<PsiElement> findChildren(@Nonnull String name,
											   @Nonnull GlobalSearchScope globalSearchScope,
											   @Nonnull Function<PsiElement, PsiElement> transformer,
											   @Nonnull ChildrenFilter filter)
	{
		Collection<Collection<PsiElement>> list = new ArrayList<>();
		for(DotNetNamespaceAsElement dotNetNamespaceAsElement : myList)
		{
			Collection<PsiElement> children = dotNetNamespaceAsElement.findChildren(name, globalSearchScope, transformer, filter);
			list.add(children);
		}
		return list.isEmpty() ? Collections.emptyList() : ContainerUtil.concat(list);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public Collection<PsiElement> getChildren(@Nonnull GlobalSearchScope globalSearchScope, @Nonnull Function<PsiElement, PsiElement> transformer, @Nonnull ChildrenFilter filter)
	{
		List<Collection<PsiElement>> list = new ArrayList<>();
		for(DotNetNamespaceAsElement element : myList)
		{
			Collection<PsiElement> children = element.getChildren(globalSearchScope, transformer, filter);
			list.add(children);
		}
		return list.isEmpty() ? Collections.emptyList() : ContainerUtil.concat(list);
	}

	@RequiredReadAction
	@Override
	public boolean processChildren(@Nonnull GlobalSearchScope globalSearchScope,
								   @Nonnull Function<PsiElement, PsiElement> transformer,
								   @Nonnull ChildrenFilter filter,
								   @Nonnull Predicate<PsiElement> processor)
	{
		for(DotNetNamespaceAsElement element : myList)
		{
			if(!element.processChildren(globalSearchScope, transformer, filter, processor))
			{
				return false;
			}
		}
		return true;
	}
}
