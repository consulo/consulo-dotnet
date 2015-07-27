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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.lang.psi.impl.stub.DotNetNamespaceStubUtil;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValueProvider;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;

/**
 * @author VISTALL
 * @since 19.10.14
 */
public abstract class IndexBasedDotNetPsiSearcher extends DotNetPsiSearcher
{
	protected Project myProject;

	public IndexBasedDotNetPsiSearcher(Project project)
	{
		myProject = project;
	}

	@NotNull
	protected abstract DotNetNamespaceAsElement createNamespace(@NotNull String indexKey, @NotNull String qName);

	@NotNull
	public abstract StubIndexKey<String, DotNetQualifiedElement> getElementByQNameIndexKey();

	@NotNull
	public abstract StubIndexKey<String, DotNetQualifiedElement> getNamespaceIndexKey();

	@RequiredReadAction
	@Override
	@Nullable
	public final DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		if(useCache())
		{
			return CachedValuesManager.getManager(myProject).createParameterizedCachedValue(new
																									ParameterizedCachedValueProvider<DotNetNamespaceAsElement, Pair<String, GlobalSearchScope>>()
			{
				@Nullable
				@Override
				public CachedValueProvider.Result<DotNetNamespaceAsElement> compute(Pair<String, GlobalSearchScope> param)
				{
					return CachedValueProvider.Result.create(findNamespaceImpl(DotNetNamespaceStubUtil.getIndexableNamespace(param.getFirst()),
							param.getFirst(), param.getSecond()), PsiModificationTracker.OUT_OF_CODE_BLOCK_MODIFICATION_COUNT);
				}

			}, false).getValue(Pair.create(qName, scope));
		}
		return findNamespaceImpl(DotNetNamespaceStubUtil.getIndexableNamespace(qName), qName, scope);
	}

	public boolean useCache()
	{
		return true;
	}

	@Nullable
	public DotNetNamespaceAsElement findNamespaceImpl(@NotNull String indexKey, @NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		if(DotNetNamespaceStubUtil.ROOT_FOR_INDEXING.equals(indexKey))
		{
			return createNamespace(indexKey, qName);
		}

		if(isFoundAnyOneElement(myProject, indexKey, getNamespaceIndexKey(), scope))
		{
			return createNamespace(indexKey, qName);
		}
		return null;
	}

	private static boolean isFoundAnyOneElement(@NotNull Project project,
			@NotNull final String indexKey,
			@NotNull StubIndexKey<String, DotNetQualifiedElement> keyForIndex,
			@NotNull GlobalSearchScope scope)
	{
		return !StubIndex.getInstance().processAllKeys(keyForIndex, new Processor<String>()
		{
			@Override
			public boolean process(String s)
			{
				return !indexKey.equals(s);
			}
		}, scope, IdFilter.getProjectIdFilter(project, false));
	}
}
