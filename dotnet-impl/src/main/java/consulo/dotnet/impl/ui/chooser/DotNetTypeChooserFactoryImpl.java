/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.impl.ui.chooser;

import consulo.annotation.component.ServiceImpl;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetShortNameSearcher;
import consulo.dotnet.psi.ui.chooser.DotNetTypeChooserFactory;
import consulo.language.editor.ui.TreeChooser;
import consulo.language.editor.ui.TreeClassChooserFactory;
import consulo.language.psi.stub.IdFilter;
import consulo.localize.LocalizeValue;
import consulo.project.content.scope.ProjectAwareSearchScope;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
@Singleton
@ServiceImpl
public class DotNetTypeChooserFactoryImpl extends DotNetTypeChooserFactory
{
	private static final TreeClassChooserFactory.ClassProvider<DotNetTypeDeclaration> ourClassProvider = (project, name, searchInLibraries, pattern, searchScope) ->
	{
		return DotNetShortNameSearcher.getInstance(project).getTypes(name, searchScope, IdFilter.getProjectIdFilter(project, true));
	};

	private final TreeClassChooserFactory myTreeClassChooserFactory;

	@Inject
	public DotNetTypeChooserFactoryImpl(TreeClassChooserFactory treeClassChooserFactory)
	{
		myTreeClassChooserFactory = treeClassChooserFactory;
	}

	@Nonnull
	@Override
	public TreeChooser<DotNetTypeDeclaration> createChooser(@Nonnull ProjectAwareSearchScope scope)
	{
		TreeClassChooserFactory.Builder<DotNetTypeDeclaration> builder = myTreeClassChooserFactory.newChooser(DotNetTypeDeclaration.class);
		builder.withTitle(LocalizeValue.localizeTODO("Choose Type"));
		builder.withSearchScope(scope);
		builder.withClassProvider(ourClassProvider);
		return builder.build();
	}

	@Nonnull
	@Override
	public TreeChooser<DotNetTypeDeclaration> createInheriableChooser(@Nonnull String baseVmQName, @Nonnull ProjectAwareSearchScope scope)
	{
		TreeClassChooserFactory.Builder<DotNetTypeDeclaration> builder = myTreeClassChooserFactory.newChooser(DotNetTypeDeclaration.class);
		builder.withTitle(LocalizeValue.localizeTODO("Choose Type"));
		builder.withSearchScope(scope);
		builder.withClassProvider(ourClassProvider);
		builder.withClassFilter(element -> element.isInheritor(baseVmQName, true));
		return builder.build();
	}
}
