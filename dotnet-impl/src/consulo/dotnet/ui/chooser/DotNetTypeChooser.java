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

package consulo.dotnet.ui.chooser;

import java.util.Collection;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetShortNameSearcher;
import com.intellij.ide.util.AbstractTreeClassChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IdFilter;

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
	private static Filter<DotNetTypeDeclaration> createFilter(final String baseTypeVmQName)
	{
		if(baseTypeVmQName == null)
		{
			return null;
		}
		return new Filter<DotNetTypeDeclaration>()
		{
			@Override
			@RequiredReadAction
			public boolean isAccepted(DotNetTypeDeclaration element)
			{
				return element.isInheritor(baseTypeVmQName, true);
			}
		};
	}

	@Override
	protected DotNetTypeDeclaration getSelectedFromTreeUserObject(DefaultMutableTreeNode node)
	{
		return null;
	}

	@NotNull
	@Override
	protected List<DotNetTypeDeclaration> getClassesByName(String name, boolean checkBoxState, String pattern, GlobalSearchScope searchScope)
	{
		Collection<DotNetTypeDeclaration> types = DotNetShortNameSearcher.getInstance(getProject()).getTypes(name, GlobalSearchScope.allScope(getProject()), IdFilter.getProjectIdFilter(getProject(), true));
		return ContainerUtil.newArrayList(types);
	}
}
