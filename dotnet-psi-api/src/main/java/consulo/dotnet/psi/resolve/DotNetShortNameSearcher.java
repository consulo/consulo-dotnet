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

package consulo.dotnet.psi.resolve;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.application.util.function.Processors;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.psi.stub.IdFilter;
import consulo.project.Project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author VISTALL
 * @since 08.07.2015
 */
@ServiceAPI(ComponentScope.PROJECT)
public abstract class DotNetShortNameSearcher implements DotNetShortNameSearcherExtension
{
	@Nonnull
	public static DotNetShortNameSearcher getInstance(@Nonnull Project project)
	{
		return project.getInstance(DotNetShortNameSearcher.class);
	}

	protected final Project myProject;

	public DotNetShortNameSearcher(Project project)
	{
		myProject = project;
	}

	@Nonnull
	public Collection<String> getTypeNames(@Nonnull SearchScope scope, @Nullable IdFilter filter)
	{
		Set<String> types = new HashSet<>();
		collectTypeNames(Processors.cancelableCollectProcessor(types), scope, filter);
		return types;
	}


	public abstract void collectTypeNames(@Nonnull Predicate<String> processor, @Nonnull SearchScope scope, @Nullable IdFilter filter);

	public abstract void collectTypes(@Nonnull String key, @Nonnull SearchScope scope, @Nullable IdFilter filter, @Nonnull Predicate<DotNetTypeDeclaration> processor);

	@Nonnull
	public Collection<DotNetTypeDeclaration> getTypes(@Nonnull String key, @Nonnull SearchScope scope, @Nullable IdFilter filter)
	{
		Set<DotNetTypeDeclaration> types = new HashSet<>();
		collectTypes(key, scope, filter, Processors.cancelableCollectProcessor(types));
		return types;
	}
}
