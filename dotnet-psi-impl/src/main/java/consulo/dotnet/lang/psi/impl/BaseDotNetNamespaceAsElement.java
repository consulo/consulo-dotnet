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

package consulo.dotnet.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.resolve.DotNetTypeTransformer;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiManager;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import consulo.util.collection.ContainerUtil;
import consulo.util.collection.SmartList;
import consulo.util.dataholder.Key;
import consulo.util.lang.Comparing;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public abstract class BaseDotNetNamespaceAsElement extends LightElement implements DotNetNamespaceAsElement
{
	public static final Key<GlobalSearchScope> RESOLVE_SCOPE = Key.create("resolve.scope");
	public static final Key<ChildrenFilter> FILTER = Key.create("namespace.children.filter");

	protected Project myProject;
	protected String myQName;

	protected BaseDotNetNamespaceAsElement(@Nonnull Project project, @Nonnull Language language, @Nonnull String qName)
	{
		super(PsiManager.getInstance(project), language);
		myQName = qName;
		myProject = project;
	}

	@Override
	@RequiredReadAction
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
	{
		GlobalSearchScope globalSearchScope = state.get(RESOLVE_SCOPE);
		if(globalSearchScope == null)
		{
			throw new IllegalArgumentException("Please specify RESOLVE_SCOPE key");
		}

		ChildrenFilter filter = state.get(FILTER);
		if(filter == null)
		{
			throw new IllegalArgumentException("Please specify FILTER key");
		}

		for(PsiElement element : getChildren(globalSearchScope, filter))
		{
			if(!processor.execute(element, state))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isWritable()
	{
		return true;
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public Collection<PsiElement> findChildren(@Nonnull String name, @Nonnull GlobalSearchScope globalSearchScope, @Nonnull ChildrenFilter filter)
	{
		return findChildren(name, globalSearchScope, DotNetTypeTransformer.INSTANCE, filter);
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public Collection<PsiElement> findChildren(@Nonnull String name,
			@Nonnull GlobalSearchScope globalSearchScope,
			@Nonnull NotNullFunction<PsiElement, PsiElement> transformer,
			@Nonnull ChildrenFilter filter)
	{
		List<PsiElement> list = new SmartList<>();
		for(PsiElement element : getChildren(globalSearchScope, filter))
		{
			addIfNameEqual(list, element, name);
		}
		return transformData(list, list.size(), transformer);
	}

	protected static void addIfNameEqual(List<PsiElement> toAdd, PsiElement element, String name)
	{
		if(element instanceof PsiNamedElement && Comparing.equal(name, ((PsiNamedElement) element).getName()))
		{
			toAdd.add(element);
		}
	}

	@RequiredReadAction
	@Nonnull
	@SuppressWarnings("unchecked")
	@Override
	public Collection<PsiElement> getChildren(@Nonnull GlobalSearchScope globalSearchScope, @Nonnull NotNullFunction<PsiElement, PsiElement> transformer, @Nonnull ChildrenFilter filter)
	{
		switch(filter)
		{
			case ONLY_ELEMENTS:
			{
				Set<? extends PsiElement> onlyElements = getOnlyElements(globalSearchScope);
				return transformData(onlyElements, onlyElements.size(), transformer);
			}
			case ONLY_NAMESPACES:
			{
				Set<? extends PsiElement> onlyNamespaces = getOnlyNamespaces(globalSearchScope);
				return transformData(onlyNamespaces, onlyNamespaces.size(), transformer);
			}
			case NONE:
			{
				Set<? extends PsiElement> onlyElements = getOnlyElements(globalSearchScope);
				Set<? extends PsiElement> onlyNamespaces = getOnlyNamespaces(globalSearchScope);

				return transformData(ContainerUtil.concat(onlyElements, onlyNamespaces), onlyElements.size() + onlyNamespaces.size(), transformer);
			}
		}
		return Collections.emptyList();
	}

	@Nonnull
	@RequiredReadAction
	protected Set<? extends PsiElement> getOnlyElements(@Nonnull GlobalSearchScope globalSearchScope)
	{
		return Collections.emptySet();
	}

	@Nonnull
	@RequiredReadAction
	protected Set<? extends PsiElement> getOnlyNamespaces(@Nonnull GlobalSearchScope globalSearchScope)
	{
		return Collections.emptySet();
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public static List<PsiElement> transformData(@Nonnull Iterable<? extends PsiElement> collection, int size, NotNullFunction<PsiElement, PsiElement> transformer)
	{
		if(size == 0)
		{
			return Collections.emptyList();
		}

		List<PsiElement> list = new ArrayList<>(size);
		for(PsiElement element : collection)
		{
			list.add(transformer.fun(element));
		}
		return list;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		if(myQName.length() == 0)
		{
			return null;
		}
		return StringUtil.getPackageName(myQName);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableQName()
	{
		return myQName;
	}

	@Override
	public String getName()
	{
		return StringUtil.getShortName(myQName);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ":" + getPresentableQName();
	}

	@Override
	public PsiElement setName(@Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}
}
