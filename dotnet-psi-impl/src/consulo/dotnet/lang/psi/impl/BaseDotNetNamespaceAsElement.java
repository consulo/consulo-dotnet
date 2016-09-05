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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.resolve.DotNetTypeTransformer;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.NotNullFunction;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public abstract class BaseDotNetNamespaceAsElement extends LightElement implements DotNetNamespaceAsElement
{
	public static Key<GlobalSearchScope> RESOLVE_SCOPE = Key.create("resolve.scope");
	public static Key<ChildrenFilter> FILTER = Key.create("namespace.children.filter");

	protected Project myProject;
	protected String myQName;

	protected BaseDotNetNamespaceAsElement(@NotNull Project project, @NotNull Language language, @NotNull String qName)
	{
		super(PsiManager.getInstance(project), language);
		myQName = qName;
		myProject = project;
	}

	@Override
	@RequiredReadAction
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
			@NotNull ResolveState state,
			PsiElement lastParent,
			@NotNull PsiElement place)
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

	@NotNull
	@Override
	@RequiredReadAction
	public PsiElement[] findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter)
	{
		return findChildren(name, globalSearchScope, DotNetTypeTransformer.INSTANCE, filter);
	}

	@NotNull
	@Override
	@RequiredReadAction
	public PsiElement[] findChildren(@NotNull String name,
			@NotNull GlobalSearchScope globalSearchScope,
			@NotNull NotNullFunction<PsiElement, PsiElement> transformer,
			@NotNull ChildrenFilter filter)
	{
		List<PsiElement> list = new SmartList<PsiElement>();
		for(PsiElement element : getChildren(globalSearchScope, filter))
		{
			addIfNameEqual(list, element, name);
		}
		return toArray(list, transformer);
	}

	protected static void addIfNameEqual(List<PsiElement> toAdd, PsiElement element, String name)
	{
		if(element instanceof PsiNamedElement && Comparing.equal(name, ((PsiNamedElement) element).getName()))
		{
			toAdd.add(element);
		}
	}

	@RequiredReadAction
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public PsiElement[] getChildren(@NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter)
	{
		return getChildren(globalSearchScope, DotNetTypeTransformer.INSTANCE, filter);
	}

	@RequiredReadAction
	@NotNull
	@SuppressWarnings("unchecked")
	@Override
	public PsiElement[] getChildren(@NotNull GlobalSearchScope globalSearchScope,
			@NotNull NotNullFunction<PsiElement, PsiElement> transformer,
			@NotNull ChildrenFilter filter)
	{
		switch(filter)
		{
			case ONLY_ELEMENTS:
				return toArray(getOnlyElements(globalSearchScope), transformer);
			case ONLY_NAMESPACES:
				return toArray(getOnlyNamespaces(globalSearchScope), transformer);
			case NONE:
				Collection<? extends PsiElement> onlyElements = getOnlyElements(globalSearchScope);
				Collection<? extends PsiElement> onlyNamespaces = getOnlyNamespaces(globalSearchScope);
				List newList = new ArrayList(onlyElements.size() + onlyNamespaces.size());

				newList.addAll(onlyElements);
				newList.addAll(onlyNamespaces);
				return toArray(newList, transformer);
		} return PsiElement.EMPTY_ARRAY;
	}

	@NotNull
	@RequiredReadAction
	protected Collection<? extends PsiElement> getOnlyElements(@NotNull GlobalSearchScope globalSearchScope)
	{
		return Collections.emptyList();
	}

	@NotNull
	@RequiredReadAction
	protected Collection<? extends PsiElement> getOnlyNamespaces(@NotNull GlobalSearchScope globalSearchScope)
	{
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static PsiElement[] toArray(@NotNull Collection<? extends PsiElement> collection, NotNullFunction<PsiElement, PsiElement> transformer)
	{
		if(collection.isEmpty())
		{
			return PsiElement.EMPTY_ARRAY;
		}
		PsiElement[] array = new PsiElement[collection.size()];
		if(collection instanceof List)
		{
			for(int i = 0; i < collection.size(); i++)
			{
				PsiElement element = ((List<PsiElement>) collection).get(i);
				array[i] = transformer.fun(element);
			}
		}
		else
		{
			int i = 0;
			for(PsiElement element : collection)
			{
				array[i++] = transformer.fun(element);
			}
		}
		return array;
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
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}
}
