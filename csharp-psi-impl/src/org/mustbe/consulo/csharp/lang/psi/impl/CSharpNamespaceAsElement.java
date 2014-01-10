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

package org.mustbe.consulo.csharp.lang.psi.impl;

import java.util.Collection;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.CSharpIndexKeys;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.NamespaceByQNameIndex;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.CommonProcessors;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IdFilter;
import lombok.val;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpNamespaceAsElement extends LightElement implements PsiNamedElement
{
	@NotNull
	private final String myQName;
	@NotNull
	private final GlobalSearchScope mySearchScopes;

	public CSharpNamespaceAsElement(@NotNull Project project, @NotNull String qName, @NotNull GlobalSearchScope searchScopes)
	{
		super(PsiManager.getInstance(project), CSharpLanguage.INSTANCE);
		myQName = qName;
		mySearchScopes = searchScopes;
	}

	@Nullable
	@Override
	public PsiElement getNavigationElement()
	{
		return findFirstNamespace();
	}

	@Override
	public void navigate(boolean requestFocus)
	{
		PsiElement navigationElement = findFirstNamespace();
		if(navigationElement instanceof Navigatable)
		{
			((Navigatable) navigationElement).navigate(requestFocus);
		}
	}

	@Override
	public boolean isValid()
	{
		return findFirstNamespace() != null;
	}

	@Nullable
	public DotNetNamespaceDeclaration findFirstNamespace()
	{
		val findFirstProcessor = new CommonProcessors.FindFirstProcessor<DotNetNamespaceDeclaration>();
		StubIndex.getInstance().process(CSharpIndexKeys.NAMESPACE_BY_QNAME_INDEX, myQName, getProject(), mySearchScopes, findFirstProcessor);
		if(findFirstProcessor.getFoundValue() != null)
		{
			return findFirstProcessor.getFoundValue();
		}

		val findFirstProcessor2 = new CommonProcessors.FindFirstProcessor<String>()
		{
			@Override
			protected boolean accept(String qName2)
			{
				return qName2.startsWith(myQName);
			}
		};

		StubIndex.getInstance().processAllKeys(CSharpIndexKeys.NAMESPACE_BY_QNAME_INDEX, findFirstProcessor2, mySearchScopes,
				IdFilter.getProjectIdFilter(getProject(), false));

		if(findFirstProcessor2.getFoundValue() != null)
		{
			Collection<DotNetNamespaceDeclaration> dotNetNamespaceDeclarations = NamespaceByQNameIndex.getInstance().get(findFirstProcessor2
					.getFoundValue(), getProject(), mySearchScopes);

			return ContainerUtil.getFirstItem(dotNetNamespaceDeclarations);
		}

		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(o == null || getClass() != o.getClass())
		{
			return false;
		}

		CSharpNamespaceAsElement that = (CSharpNamespaceAsElement) o;

		if(!getQName().equals(that.getQName()))
		{
			return false;
		}
		if(!mySearchScopes.equals(that.mySearchScopes))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = getQName().hashCode();
		result = 31 * result + mySearchScopes.hashCode();
		return result;
	}

	@Override
	public boolean processDeclarations(@NotNull final PsiScopeProcessor processor, @NotNull final ResolveState state, final PsiElement lastParent,
			@NotNull final PsiElement place)
	{
		return StubIndex.getInstance().process(CSharpIndexKeys.NAMESPACE_BY_QNAME_INDEX, getQName(), getProject(), mySearchScopes,
				new Processor<DotNetNamespaceDeclaration>()

		{
			@Override
			public boolean process(DotNetNamespaceDeclaration dotNetNamespaceDeclaration)
			{
				return dotNetNamespaceDeclaration.processDeclarations(processor, state, lastParent, place);
			}
		});
	}

	@Override
	public String toString()
	{
		return "CSharpNamespaceAsElement: " + getQName();
	}

	@NotNull
	public String getQName()
	{
		return myQName;
	}

	@Override
	public String getName()
	{
		QualifiedName qualifiedName = QualifiedName.fromDottedString(getQName());
		return qualifiedName.getLastComponent();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}
}
