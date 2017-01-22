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

package consulo.dotnet.resolve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;
import consulo.annotations.Immutable;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetTypeDeclaration;

/**
 * @author VISTALL
 * @since 13.07.14
 */
public abstract class DotNetPsiSearcher
{
	@NotNull
	public static DotNetPsiSearcher getInstance(@NotNull Project project)
	{
		return ServiceManager.getService(project, DotNetPsiSearcher.class);
	}

	@Deprecated
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
	@RequiredReadAction
	public DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		return null;
	}

	@NotNull
	@RequiredReadAction
	public DotNetTypeDeclaration[] findTypes(@NotNull String vmQName, @NotNull GlobalSearchScope scope)
	{
		return findTypes(vmQName, scope, DEFAULT_TRANSFORMER);
	}

	@NotNull
	@RequiredReadAction
	public DotNetTypeDeclaration[] findTypes(@NotNull String vmQName, @NotNull GlobalSearchScope scope, @NotNull NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		Collection<? extends DotNetTypeDeclaration> declarations = findTypesImpl(vmQName, scope);
		if(declarations.isEmpty())
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
		}
		List<DotNetTypeDeclaration> list = new ArrayList<DotNetTypeDeclaration>(declarations.size());

		for(DotNetTypeDeclaration declaration : declarations)
		{
			list.add(transformer.fun(declaration));
		}

		return ContainerUtil.toArray(list, DotNetTypeDeclaration.ARRAY_FACTORY);
	}

	@NotNull
	@RequiredReadAction
	public abstract Collection<? extends DotNetTypeDeclaration> findTypesImpl(@NotNull String vmQName, @NotNull GlobalSearchScope scope);

	@Nullable
	@RequiredReadAction
	public DotNetTypeDeclaration findType(@NotNull String vmQName, @NotNull GlobalSearchScope scope)
	{
		return findType(vmQName, scope, DEFAULT_TRANSFORMER);
	}

	@Nullable
	@RequiredReadAction
	public DotNetTypeDeclaration findType(@NotNull String vmQName, @NotNull GlobalSearchScope scope, @NotNull NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		DotNetTypeDeclaration[] types = findTypes(vmQName, scope, transformer);
		return ArrayUtil.getFirstElement(types);
	}
}
