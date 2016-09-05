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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
public class DotNetShortNameSearcherImpl extends DotNetShortNameSearcher
{
	private static final ExtensionPointName<DotNetShortNameSearcher> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core" + "" +
			".shortNameSearcher");

	public DotNetShortNameSearcherImpl(Project project)
	{
		super(project);
	}

	@Override
	public void collectTypeNames(@NotNull Processor<String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		for(DotNetShortNameSearcher nameSearcher : EP_NAME.getExtensions(myProject))
		{
			nameSearcher.collectTypeNames(processor, scope, filter);
		}
	}

	@Override
	public void collectTypes(@NotNull String key,
			@NotNull GlobalSearchScope scope,
			@Nullable IdFilter filter,
			@NotNull Processor<DotNetTypeDeclaration> processor)
	{
		for(DotNetShortNameSearcher nameSearcher : EP_NAME.getExtensions(myProject))
		{
			nameSearcher.collectTypes(key, scope, filter, processor);
		}
	}
}
