/*
 * Copyright 2000-2009 JetBrains s.r.o.
 * Copyright 2013-2014 must-be.org
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
package consulo.dotnet.psi.search.searches;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ExtensibleQueryFactory;
import com.intellij.util.Query;

/**
 * @author VISTALL
 *         <p/>
 *         Inspired by Jetbrains from java-impl (com.intellij.psi.search.searches.DirectClassInheritorsSearch) by max
 */
public class DirectTypeInheritorsSearch extends ExtensibleQueryFactory<DotNetTypeDeclaration, DirectTypeInheritorsSearch.SearchParameters>
{
	public static DirectTypeInheritorsSearch INSTANCE = new DirectTypeInheritorsSearch();

	public static class SearchParameters
	{
		private Project myProject;
		private final String myVmQName;
		private final SearchScope myScope;
		private final boolean myCheckInheritance;

		public SearchParameters(@NotNull Project project, @NotNull String vmQName, SearchScope scope, boolean checkInheritance)
		{
			myProject = project;
			myVmQName = vmQName;
			myScope = scope;
			myCheckInheritance = checkInheritance;
		}

		public SearchParameters(@NotNull Project project, String vmQName, final SearchScope scope)
		{
			this(project, vmQName, scope, true);
		}

		@NotNull
		public Project getProject()
		{
			return myProject;
		}

		@NotNull
		public String getVmQName()
		{
			return myVmQName;
		}

		public SearchScope getScope()
		{
			return myScope;
		}

		public boolean isCheckInheritance()
		{
			return myCheckInheritance;
		}
	}

	private DirectTypeInheritorsSearch()
	{
		super("org.mustbe.consulo.dotnet.core");
	}

	public static Query<DotNetTypeDeclaration> search(Project project, final String aClass)
	{
		return search(project, aClass, GlobalSearchScope.allScope(project));
	}

	public static Query<DotNetTypeDeclaration> search(Project project, String vmQName, SearchScope scope)
	{
		return INSTANCE.createUniqueResultsQuery(new SearchParameters(project, vmQName, scope));
	}

	public static Query<DotNetTypeDeclaration> search(Project project, String vmQName, SearchScope scope, boolean checkInheritance)
	{
		return INSTANCE.createUniqueResultsQuery(new SearchParameters(project, vmQName, scope, checkInheritance));
	}
}
