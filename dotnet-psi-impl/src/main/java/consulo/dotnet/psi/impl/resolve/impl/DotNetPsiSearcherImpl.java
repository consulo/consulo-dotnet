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
import consulo.component.extension.ExtensionPointName;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.impl.DotNetNamespaceCacheManager;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.psi.resolve.DotNetPsiSearcher;
import consulo.project.Project;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * @author VISTALL
 * @since 13.07.14
 */
@Singleton
public class DotNetPsiSearcherImpl extends DotNetPsiSearcher
{
	private static final ExtensionPointName<DotNetPsiSearcher> EP_NAME = ExtensionPointName.create("consulo.dotnet.psiSearcher");

	private List<DotNetPsiSearcher> mySearchers;
	private DotNetNamespaceCacheManager myCacheManager;

	@Inject
	public DotNetPsiSearcherImpl(Project project, DotNetNamespaceCacheManager cacheManager)
	{
		mySearchers = EP_NAME.getExtensionList(project);
		myCacheManager = cacheManager;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetNamespaceAsElement findNamespace(@Nonnull String qName, @Nonnull SearchScope scope)
	{
		return myCacheManager.computeNamespace(mySearchers, qName, scope);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public Collection<? extends DotNetTypeDeclaration> findTypesImpl(@Nonnull String vmQName, @Nonnull SearchScope scope)
	{
		return myCacheManager.computeTypes(mySearchers, vmQName, scope);
	}
}
