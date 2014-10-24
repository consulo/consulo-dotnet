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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.consulo.annotations.Immutable;
import org.consulo.lombok.annotations.ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 13.07.14
 */
@ProjectService
public abstract class DotNetPsiSearcher
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

	public static final NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration> DEFAULT_TRANSFORMER = new
			NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration>()
	{
		@NotNull
		@Override
		public DotNetTypeDeclaration fun(DotNetTypeDeclaration typeDeclaration)
		{
			return typeDeclaration;
		}
	};

	@Nullable
	public DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		return null;
	}

	@NotNull
	public DotNetTypeDeclaration[] findTypes(@NotNull String vmQName, @NotNull GlobalSearchScope scope)
	{
		return findTypes(vmQName, scope, TypeResoleKind.UNKNOWN, DEFAULT_TRANSFORMER);
	}

	@NotNull
	public DotNetTypeDeclaration[] findTypes(@NotNull String vmQName, @NotNull GlobalSearchScope scope, @NotNull TypeResoleKind typeResoleKind)
	{
		return findTypes(vmQName, scope, typeResoleKind, DEFAULT_TRANSFORMER);
	}

	@NotNull
	public DotNetTypeDeclaration[] findTypes(@NotNull String vmQName, @NotNull GlobalSearchScope scope, @NotNull TypeResoleKind typeResoleKind,
			@NotNull NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		Collection<? extends DotNetTypeDeclaration> declarations = findTypesImpl(vmQName, scope, typeResoleKind);
		if(declarations.isEmpty())
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
		}
		List<DotNetTypeDeclaration> list = new ArrayList<DotNetTypeDeclaration>(declarations.size());

		for(DotNetTypeDeclaration declaration : declarations)
		{
			//if(typeResoleKind == TypeResoleKind.UNKNOWN ||
			//		declaration.isStruct() && typeResoleKind == TypeResoleKind.STRUCT ||
			//		!declaration.isStruct() && typeResoleKind == TypeResoleKind.CLASS)
			{
				list.add(transformer.fun(declaration));
			}
		}

		return ContainerUtil.toArray(list, DotNetTypeDeclaration.ARRAY_FACTORY);
	}

	@NotNull
	public abstract Collection<? extends DotNetTypeDeclaration> findTypesImpl(@NotNull String vmQName, @NotNull GlobalSearchScope scope,
			@NotNull TypeResoleKind typeResoleKind);

	@Nullable
	public DotNetTypeDeclaration findType(@NotNull String vmQName, @NotNull GlobalSearchScope scope)
	{
		return findType(vmQName, scope, TypeResoleKind.UNKNOWN);
	}

	@Nullable
	public DotNetTypeDeclaration findType(@NotNull String vmQName, @NotNull GlobalSearchScope scope, @NotNull TypeResoleKind typeResoleKind)
	{
		return findType(vmQName, scope, typeResoleKind, DEFAULT_TRANSFORMER);
	}

	@Nullable
	public DotNetTypeDeclaration findType(@NotNull String vmQName, @NotNull GlobalSearchScope scope, @NotNull TypeResoleKind typeResoleKind,
			@NotNull NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		DotNetTypeDeclaration[] types = findTypes(vmQName, scope, typeResoleKind, transformer);
		return ArrayUtil.getFirstElement(types);
	}
}
