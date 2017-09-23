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

import gnu.trove.THashSet;

import java.util.Collection;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.Processors;
import com.intellij.util.indexing.IdFilter;
import consulo.dotnet.psi.DotNetTypeDeclaration;

/**
 * @author VISTALL
 * @since 08.07.2015
 */
public abstract class DotNetShortNameSearcher
{
	@NotNull
	public static DotNetShortNameSearcher getInstance(@NotNull Project project)
	{
		return ServiceManager.getService(project, DotNetShortNameSearcher.class);
	}

	protected final Project myProject;

	public DotNetShortNameSearcher(Project project)
	{
		myProject = project;
	}

	@NotNull
	public Collection<String> getTypeNames(@NotNull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		Set<String> types = new THashSet<>();
		collectTypeNames(Processors.cancelableCollectProcessor(types), scope, filter);
		return types;
	}


	public abstract void collectTypeNames(@NotNull Processor<String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter);

	public abstract void collectTypes(@NotNull String key, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter, @NotNull Processor<DotNetTypeDeclaration> processor);

	@NotNull
	public Collection<DotNetTypeDeclaration> getTypes(@NotNull String key, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		Set<DotNetTypeDeclaration> types = new THashSet<>();
		collectTypes(key, scope, filter, Processors.cancelableCollectProcessor(types));
		return types;
	}
}
