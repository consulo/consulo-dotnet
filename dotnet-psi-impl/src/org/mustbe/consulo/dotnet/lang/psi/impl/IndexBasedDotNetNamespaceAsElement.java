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

import gnu.trove.THashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.impl.IndexBasedDotNetPsiSearcher;
import com.intellij.lang.Language;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.ArrayUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.NotNullFunction;
import com.intellij.util.Processor;
import com.intellij.util.containers.ArrayListSet;
import lombok.val;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public abstract class IndexBasedDotNetNamespaceAsElement extends BaseDotNetNamespaceAsElement
{
	protected String myIndexKey;

	@NotNull
	private final IndexBasedDotNetPsiSearcher mySearcher;

	protected IndexBasedDotNetNamespaceAsElement(@NotNull Project project,
			@NotNull Language language,
			@NotNull String indexKey,
			@NotNull String qName,
			@NotNull IndexBasedDotNetPsiSearcher searcher)
	{
		super(project, language, qName);
		myIndexKey = indexKey;
		mySearcher = searcher;
	}

	@NotNull
	@Override
	public PsiElement[] findChildren(@NotNull final String name,
			@NotNull GlobalSearchScope globalSearchScope,
			@NotNull NotNullFunction<PsiElement, PsiElement> transformer,
			@NotNull ChildrenFilter filter)
	{
		StubIndexKey<String, DotNetQualifiedElement> key;
		switch(filter)
		{
			case ONLY_ELEMENTS:

				key = mySearcher.getElementByQNameIndexKey();

				Collection<DotNetQualifiedElement> elements = StubIndex.getElements(key, myIndexKey + "." + name, myProject, globalSearchScope,
						DotNetQualifiedElement.class);

				return toArray(elements, transformer);
			case ONLY_NAMESPACES:
				val newQualifiedName = QualifiedName.fromDottedString(myQName).append(name);

				key = mySearcher.getNamespaceIndexKey();

				CommonProcessors.FindFirstProcessor<DotNetQualifiedElement> processor = new CommonProcessors
						.FindFirstProcessor<DotNetQualifiedElement>();
				StubIndex.getInstance().processElements(key, newQualifiedName.toString(), myProject, globalSearchScope,
						DotNetQualifiedElement.class, processor);

				PsiElement foundValue = processor.getFoundValue();

				if(foundValue != null)
				{
					val namespace = DotNetPsiSearcher.getInstance(myProject).findNamespace(newQualifiedName.toString(), globalSearchScope);
					if(namespace != null)
					{
						return new PsiElement[]{transformer.fun(namespace)};
					}
				}
				return PsiElement.EMPTY_ARRAY;
			case NONE:
				PsiElement[] onlyElements = findChildren(name, globalSearchScope, transformer, ChildrenFilter.ONLY_ELEMENTS);
				PsiElement[] onlyNamespaces = findChildren(name, globalSearchScope, transformer, ChildrenFilter.ONLY_NAMESPACES);
				return ArrayUtil.mergeArrays(onlyElements, onlyNamespaces);
		}

		return PsiElement.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	protected Collection<? extends PsiElement> getOnlyElements(@NotNull GlobalSearchScope globalSearchScope)
	{
		final Set<PsiElement> set = new THashSet<PsiElement>();

		StubIndex.getInstance().processElements(mySearcher.getNamespaceIndexKey(), myIndexKey, myProject, globalSearchScope,
				DotNetQualifiedElement.class, new Processor<DotNetQualifiedElement>()
		{
			@Override
			public boolean process(DotNetQualifiedElement element)
			{
				String presentableQName = element.getPresentableParentQName();
				if(Comparing.equal(myQName, presentableQName))
				{
					set.add(element);
				}
				return true;
			}
		});

		return set;
	}

	@NotNull
	@Override
	protected Collection<? extends PsiElement> getOnlyNamespaces(@NotNull GlobalSearchScope globalSearchScope)
	{
		val thisQualifiedName = QualifiedName.fromDottedString(myQName);

		val namespaceChildren = new ArrayListSet<String>();
		Collection<DotNetQualifiedElement> otherNamespaces = StubIndex.getElements(mySearcher.getNamespaceIndexKey(), myIndexKey, myProject,
				globalSearchScope, DotNetQualifiedElement.class);

		for(DotNetQualifiedElement psiElement : otherNamespaces)
		{
			ProgressManager.checkCanceled();

			String presentableQName = psiElement.getPresentableQName();
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
