/*
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

package org.mustbe.consulo.dotnet.resolve;

import java.util.Collection;

import org.consulo.lombok.annotations.ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 10.01.14
 */
@ProjectService
public abstract class DotNetPsiFacade
{
	public static class Adapter extends DotNetPsiFacade
	{
		@NotNull
		@Override
		public DotNetTypeDeclaration[] findTypes(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount)
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
		}

		@Nullable
		@Override
		public DotNetTypeDeclaration findType(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount)
		{
			DotNetTypeDeclaration[] types = findTypes(qName, searchScope, genericCount);
			return types.length > 0 ? types[0] : null;
		}

		@NotNull
		@Override
		public String[] getAllTypeNames()
		{
			return ArrayUtil.EMPTY_STRING_ARRAY;
		}

		@NotNull
		@Override
		public DotNetTypeDeclaration[] getTypesByName(@NotNull String name, @NotNull GlobalSearchScope searchScope)
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
		}

		@NotNull
		protected DotNetTypeDeclaration[] toArray(@NotNull Collection<? extends DotNetTypeDeclaration> list)
		{
			return list.isEmpty() ? DotNetTypeDeclaration.EMPTY_ARRAY : list.toArray(new DotNetTypeDeclaration[list.size()]);
		}
	}

	@NotNull
	public abstract DotNetTypeDeclaration[] findTypes(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount);

	@Nullable
	public abstract DotNetTypeDeclaration findType(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount);

	@NotNull
	public abstract String[] getAllTypeNames();

	@NotNull
	public abstract DotNetTypeDeclaration[] getTypesByName(@NotNull String name, @NotNull GlobalSearchScope searchScope);
}
