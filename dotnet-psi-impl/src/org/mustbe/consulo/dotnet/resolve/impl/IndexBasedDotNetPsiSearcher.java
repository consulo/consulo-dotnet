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
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceUtil;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.CommonProcessors;

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
	public abstract StringStubIndexExtension<? extends PsiElement> getHardIndexExtension();

	@NotNull
	public abstract StringStubIndexExtension<? extends PsiElement> getSoftIndexExtension();

	@Nullable
	@Override
	public DotNetNamespaceAsElement findNamespaceImpl(@NotNull String indexKey, @NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		if(DotNetNamespaceUtil.ROOT_FOR_INDEXING.equals(indexKey))
		{
			return createNamespace(indexKey, qName);
		}

		if(isFoundAnyOneElement(myProject, indexKey, getHardIndexExtension(), scope))
		{
			return createNamespace(indexKey, qName);
		}

		if(isFoundAnyOneElement(myProject, indexKey, getSoftIndexExtension(), scope))
		{
			return createNamespace(indexKey, qName);
		}
		return null;
	}

	private static boolean isFoundAnyOneElement(@NotNull Project project,
			@NotNull String indexKey,
			@NotNull StringStubIndexExtension<? extends PsiElement> indexExtension,
			@NotNull GlobalSearchScope scope)
	{
		CommonProcessors.FindProcessor<PsiElement> processor = new CommonProcessors.FindFirstProcessor<PsiElement>();

		StubIndexKey key = indexExtension.getKey();

		StubIndex.getInstance().processElements(key, indexKey, project, scope, PsiElement.class, processor);
		return processor.isFound();
	}
}
