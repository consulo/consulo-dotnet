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

import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetShortNameSearcher;
import consulo.ide.impl.idea.ide.util.AbstractTreeClassChooserDialog;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.IdFilter;
import consulo.project.Project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
public class DotNetTypeChooser extends AbstractTreeClassChooserDialog<DotNetTypeDeclaration>
{
	public DotNetTypeChooser(String title, Project project, GlobalSearchScope searchScope, String baseTypeVmQName)
	{
		super(title, project, searchScope, DotNetTypeDeclaration.class, createFilter(baseTypeVmQName), null);
	}

	@Nullable
	private static Predicate<DotNetTypeDeclaration> createFilter(final String baseTypeVmQName)
	{
		if(baseTypeVmQName == null)
		{
			return null;
		}
		return element -> element.isInheritor(baseTypeVmQName, true);
	}

	@Override
	protected DotNetTypeDeclaration getSelectedFromTreeUserObject(DefaultMutableTreeNode node)
	{
		return null;
	}

	@Nonnull
	@Override
	protected List<DotNetTypeDeclaration> getClassesByName(String name, boolean checkBoxState, String pattern, GlobalSearchScope searchScope)
	{
		Collection<DotNetTypeDeclaration> types = DotNetShortNameSearcher.getInstance(getProject()).getTypes(name, GlobalSearchScope.allScope(getProject()), IdFilter.getProjectIdFilter(getProject(), true));
		return new ArrayList<>(types);
	}
}
