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

package consulo.dotnet.psi.impl.resolve.impl;

import consulo.annotation.component.ServiceImpl;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetShortNameSearcher;
import consulo.dotnet.psi.resolve.DotNetShortNameSearcherExtension;
import consulo.language.psi.stub.IdFilter;
import consulo.project.Project;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @author VISTALL
 * @since 08.07.2015
 */
@Singleton
@ServiceImpl
public class DotNetShortNameSearcherImpl extends DotNetShortNameSearcher
{
	@Inject
	public DotNetShortNameSearcherImpl(Project project)
	{
		super(project);
	}

	@Override
	public void collectTypeNames(@Nonnull Predicate<String> processor, @Nonnull SearchScope scope, @Nullable IdFilter filter)
	{
		for(DotNetShortNameSearcherExtension nameSearcher : myProject.getExtensionList(DotNetShortNameSearcherExtension.class))
		{
			nameSearcher.collectTypeNames(processor, scope, filter);
		}
	}

	@Override
	public void collectTypes(@Nonnull String key,
			@Nonnull SearchScope scope,
			@Nullable IdFilter filter,
			@Nonnull Predicate<DotNetTypeDeclaration> processor)
	{
		for(DotNetShortNameSearcherExtension nameSearcher : myProject.getExtensionList(DotNetShortNameSearcherExtension.class))
		{
			nameSearcher.collectTypes(key, scope, filter, processor);
		}
	}
}
