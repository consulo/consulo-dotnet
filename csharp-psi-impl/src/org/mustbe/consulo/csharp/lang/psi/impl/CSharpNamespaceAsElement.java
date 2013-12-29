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

package org.mustbe.consulo.csharp.lang.psi.impl;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.stub.index.DotNetIndexKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
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

	@NotNull
	@Override
	public PsiElement getNavigationElement()
	{
		return findFirstNamespace();
	}

	private DotNetNamespaceDeclaration findFirstNamespace()
	{
		val ref = new Ref<DotNetNamespaceDeclaration>();
		StubIndex.getInstance().process(DotNetIndexKeys.NAMESPACE_BY_QNAME_INDEX, getQName(), getProject(), mySearchScopes,
				new Processor<DotNetNamespaceDeclaration>()

		{
			@Override
			public boolean process(DotNetNamespaceDeclaration dotNetNamespaceDeclaration)
			{
				ref.set(dotNetNamespaceDeclaration);
				return false;
			}
		});
		return ref.get();
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
		return StubIndex.getInstance().process(DotNetIndexKeys.NAMESPACE_BY_QNAME_INDEX, getQName(), getProject(), mySearchScopes,
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
