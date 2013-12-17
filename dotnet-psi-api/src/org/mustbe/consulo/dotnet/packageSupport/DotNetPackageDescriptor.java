/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.dotnet.packageSupport;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.stub.index.DotNetIndexKeys;
import org.mustbe.consulo.dotnet.psi.stub.index.NamespaceByQNameIndex;
import org.mustbe.consulo.packageSupport.PackageDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.Processor;
import com.intellij.util.indexing.IdFilter;
import lombok.val;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class DotNetPackageDescriptor implements PackageDescriptor
{
	public static final DotNetPackageDescriptor INSTANCE = new DotNetPackageDescriptor();

	@Override
	public boolean canCreate(@NotNull String qName, Project project, GlobalSearchScope searchScope)
	{
		val thisQ = toQName(qName);

		val booleanRef = new Ref<Boolean>(false);
		StubIndex.getInstance().processAllKeys(DotNetIndexKeys.NAMESPACE_BY_QNAME_INDEX, new Processor<String>()
		{
			@Override
			public boolean process(String s)
			{
				QualifiedName qualifiedName = toQName(s);
				if(qualifiedName.matchesPrefix(thisQ))
				{
					booleanRef.set(true);
					return false;
				}
				return true;
			}
		}, searchScope, IdFilter.getProjectIdFilter(project, false));

		return booleanRef.get();
	}

	@Nullable
	@Override
	public PsiElement getNavigationItem(@NotNull QualifiedName qualifiedName, @NotNull Project project)
	{
		Collection<String> allKeys = NamespaceByQNameIndex.getInstance().getAllKeys(project);
		for(String allKey : allKeys)
		{
			QualifiedName inKey = toQName(allKey);
			if(inKey.matchesPrefix(qualifiedName))
			{
				Collection<DotNetNamespaceDeclaration> dotNetNamespaceDeclarations = NamespaceByQNameIndex.getInstance().get(allKey, project,
						GlobalSearchScope.allScope(project));
				if(dotNetNamespaceDeclarations.isEmpty())
				{
					continue;
				}
				return dotNetNamespaceDeclarations.iterator().next();
			}
		}
		return null;
	}

	@NotNull
	@Override
	public String fromQName(@NotNull QualifiedName name)
	{
		return name.join(".");
	}

	@NotNull
	@Override
	public QualifiedName toQName(@NotNull String name)
	{
		if(name.isEmpty())
		{
			return QualifiedName.fromComponents();
		}
		return QualifiedName.fromDottedString(name);
	}
}
