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
package org.mustbe.consulo.dotnet.psi.search.searches;

import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ExtensibleQueryFactory;
import com.intellij.util.Query;

/**
 * @author max
 *         <p/>
 *         Copied from Java plugin by Jetbrains (com.intellij.psi.search.searches.ClassInheritorsSearch)
 */
public class DirectClassInheritorsSearch extends ExtensibleQueryFactory<DotNetTypeDeclaration, DirectClassInheritorsSearch.SearchParameters>
{
	public static DirectClassInheritorsSearch INSTANCE = new DirectClassInheritorsSearch();

	public static class SearchParameters
	{
		private final DotNetTypeDeclaration myClass;
		private final SearchScope myScope;
		private final boolean myCheckInheritance;

		public SearchParameters(DotNetTypeDeclaration aClass, SearchScope scope, boolean includeAnonymous, boolean checkInheritance)
		{
			myClass = aClass;
			myScope = scope;
			myCheckInheritance = checkInheritance;
		}

		public SearchParameters(final DotNetTypeDeclaration aClass, SearchScope scope, final boolean includeAnonymous)
		{
			this(aClass, scope, includeAnonymous, true);
		}

		public SearchParameters(final DotNetTypeDeclaration aClass, final SearchScope scope)
		{
			this(aClass, scope, true);
		}

		public DotNetTypeDeclaration getClassToProcess()
		{
			return myClass;
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

	private DirectClassInheritorsSearch()
	{
		super("org.mustbe.consulo.dotnet.core");
	}

	public static Query<DotNetTypeDeclaration> search(final DotNetTypeDeclaration aClass)
	{
		return search(aClass, GlobalSearchScope.allScope(aClass.getProject()));
	}

	public static Query<DotNetTypeDeclaration> search(final DotNetTypeDeclaration aClass, SearchScope scope)
	{
		return INSTANCE.createUniqueResultsQuery(new SearchParameters(aClass, scope));
	}

	public static Query<DotNetTypeDeclaration> search(final DotNetTypeDeclaration aClass, SearchScope scope, final boolean checkInheritance)
	{
		return INSTANCE.createUniqueResultsQuery(new SearchParameters(aClass, scope, checkInheritance));
	}
}
