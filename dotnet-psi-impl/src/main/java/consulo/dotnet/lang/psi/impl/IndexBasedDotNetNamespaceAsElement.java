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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.dotnet.resolve.impl.IndexBasedDotNetPsiSearcher;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public abstract class IndexBasedDotNetNamespaceAsElement extends BaseDotNetNamespaceAsElement
{
	protected String myIndexKey;

	@Nonnull
	private final IndexBasedDotNetPsiSearcher mySearcher;

	protected IndexBasedDotNetNamespaceAsElement(@Nonnull Project project, @Nonnull Language language, @Nonnull String indexKey, @Nonnull String qName, @Nonnull IndexBasedDotNetPsiSearcher searcher)
	{
		super(project, language, qName);
		myIndexKey = indexKey;
		mySearcher = searcher;
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public List<PsiElement> findChildren(@Nonnull final String name,
			@Nonnull GlobalSearchScope globalSearchScope,
			@Nonnull NotNullFunction<PsiElement, PsiElement> transformer,
			@Nonnull ChildrenFilter filter)
	{
		switch(filter)
		{
			case ONLY_ELEMENTS:
				Collection<DotNetQualifiedElement> elements = StubIndex.getElements(mySearcher.getElementByQNameIndexKey(), myIndexKey + "." + name, myProject, globalSearchScope,
						DotNetQualifiedElement.class);

				return transformData(elements, elements.size(), transformer);
			case ONLY_NAMESPACES:
				QualifiedName newQualifiedName = QualifiedName.fromDottedString(myQName).append(name);

				DotNetNamespaceAsElement namespace = DotNetPsiSearcher.getInstance(myProject).findNamespace(newQualifiedName.toString(), globalSearchScope);
				if(namespace != null)
				{
					return Collections.singletonList(transformer.fun(namespace));
				}
				return Collections.emptyList();
			case NONE:
				List<PsiElement> onlyElements = findChildren(name, globalSearchScope, transformer, ChildrenFilter.ONLY_ELEMENTS);
				List<PsiElement> onlyNamespaces = findChildren(name, globalSearchScope, transformer, ChildrenFilter.ONLY_NAMESPACES);
				return ContainerUtil.concat(onlyElements, onlyNamespaces);
		}

		return Collections.emptyList();
	}

	@RequiredReadAction
	@Nonnull
	@Override
	protected Set<? extends PsiElement> getOnlyElements(@Nonnull final GlobalSearchScope globalSearchScope)
	{
		return DotNetNamespaceCacheManager.getInstance(myProject).computeElements(mySearcher, this, myIndexKey, myQName, globalSearchScope, DotNetNamespaceCacheManager.ONLY_ELEMENTS);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	protected Set<? extends PsiElement> getOnlyNamespaces(@Nonnull final GlobalSearchScope globalSearchScope)
	{
		return DotNetNamespaceCacheManager.getInstance(myProject).computeElements(mySearcher, this, myIndexKey, myQName, globalSearchScope, DotNetNamespaceCacheManager.ONLY_NAMESPACES);
	}
}
