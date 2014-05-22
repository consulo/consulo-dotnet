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

import org.consulo.annotations.Immutable;
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
	public static enum TypeResoleKind
	{
		CLASS,
		STRUCT,
		UNKNOWN;

		@Immutable
		@NotNull
		public static TypeResoleKind[] VALUES = values();
	}

	public static class ResolveContext
	{
		private final String myQName;
		private final int myGenericCount;
		private final TypeResoleKind myTypeResoleKind;
		private final GlobalSearchScope myScope;

		public ResolveContext(String qname, int genericCount, TypeResoleKind typeResoleKind, GlobalSearchScope scope)
		{
			myQName = qname;
			myGenericCount = genericCount;
			myTypeResoleKind = typeResoleKind;
			myScope = scope;
		}

		public int getGenericCount()
		{
			return myGenericCount;
		}

		public TypeResoleKind getTypeResoleKind()
		{
			return myTypeResoleKind;
		}

		public GlobalSearchScope getScope()
		{
			return myScope;
		}

		public String getQName()
		{
			return myQName;
		}
	}

	public static class Adapter extends DotNetPsiFacade
	{
		@NotNull
		@Override
		public DotNetTypeDeclaration[] findTypes(@NotNull ResolveContext context)
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
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

		@Override
		public DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull GlobalSearchScope scope)
		{
			return null;
		}

		@NotNull
		protected DotNetTypeDeclaration[] toArray(@NotNull Collection<? extends DotNetTypeDeclaration> list)
		{
			return list.isEmpty() ? DotNetTypeDeclaration.EMPTY_ARRAY : list.toArray(new DotNetTypeDeclaration[list.size()]);
		}
	}

	public boolean isAcceptableType(@NotNull ResolveContext context, @NotNull DotNetTypeDeclaration type)
	{
		switch(context.getTypeResoleKind())
		{
			case CLASS:
				if(type.isStruct())
				{
					return false;
				}
				break;
			case STRUCT:
				if(!type.isStruct())
				{
					return false;
				}
				break;
		}

		boolean needCheckGeneric = context.getGenericCount() != -1;
		return needCheckGeneric && type.getGenericParametersCount() == context.getGenericCount() || !needCheckGeneric;
	}

	@Nullable
	public DotNetTypeDeclaration findType(@NotNull ResolveContext resolveContext)
	{
		DotNetTypeDeclaration[] types = findTypes(resolveContext);
		return types.length > 0 ? types[0] : null;
	}

	@NotNull
	public abstract DotNetTypeDeclaration[] findTypes(@NotNull ResolveContext context);

	@NotNull
	public DotNetTypeDeclaration[] findTypes(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount)
	{
		return findTypes(new ResolveContext(qName, genericCount, TypeResoleKind.UNKNOWN, searchScope));
	}

	@Nullable
	public DotNetTypeDeclaration findType(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount)
	{
		DotNetTypeDeclaration[] types = findTypes(qName, searchScope, genericCount);
		return types.length > 0 ? types[0] : null;
	}

	@NotNull
	public abstract String[] getAllTypeNames();

	@NotNull
	public abstract DotNetTypeDeclaration[] getTypesByName(@NotNull String name, @NotNull GlobalSearchScope searchScope);

	public abstract DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull GlobalSearchScope scope);
}
