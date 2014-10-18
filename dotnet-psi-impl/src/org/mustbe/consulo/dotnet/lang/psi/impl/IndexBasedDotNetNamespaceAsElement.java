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

package org.mustbe.consulo.dotnet.lang.psi.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.lang.Language;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.containers.ContainerUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public abstract class IndexBasedDotNetNamespaceAsElement extends BaseDotNetNamespaceAsElement
{
	public static Key<Boolean> PROCESS_CHILD_NAMESPACES = Key.create("process.child.namespaces");
	public static Key<GlobalSearchScope> RESOLVE_SCOPE = Key.create("resolve.scope");

	protected String myIndexKey;

	protected IndexBasedDotNetNamespaceAsElement(@NotNull Project project, @NotNull Language language, @NotNull String indexKey,
			@NotNull String qName)
	{
		super(project, language, qName);
		myIndexKey = indexKey;
	}

	@NotNull
	public abstract StringStubIndexExtension<? extends PsiElement> getHardIndexExtension();

	@NotNull
	public abstract StringStubIndexExtension<? extends PsiElement> getSoftIndexExtension();

	@NotNull
	@Override
	public PsiElement[] findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter)
	{
		switch(filter)
		{
			case ONLY_ELEMENTS:
				List<PsiElement> elements = new SmartList<PsiElement>();

				Collection<? extends PsiElement> psiElements = getHardIndexExtension().get(myIndexKey, myProject, globalSearchScope);
				for(PsiElement psiElement : psiElements)
				{
					ProgressManager.checkCanceled();

					addIfNameEqual(elements, psiElement, name);
				}
				return ContainerUtil.toArray(elements, PsiElement.ARRAY_FACTORY);
			case ONLY_NAMESPACES:
				val newQualifiedName = QualifiedName.fromDottedString(myQName).append(name);

				Collection<? extends PsiElement> otherNamespaces = getSoftIndexExtension().get(newQualifiedName.toString(), myProject, globalSearchScope);

				if(!otherNamespaces.isEmpty())
				{
					val namespace = DotNetPsiSearcher.getInstance(myProject).findNamespace(newQualifiedName.toString(), globalSearchScope);
					if(namespace != null)
					{
						return new PsiElement[] {namespace};
					}
				}
				return PsiElement.EMPTY_ARRAY;
			case NONE:
				PsiElement[] onlyElements = findChildren(name, globalSearchScope, ChildrenFilter.ONLY_ELEMENTS);
				PsiElement[] onlyNamespaces = findChildren(name, globalSearchScope, ChildrenFilter.ONLY_NAMESPACES);
				return ArrayUtil.mergeArrays(onlyElements, onlyNamespaces);
		}

		return PsiElement.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	protected Collection<? extends PsiElement> getOnlyElements(@NotNull GlobalSearchScope globalSearchScope)
	{
		return getHardIndexExtension().get(myIndexKey, myProject, globalSearchScope);
	}

	@NotNull
	@Override
	protected Collection<? extends PsiElement> getOnlyNamespaces(@NotNull GlobalSearchScope globalSearchScope)
	{
		val thisQualifiedName = QualifiedName.fromDottedString(myQName);

		val namespaceChildren = new ArrayListSet<String>();
		Collection<? extends PsiElement> otherNamespaces = getSoftIndexExtension().get(myIndexKey, myProject, globalSearchScope);
		for(PsiElement psiElement : otherNamespaces)
		{
			ProgressManager.checkCanceled();

			if(psiElement instanceof DotNetQualifiedElement)
			{
				String presentableQName = ((DotNetQualifiedElement) psiElement).getPresentableQName();
				if(presentableQName == null)
				{
					continue;
				}

				QualifiedName qualifiedName = QualifiedName.fromDottedString(presentableQName);
				if(qualifiedName.matchesPrefix(thisQualifiedName))
				{
					List<String> childList = qualifiedName.getComponents().subList(0, thisQualifiedName.getComponentCount() + 1);

					QualifiedName child = QualifiedName.fromComponents(childList);

					namespaceChildren.add(child.toString());
				}
			}
		}

		List<PsiElement> newList = new ArrayList<PsiElement>(namespaceChildren.size());
		for(String namespaceChild : namespaceChildren)
		{
			ProgressManager.checkCanceled();

			val namespace = DotNetPsiSearcher.getInstance(myProject).findNamespace(namespaceChild, globalSearchScope);
			if(namespace != null)
			{
				newList.add(namespace);
			}
		}
		return newList;
	}
}
