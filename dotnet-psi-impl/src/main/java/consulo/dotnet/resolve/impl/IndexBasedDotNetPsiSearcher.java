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

package consulo.dotnet.resolve.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.lang.psi.impl.stub.DotNetNamespaceStubUtil;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.dotnet.resolve.GlobalSearchScopeFilter;

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

	@Nonnull
	protected abstract DotNetNamespaceAsElement createNamespace(@Nonnull String indexKey, @Nonnull String qName);

	@Nonnull
	public abstract StubIndexKey<String, DotNetQualifiedElement> getElementByQNameIndexKey();

	@Nonnull
	public abstract StubIndexKey<String, DotNetQualifiedElement> getNamespaceIndexKey();

	@RequiredReadAction
	@Override
	@Nullable
	public final DotNetNamespaceAsElement findNamespace(@Nonnull String qName, @Nonnull GlobalSearchScope scope)
	{
		return findNamespaceImpl(DotNetNamespaceStubUtil.getIndexableNamespace(qName), qName, scope);
	}

	@Nullable
	public DotNetNamespaceAsElement findNamespaceImpl(@Nonnull String indexKey, @Nonnull String qName, @Nonnull GlobalSearchScope scope)
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

	private static boolean isFoundAnyOneElement(@Nonnull Project project,
			@Nonnull final String indexKey,
			@Nonnull StubIndexKey<String, DotNetQualifiedElement> keyForIndex,
			@Nonnull GlobalSearchScope scope)
	{
		return !StubIndex.getInstance().processAllKeys(keyForIndex, s ->
		{
			ProgressManager.checkCanceled();
			return !indexKey.equals(s);
		}, scope, new GlobalSearchScopeFilter(scope));
	}

	@Nonnull
	public Project getProject()
	{
		return myProject;
	}
}
