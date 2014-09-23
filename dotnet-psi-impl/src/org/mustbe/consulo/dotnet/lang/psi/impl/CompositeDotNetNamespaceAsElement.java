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

package org.mustbe.consulo.dotnet.lang.psi.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public class CompositeDotNetNamespaceAsElement extends BaseDotNetNamespaceAsElement
{
	private List<DotNetNamespaceAsElement> myList;

	public CompositeDotNetNamespaceAsElement(Project project, @NotNull String qName, List<DotNetNamespaceAsElement> list)
	{
		super(project, Language.ANY, qName);
		myList = list;
	}

	@NotNull
	@Override
	public PsiElement[] findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, boolean withChildNamespaces)
	{
		List<PsiElement> list = new SmartList<PsiElement>();
		for(DotNetNamespaceAsElement dotNetNamespaceAsElement : myList)
		{
			PsiElement[] children = dotNetNamespaceAsElement.findChildren(name, globalSearchScope, withChildNamespaces);
			Collections.addAll(list, children);
		}
		return list.isEmpty() ? PsiElement.EMPTY_ARRAY : ContainerUtil.toArray(list, PsiElement.ARRAY_FACTORY);
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public Collection<? extends PsiElement> getChildren(@NotNull GlobalSearchScope globalSearchScope, boolean withChildNamespaces)
	{
		List list = new LinkedList<PsiElement>();
		for(DotNetNamespaceAsElement dotNetNamespaceAsElement : myList)
		{
			Collection<? extends PsiElement> children = dotNetNamespaceAsElement.getChildren(globalSearchScope, withChildNamespaces);
			list.addAll(children);
		}
		return list;
	}
}
