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

package org.mustbe.consulo.csharp.lang.psi.impl.resolve;

import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceHelper;
import org.mustbe.consulo.csharp.lang.psi.impl.msil.MsilToCSharpUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.TypeByQNameIndex;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.TypeIndex;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class CSharpPsiFacadeImpl extends DotNetPsiFacade.Adapter
{
	private final Project myProject;

	public CSharpPsiFacadeImpl(Project project)
	{
		myProject = project;
	}

	@NotNull
	@Override
	public String[] getAllTypeNames()
	{
		Collection<String> allKeys = TypeIndex.getInstance().getAllKeys(myProject);
		return ArrayUtil.toStringArray(allKeys);
	}

	@NotNull
	@Override
	public DotNetTypeDeclaration[] getTypesByName(@NotNull String name, @NotNull GlobalSearchScope searchScope)
	{
		Collection<DotNetTypeDeclaration> declarations = TypeIndex.getInstance().get(name, myProject, searchScope);
		return toArray(declarations);
	}

	@NotNull
	@Override
	public DotNetTypeDeclaration[] findTypes(@NotNull ResolveContext context)
	{
		Collection<DotNetTypeDeclaration> dotNetTypeDeclarations = TypeByQNameIndex.getInstance().get(context.getQName(), myProject, context.getScope());
		if(dotNetTypeDeclarations.isEmpty())
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
		}
		if(context.getGenericCount() < 0 && context.getTypeResoleKind() == TypeResoleKind.UNKNOWN)
		{
			return toArray(dotNetTypeDeclarations);
		}

		List<DotNetTypeDeclaration> list = new SmartList<DotNetTypeDeclaration>();
		for(DotNetTypeDeclaration type : dotNetTypeDeclarations)
		{
			if(isAcceptableType(context, type))
			{
				list.add(type);
			}
		}

		return toArray(list);
	}

	@Override
	@NotNull
	protected DotNetTypeDeclaration[] toArray(@NotNull Collection<? extends DotNetTypeDeclaration> list)
	{
		if(list.isEmpty())
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
		}
		DotNetTypeDeclaration[] array = new DotNetTypeDeclaration[list.size()];
		int i = 0;
		for(DotNetTypeDeclaration t : list)
		{
			array[i ++ ] = (DotNetTypeDeclaration) MsilToCSharpUtil.wrap(t);
		}
		return array;
	}

	@Override
	public DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull  GlobalSearchScope scope)
	{
		return CSharpNamespaceHelper.getNamespaceElementIfFind(myProject, qName, scope);
	}
}
