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

package consulo.dotnet.resolve;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.Processors;
import com.intellij.util.indexing.IdFilter;
import consulo.dotnet.psi.DotNetTypeDeclaration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author VISTALL
 * @since 08.07.2015
 */
public abstract class DotNetShortNameSearcher
{
	@Nonnull
	public static DotNetShortNameSearcher getInstance(@Nonnull Project project)
	{
		return ServiceManager.getService(project, DotNetShortNameSearcher.class);
	}

	protected final Project myProject;

	public DotNetShortNameSearcher(Project project)
	{
		myProject = project;
	}

	@Nonnull
	public Collection<String> getTypeNames(@Nonnull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		Set<String> types = new HashSet<>();
		collectTypeNames(Processors.cancelableCollectProcessor(types), scope, filter);
		return types;
	}


	public abstract void collectTypeNames(@Nonnull Processor<String> processor, @Nonnull GlobalSearchScope scope, @Nullable IdFilter filter);

	public abstract void collectTypes(@Nonnull String key, @Nonnull GlobalSearchScope scope, @Nullable IdFilter filter, @Nonnull Processor<DotNetTypeDeclaration> processor);

	@Nonnull
	public Collection<DotNetTypeDeclaration> getTypes(@Nonnull String key, @Nonnull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		Set<DotNetTypeDeclaration> types = new HashSet<>();
		collectTypes(key, scope, filter, Processors.cancelableCollectProcessor(types));
		return types;
	}
}
