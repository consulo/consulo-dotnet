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

package consulo.msil.lang.psi.impl.resolve;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetShortNameSearcher;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import consulo.msil.lang.psi.impl.elementType.stub.index.MsilTypeByNameIndex;

/**
 * @author VISTALL
 * @since 08.07.2015
 */
public class MsilShortNameSearcher extends DotNetShortNameSearcher
{
	@Inject
	public MsilShortNameSearcher(Project project)
	{
		super(project);
	}

	@Override
	public void collectTypeNames(@Nonnull Processor<String> processor, @Nonnull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		StubIndex.getInstance().processAllKeys(MsilIndexKeys.TYPE_BY_NAME_INDEX, processor, scope, filter);
		MsilTypeByNameIndex.getInstance().processAllKeys(myProject, processor);
	}

	@Override
	public void collectTypes(@Nonnull String name, @Nonnull GlobalSearchScope scope, @Nullable IdFilter filter, @Nonnull Processor<DotNetTypeDeclaration> processor)
	{
		StubIndex.getInstance().processElements(MsilIndexKeys.TYPE_BY_NAME_INDEX, name, myProject, scope, MsilClassEntry.class, processor);
	}
}
