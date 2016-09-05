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

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.impl.IndexBasedDotNetPsiSearcher;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.ArrayUtil;
import com.intellij.util.NotNullFunction;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public abstract class IndexBasedDotNetNamespaceAsElement extends BaseDotNetNamespaceAsElement
{
	protected String myIndexKey;

	@NotNull
	private final IndexBasedDotNetPsiSearcher mySearcher;

	protected IndexBasedDotNetNamespaceAsElement(@NotNull Project project, @NotNull Language language, @NotNull String indexKey, @NotNull String qName, @NotNull IndexBasedDotNetPsiSearcher searcher)
	{
		super(project, language, qName);
		myIndexKey = indexKey;
		mySearcher = searcher;
	}

	@NotNull
	@Override
	@RequiredReadAction
	public PsiElement[] findChildren(@NotNull final String name,
			@NotNull GlobalSearchScope globalSearchScope,
			@NotNull NotNullFunction<PsiElement, PsiElement> transformer,
			@NotNull ChildrenFilter filter)
	{
		switch(filter)
		{
			case ONLY_ELEMENTS:
				Collection<DotNetQualifiedElement> elements = StubIndex.getElements(mySearcher.getElementByQNameIndexKey(), myIndexKey + "." + name, myProject, globalSearchScope,
						DotNetQualifiedElement.class);

				return toArray(elements, transformer);
			case ONLY_NAMESPACES:
				QualifiedName newQualifiedName = QualifiedName.fromDottedString(myQName).append(name);

				DotNetNamespaceAsElement namespace = DotNetPsiSearcher.getInstance(myProject).findNamespace(newQualifiedName.toString(), globalSearchScope);
				if(namespace != null)
				{
					return new PsiElement[]{transformer.fun(namespace)};
				}
				return PsiElement.EMPTY_ARRAY;
			case NONE:
				PsiElement[] onlyElements = findChildren(name, globalSearchScope, transformer, ChildrenFilter.ONLY_ELEMENTS);
				PsiElement[] onlyNamespaces = findChildren(name, globalSearchScope, transformer, ChildrenFilter.ONLY_NAMESPACES);
				return ArrayUtil.mergeArrays(onlyElements, onlyNamespaces);
		}

		return PsiElement.EMPTY_ARRAY;
	}

	@RequiredReadAction
	@NotNull
	@Override
	protected Collection<? extends PsiElement> getOnlyElements(@NotNull final GlobalSearchScope globalSearchScope)
	{
		return DotNetNamespaceCacheManager.getInstance(myProject).computeElements(mySearcher, this, myIndexKey, myQName, globalSearchScope, DotNetNamespaceCacheManager.ONLY_ELEMENTS);
	}

	@RequiredReadAction
	@NotNull
	@Override
	protected Collection<? extends PsiElement> getOnlyNamespaces(@NotNull final GlobalSearchScope globalSearchScope)
	{
		return DotNetNamespaceCacheManager.getInstance(myProject).computeElements(mySearcher, this, myIndexKey, myQName, globalSearchScope, DotNetNamespaceCacheManager.ONLY_NAMESPACES);
	}
}
