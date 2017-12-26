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

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.lang.psi.impl.DotNetNamespaceCacheManager;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.resolve.DotNetPsiSearcher;

/**
 * @author VISTALL
 * @since 13.07.14
 */
public class DotNetPsiSearcherImpl extends DotNetPsiSearcher
{
	private static final ExtensionPointName<DotNetPsiSearcher> EP_NAME = ExtensionPointName.create("consulo.dotnet.psiSearcher");

	private DotNetPsiSearcher[] mySearchers;
	private DotNetNamespaceCacheManager myCacheManager;

	public DotNetPsiSearcherImpl(Project project, DotNetNamespaceCacheManager cacheManager)
	{
		mySearchers = EP_NAME.getExtensions(project);
		myCacheManager = cacheManager;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		return myCacheManager.computeNamespace(mySearchers, qName, scope);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public Collection<? extends DotNetTypeDeclaration> findTypesImpl(@NotNull String vmQName, @NotNull GlobalSearchScope scope)
	{
		return myCacheManager.computeTypes(mySearchers, vmQName, scope);
	}
}
