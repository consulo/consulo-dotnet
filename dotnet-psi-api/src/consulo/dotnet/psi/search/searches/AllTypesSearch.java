/*
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

import consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ExtensibleQueryFactory;
import com.intellij.util.Query;

/**
 * @author max
 *         <p/>
 *         Copied from Java plugin by Jetbrains (com.intellij.psi.search.searches.ClassInheritorsSearch)
 */
public class AllTypesSearch extends ExtensibleQueryFactory<DotNetTypeDeclaration, AllTypesSearch.SearchParameters>
{
	public static final AllTypesSearch INSTANCE = new AllTypesSearch();

	public static class SearchParameters
	{
		private final SearchScope myScope;
		private final Project myProject;
		private final Condition<String> myShortNameCondition;

		public SearchParameters(final SearchScope scope, final Project project)
		{
			this(scope, project, Conditions.<String>alwaysTrue());
		}

		public SearchParameters(final SearchScope scope, final Project project, final Condition<String> shortNameCondition)
		{
			myScope = scope;
			myProject = project;
			myShortNameCondition = shortNameCondition;
		}

		public SearchScope getScope()
		{
			return myScope;
		}

		public Project getProject()
		{
			return myProject;
		}

		public boolean nameMatches(String name)
		{
			return myShortNameCondition.value(name);
		}
	}

	private AllTypesSearch()
	{
		super("org.mustbe.consulo.dotnet.core");
	}

	public static Query<DotNetTypeDeclaration> search(SearchScope scope, Project project)
	{
		return INSTANCE.createQuery(new SearchParameters(scope, project));
	}

	public static Query<DotNetTypeDeclaration> search(SearchScope scope, Project project, Condition<String> shortNameCondition)
	{
		return INSTANCE.createQuery(new SearchParameters(scope, project, shortNameCondition));
	}
}