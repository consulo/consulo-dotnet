/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.csharp.ide.navigation;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.TypeIndex;
import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public class CSharpTypeNameContributor implements GotoClassContributor
{
	@Nullable
	@Override
	public String getQualifiedName(NavigationItem navigationItem)
	{
		if(navigationItem instanceof CSharpTypeDeclaration)
		{
			return ((CSharpTypeDeclaration) navigationItem).getQName();
		}
		return null;
	}

	@Nullable
	@Override
	public String getQualifiedNameSeparator()
	{
		return ".";
	}

	@NotNull
	@Override
	public String[] getNames(Project project, boolean includeNonProjectItems)
	{
		Collection<String> allKeys = TypeIndex.getInstance().getAllKeys(project);
		return allKeys.toArray(new String[allKeys.size()]);
	}

	@NotNull
	@Override
	public NavigationItem[] getItemsByName(String name, final String pattern, Project project, boolean includeNonProjectItems)
	{
		Collection<CSharpTypeDeclaration> cSharpTypeDeclarations = TypeIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));
		return cSharpTypeDeclarations.toArray(new NavigationItem[cSharpTypeDeclarations.size()]);
	}
}
