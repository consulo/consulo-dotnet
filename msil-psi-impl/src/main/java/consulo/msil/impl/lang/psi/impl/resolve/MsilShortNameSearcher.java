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

package consulo.msil.impl.lang.psi.impl.resolve;

import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetShortNameSearcher;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.IdFilter;
import consulo.language.psi.stub.StubIndex;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.impl.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import consulo.msil.impl.lang.psi.impl.elementType.stub.index.MsilTypeByNameIndex;
import consulo.project.Project;
import jakarta.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

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
	public void collectTypeNames(@Nonnull Predicate<String> processor, @Nonnull SearchScope scope, @Nullable IdFilter filter)
	{
		StubIndex.getInstance().processAllKeys(MsilIndexKeys.TYPE_BY_NAME_INDEX, processor::test, (GlobalSearchScope) scope, filter);
		MsilTypeByNameIndex.getInstance().processAllKeys(myProject, processor::test);
	}

	@Override
	public void collectTypes(@Nonnull String name, @Nonnull SearchScope scope, @Nullable IdFilter filter, @Nonnull Predicate<DotNetTypeDeclaration> processor)
	{
		StubIndex.getInstance().processElements(MsilIndexKeys.TYPE_BY_NAME_INDEX, name, myProject, (GlobalSearchScope) scope, MsilClassEntry.class, processor::test);
	}
}
