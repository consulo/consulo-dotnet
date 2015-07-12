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

package org.mustbe.consulo.msil.lang.psi.impl.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetShortNameSearcher;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilTypeByNameIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;

/**
 * @author VISTALL
 * @since 08.07.2015
 */
public class MsilShortNameSearcher extends DotNetShortNameSearcher
{
	public MsilShortNameSearcher(Project project)
	{
		super(project);
	}

	@Override
	public void collectTypeNames(@NotNull Processor<String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		StubIndex.getInstance().processAllKeys(MsilIndexKeys.TYPE_BY_NAME_INDEX, processor, scope, filter);
		MsilTypeByNameIndex.getInstance().processAllKeys(myProject, processor);
	}

	@Override
	public void collectTypes(@NotNull String name, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter, @NotNull Processor<DotNetTypeDeclaration>
			processor)
	{
		StubIndex.getInstance().processElements(MsilIndexKeys.TYPE_BY_NAME_INDEX, name, myProject, scope, MsilClassEntry.class, processor);
	}
}