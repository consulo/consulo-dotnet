/*
 * Copyright 2013-2015 must-be.org
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
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetShortNameSearcher;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;

/**
 * @author VISTALL
 * @since 08.07.2015
 */
@jakarta.inject.Singleton
public class DotNetShortNameSearcherImpl extends DotNetShortNameSearcher
{
	private static final ExtensionPointName<DotNetShortNameSearcher> EP_NAME = ExtensionPointName.create("consulo.dotnet.shortNameSearcher");

	@jakarta.inject.Inject
	public DotNetShortNameSearcherImpl(Project project)
	{
		super(project);
	}

	@Override
	public void collectTypeNames(@Nonnull Processor<String> processor, @Nonnull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		for(DotNetShortNameSearcher nameSearcher : EP_NAME.getExtensionList(myProject))
		{
			nameSearcher.collectTypeNames(processor, scope, filter);
		}
	}

	@Override
	public void collectTypes(@Nonnull String key,
			@Nonnull GlobalSearchScope scope,
			@Nullable IdFilter filter,
			@Nonnull Processor<DotNetTypeDeclaration> processor)
	{
		for(DotNetShortNameSearcher nameSearcher : EP_NAME.getExtensionList(myProject))
		{
			nameSearcher.collectTypes(key, scope, filter, processor);
		}
	}
}
