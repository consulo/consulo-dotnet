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

package org.mustbe.consulo.dotnet.resolve.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class DotNetPsiFacadeImpl extends DotNetPsiFacade
{
	private static final ExtensionPointName<DotNetPsiFacade> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.psi.facade");

	private final DotNetPsiFacade[] myFacades;
	@NotNull
	private final Project myProject;

	public DotNetPsiFacadeImpl(@NotNull Project project)
	{
		myProject = project;
		myFacades = EP_NAME.getExtensions(project);
	}

	@NotNull
	@Override
	public DotNetTypeDeclaration[] findTypes(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount)
	{
		List<DotNetTypeDeclaration> typeDeclarations = new ArrayList<DotNetTypeDeclaration>();
		for(DotNetPsiFacade facade : myFacades)
		{
			Collections.addAll(typeDeclarations, facade.findTypes(qName, searchScope, genericCount));
		}
		return typeDeclarations.isEmpty() ? DotNetTypeDeclaration.EMPTY_ARRAY : typeDeclarations.toArray(new DotNetTypeDeclaration[typeDeclarations
				.size()]);
	}

	@Nullable
	@Override
	public DotNetTypeDeclaration findType(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount)
	{
		for(DotNetPsiFacade facade : myFacades)
		{
			DotNetTypeDeclaration type = facade.findType(qName, searchScope, genericCount);
			if(type != null)
			{
				return type;
			}
		}
		return null;
	}

	@NotNull
	@Override
	public String[] getAllTypeNames()
	{
		List<String> list = new ArrayList<String>();
		for(DotNetPsiFacade facade : myFacades)
		{
			Collections.addAll(list, facade.getAllTypeNames());
		}
		return ArrayUtil.toStringArray(list);
	}

	@NotNull
	@Override
	public DotNetTypeDeclaration[] getTypesByName(@NotNull String name, @NotNull GlobalSearchScope searchScope)
	{
		List<DotNetTypeDeclaration> list = new ArrayList<DotNetTypeDeclaration>();
		for(DotNetPsiFacade facade : myFacades)
		{
			Collections.addAll(list, facade.getTypesByName(name, searchScope));
		}
		return list.isEmpty() ? DotNetTypeDeclaration.EMPTY_ARRAY : list.toArray(new DotNetTypeDeclaration[list.size()]);
	}

	@Override
	public DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		List<DotNetNamespaceAsElement> list = new ArrayList<DotNetNamespaceAsElement>();
		for(DotNetPsiFacade facade : myFacades)
		{
			DotNetNamespaceAsElement namespace = facade.findNamespace(qName, scope);
			if(namespace != null)
			{
				list.add(namespace);
			}
		}
		return list.isEmpty() ? null : new DotNetCompositeNamespaceAsElement(myProject, list.toArray(new DotNetNamespaceAsElement[list.size()]));
	}
}
