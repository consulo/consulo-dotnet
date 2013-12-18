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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.stub.index.DotNetIndexKeys;
import org.mustbe.consulo.dotnet.psi.stub.index.MethodByQNameIndex;
import org.mustbe.consulo.dotnet.psi.stub.index.NamespaceByQNameIndex;
import org.mustbe.consulo.dotnet.psi.stub.index.TypeByQNameIndex;
import org.mustbe.consulo.packageSupport.PackageDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
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

	@Override
	@NotNull
	public List<PsiElement> getChildren(@NotNull final QualifiedName qualifiedName, @NotNull GlobalSearchScope globalSearchScope,
			@NotNull Project project)
	{
		val tnames = new HashSet<String>();
		val nnames = new HashSet<String>();
		val mnames = new HashSet<String>();
		StubIndex.getInstance().processAllKeys(DotNetIndexKeys.TYPE_BY_QNAME_INDEX, new Processor<String>()
		{
			@Override
			public boolean process(String s)
			{
				QualifiedName q = toQName(s);
				if(qualifiedName.getComponentCount() == 0)
				{
					if(q.getComponentCount() == 1)
					{
						tnames.add(s);
					}
				}
				else if(q.matchesPrefix(qualifiedName) && (q.getComponentCount() - 1) == qualifiedName.getComponentCount())
				{
					tnames.add(s);
				}
				return true;
			}
		}, globalSearchScope, IdFilter.getProjectIdFilter(project, false));

		StubIndex.getInstance().processAllKeys(DotNetIndexKeys.NAMESPACE_BY_QNAME_INDEX, new Processor<String>()
		{
			@Override
			public boolean process(String s)
			{
				QualifiedName q = toQName(s);
				if(qualifiedName.getComponentCount() == 0)
				{
					if(q.getComponentCount() == 1)
					{
						nnames.add(s);
					}
				}
				else if(q.matchesPrefix(qualifiedName) && (q.getComponentCount() - 1) == qualifiedName.getComponentCount())
				{
					nnames.add(s);
				}
				return true;
			}
		}, globalSearchScope, IdFilter.getProjectIdFilter(project, false));

		StubIndex.getInstance().processAllKeys(DotNetIndexKeys.METHOD_BY_QNAME_INDEX, new Processor<String>()
		{
			@Override
			public boolean process(String s)
			{
				QualifiedName q = toQName(s);
				if(qualifiedName.getComponentCount() == 0)
				{
					if(q.getComponentCount() == 1)
					{
						mnames.add(s);
					}
				}
				else if(q.matchesPrefix(qualifiedName) && (q.getComponentCount() - 1) == qualifiedName.getComponentCount())
				{
					mnames.add(s);
				}
				return true;
			}
		}, globalSearchScope, IdFilter.getProjectIdFilter(project, false));

		val list = new LinkedHashSet<PsiElement>(tnames.size() + nnames.size() + mnames.size());
		for(String name : tnames)
		{
			Collection<DotNetTypeDeclaration> elements = TypeByQNameIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));
			ContainerUtil.addAllNotNull(list, elements);
		}
		for(String name : nnames)
		{
			Collection<DotNetNamespaceDeclaration> elements = NamespaceByQNameIndex.getInstance().get(name, project,
					GlobalSearchScope.allScope(project));
			ContainerUtil.addAllNotNull(list, elements);
		}
		for(String name : mnames)
		{
			Collection<DotNetMethodDeclaration> elements = MethodByQNameIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));
			ContainerUtil.addAllNotNull(list, elements);
		}
		return new ArrayList<PsiElement>(list);
	}
}
