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

import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.lang.psi.impl.CompositeDotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 13.07.14
 */
public class DotNetPsiSearcherImpl extends DotNetPsiSearcher
{
	private static final ExtensionPointName<DotNetPsiSearcher> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.psiSearcher");

	private DotNetPsiSearcher[] mySearchers;
	private Project myProject;

	public DotNetPsiSearcherImpl(Project project)
	{
		myProject = project;
		mySearchers = EP_NAME.getExtensions(project);
	}

	@Nullable
	@Override
	public DotNetNamespaceAsElement findNamespace(@NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		List<DotNetNamespaceAsElement> namespaceAsElements = new SmartList<DotNetNamespaceAsElement>();
		for(DotNetPsiSearcher searcher : mySearchers)
		{
			DotNetNamespaceAsElement namespace = searcher.findNamespace(qName, scope);
			if(namespace != null)
			{
				namespaceAsElements.add(namespace);
			}
		}

		if(namespaceAsElements.isEmpty())
		{
			return null;
		}
		else if(namespaceAsElements.size() == 1)
		{
			return namespaceAsElements.get(0);
		}
		return new CompositeDotNetNamespaceAsElement(myProject, qName, namespaceAsElements);
	}

	@NotNull
	@Override
	public Collection<? extends DotNetTypeDeclaration> findTypesImpl(@NotNull String vmQName, @NotNull GlobalSearchScope scope,
			@NotNull TypeResoleKind typeResoleKind)
	{
		List<DotNetTypeDeclaration> list = new SmartList<DotNetTypeDeclaration>();
		for(DotNetPsiSearcher searcher : mySearchers)
		{
			list.addAll(searcher.findTypesImpl(vmQName, scope, typeResoleKind));
		}
		return list;
	}
}
