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

package org.mustbe.consulo.msil.lang.psi.impl.resolve;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceUtil;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.impl.MsilNamespaceAsElementImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilAllNamespaceIndex;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilNamespaceIndex;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilTypeByQNameIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 13.07.14
 */
public class MsilPsiSearcher extends DotNetPsiSearcher
{
	private Project myProject;

	public MsilPsiSearcher(Project project)
	{
		myProject = project;
	}

	@NotNull
	@Override
	public Collection<? extends DotNetTypeDeclaration> findTypesImpl(@NotNull String vmQName, @NotNull GlobalSearchScope scope,
			@NotNull TypeResoleKind typeResoleKind)
	{
		return MsilTypeByQNameIndex.getInstance().get(vmQName, myProject, scope);
	}

	@Nullable
	@Override
	public DotNetNamespaceAsElement findNamespaceImpl(@NotNull String indexKey, @NotNull String qName, @NotNull GlobalSearchScope scope)
	{
		if(DotNetNamespaceUtil.ROOT_FOR_INDEXING.equals(indexKey))
		{
			return new MsilNamespaceAsElementImpl(myProject, indexKey, qName);
		}

		Collection<MsilClassEntry> temp = MsilNamespaceIndex.getInstance().get(indexKey, myProject, scope);
		if(!temp.isEmpty())
		{
			return new MsilNamespaceAsElementImpl(myProject, indexKey, qName);
		}

		temp = MsilAllNamespaceIndex.getInstance().get(indexKey, myProject, scope);
		if(!temp.isEmpty())
		{
			return new MsilNamespaceAsElementImpl(myProject, indexKey, qName);
		}
		return null;
	}
}
