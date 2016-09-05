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

import java.util.Collection;

import consulo.lombok.annotations.ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;

/**
 * @author VISTALL
 * @since 08.07.2015
 */
@ProjectService
public abstract class DotNetShortNameSearcher
{
	protected Project myProject;

	public DotNetShortNameSearcher(Project project)
	{
		myProject = project;
	}

	@NotNull
	public Collection<String> getTypeNames(@NotNull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		CommonProcessors.CollectProcessor<String> collectProcessor = new CommonProcessors.CollectProcessor<String>();
		collectTypeNames(collectProcessor, scope, filter);
		return collectProcessor.getResults();
	}


	public abstract void collectTypeNames(@NotNull Processor<String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter);

	public abstract void collectTypes(@NotNull String key, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter, @NotNull Processor<DotNetTypeDeclaration> processor);

	@NotNull
	public Collection<DotNetTypeDeclaration> getTypes(@NotNull String key, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		CommonProcessors.CollectProcessor<DotNetTypeDeclaration> collectProcessor = new CommonProcessors.CollectProcessor<DotNetTypeDeclaration>();
		collectTypes(key, scope, filter, collectProcessor);
		return collectProcessor.getResults();
	}
}
