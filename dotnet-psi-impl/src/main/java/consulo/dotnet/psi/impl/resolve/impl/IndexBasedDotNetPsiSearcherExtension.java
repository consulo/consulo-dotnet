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

package consulo.dotnet.psi.impl.resolve.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.util.function.CommonProcessors;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.psi.impl.stub.DotNetNamespaceStubUtil;
import consulo.dotnet.psi.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.psi.resolve.DotNetPsiSearcherExtension;
import consulo.language.psi.PsiElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.StubIndex;
import consulo.language.psi.stub.StubIndexKey;
import consulo.project.Project;

import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 19.10.14
 */
public abstract class IndexBasedDotNetPsiSearcherExtension implements DotNetPsiSearcherExtension
{
	protected Project myProject;

	public IndexBasedDotNetPsiSearcherExtension(Project project)
	{
		myProject = project;
	}

	protected abstract DotNetNamespaceAsElement createNamespace(String indexKey, String qName);

	public abstract StubIndexKey<String, DotNetQualifiedElement> getElementByQNameIndexKey();

	public abstract StubIndexKey<String, DotNetQualifiedElement> getNamespaceIndexKey();

	@RequiredReadAction
	@Override
	@Nullable
	public final DotNetNamespaceAsElement findNamespace(String qName, SearchScope scope)
	{
		return findNamespaceImpl(DotNetNamespaceStubUtil.getIndexableNamespace(qName), qName, scope);
	}

	@Nullable
	public DotNetNamespaceAsElement findNamespaceImpl(String indexKey, String qName, SearchScope scope)
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

	private static boolean isFoundAnyOneElement(Project project,
												final String indexKey,
												StubIndexKey<String, DotNetQualifiedElement> keyForIndex,
												SearchScope scope)
	{
		CommonProcessors.FindFirstProcessor<PsiElement> processor = new CommonProcessors.FindFirstProcessor<>();
		StubIndex.getInstance().processElements(keyForIndex, indexKey, project, (GlobalSearchScope) scope, DotNetQualifiedElement.class, processor);
		return processor.getFoundValue() != null;
	}

	public Project getProject()
	{
		return myProject;
	}
}
