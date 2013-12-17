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

package org.mustbe.consulo.packageSupport;

import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.packageSupport.DotNetPackageDescriptor;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class Package extends LightElement implements PsiNamedElement
{
	public static final Key<GlobalSearchScope> SEARCH_SCOPE_KEY = Key.create("resolve-scope");

	private final QualifiedName myQualifiedName;

	public Package(Project project, QualifiedName qualifiedName)
	{
		super(PsiManager.getInstance(project), Language.ANY);
		myQualifiedName = qualifiedName;
	}

	@NotNull
	public QualifiedName getQualifiedName()
	{
		return myQualifiedName;
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent,
			@NotNull PsiElement place)
	{
		GlobalSearchScope globalSearchScope = processor.getHint(SEARCH_SCOPE_KEY);
		if(globalSearchScope == null)
		{
			globalSearchScope = GlobalSearchScope.allScope(getProject());
		}
		List<PsiElement> children = DotNetPackageDescriptor.INSTANCE.getChildren(myQualifiedName, globalSearchScope, getProject());
		for(PsiElement child : children)
		{
			if(!processor.execute(child, state))
			{
				return false;
			}
		}
		return true;
	}

	@NotNull
	@Override
	public PsiElement[] getChildren()
	{
		List<PsiElement> children = DotNetPackageDescriptor.INSTANCE.getChildren(myQualifiedName, GlobalSearchScope.allScope(getProject()),
				getProject());
		return children.toArray(new PsiElement[children.size()]);
	}

	@NotNull
	@Override
	public PsiElement getNavigationElement()
	{
		PsiElement navigationItem = DotNetPackageDescriptor.INSTANCE.getNavigationItem(myQualifiedName, getProject());
		if(navigationItem != null)
		{
			return navigationItem;
		}
		return this;
	}

	@Override
	public String toString()
	{
		return "Package: " + myQualifiedName;
	}

	@Override
	public String getName()
	{
		return myQualifiedName.toString();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}
}
