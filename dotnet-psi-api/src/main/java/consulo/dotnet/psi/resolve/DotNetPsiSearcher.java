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

package consulo.dotnet.psi.resolve;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.project.Project;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.ContainerUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author VISTALL
 * @since 13.07.14
 */
@ServiceAPI(ComponentScope.PROJECT)
public abstract class DotNetPsiSearcher implements DotNetPsiSearcherExtension
{
	@Nonnull
	public static DotNetPsiSearcher getInstance(@Nonnull Project project)
	{
		return project.getInstance(DotNetPsiSearcher.class);
	}

	@Deprecated
	public static enum TypeResoleKind
	{
		CLASS,
		STRUCT,
		UNKNOWN;

		@Nonnull
		public static TypeResoleKind[] VALUES = values();
	}

	public static final Function<DotNetTypeDeclaration, DotNetTypeDeclaration> DEFAULT_TRANSFORMER = typeDeclaration -> typeDeclaration;

	@Nullable
	@RequiredReadAction
	public DotNetNamespaceAsElement findNamespace(@Nonnull String qName, @Nonnull SearchScope scope)
	{
		return null;
	}

	@Nonnull
	@RequiredReadAction
	public DotNetTypeDeclaration[] findTypes(@Nonnull String vmQName, @Nonnull SearchScope scope)
	{
		return findTypes(vmQName, scope, DEFAULT_TRANSFORMER);
	}

	@Nonnull
	@RequiredReadAction
	public DotNetTypeDeclaration[] findTypes(@Nonnull String vmQName, @Nonnull SearchScope scope, @Nonnull Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		Collection<? extends DotNetTypeDeclaration> declarations = findTypesImpl(vmQName, scope);
		if(declarations.isEmpty())
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
		}
		List<DotNetTypeDeclaration> list = new ArrayList<DotNetTypeDeclaration>(declarations.size());

		for(DotNetTypeDeclaration declaration : declarations)
		{
			list.add(transformer.apply(declaration));
		}

		return ContainerUtil.toArray(list, DotNetTypeDeclaration.ARRAY_FACTORY);
	}

	@Nonnull
	@RequiredReadAction
	public abstract Collection<? extends DotNetTypeDeclaration> findTypesImpl(@Nonnull String vmQName, @Nonnull SearchScope scope);

	@Nullable
	@RequiredReadAction
	public DotNetTypeDeclaration findType(@Nonnull String vmQName, @Nonnull SearchScope scope)
	{
		return findType(vmQName, scope, DEFAULT_TRANSFORMER);
	}

	@Nullable
	@RequiredReadAction
	public DotNetTypeDeclaration findType(@Nonnull String vmQName, @Nonnull SearchScope scope, @Nonnull Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		DotNetTypeDeclaration[] types = findTypes(vmQName, scope, transformer);
		return ArrayUtil.getFirstElement(types);
	}
}
