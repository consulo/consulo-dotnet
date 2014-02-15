/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

package org.mustbe.consulo.ironPython.psi.impl;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.AccessDirection;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.impl.ResolveResultList;
import com.jetbrains.python.psi.resolve.PyResolveContext;
import com.jetbrains.python.psi.resolve.RatedResolveResult;
import com.jetbrains.python.psi.types.PyType;
import com.jetbrains.python.psi.types.TypeEvalContext;

/**
 * @author yole
 */
public class PyDotNetNamespaceType implements PyType
{
	private final DotNetNamespaceAsElement myPackage;
	@Nullable
	private final Module myModule;

	public PyDotNetNamespaceType(DotNetNamespaceAsElement aPackage, @Nullable Module module)
	{
		myPackage = aPackage;
		myModule = module;
	}

	@Override
	public List<? extends RatedResolveResult> resolveMember(@NotNull String name, @Nullable PyExpression location,
			@NotNull AccessDirection direction, @NotNull PyResolveContext resolveContext)
	{
		Project project = myPackage.getProject();
		DotNetPsiFacade facade = DotNetPsiFacade.getInstance(project);
		String childName = myPackage.getPresentableQName() + "." + name;
		GlobalSearchScope scope = getScope(project);
		ResolveResultList result = new ResolveResultList();
		final DotNetTypeDeclaration[] classes = facade.findTypes(childName, scope, -1);
		for(DotNetTypeDeclaration aClass : classes)
		{
			result.poke(aClass, RatedResolveResult.RATE_NORMAL);
		}
		final DotNetNamespaceAsElement psiPackage = facade.findNamespace(childName, scope);
		if(psiPackage != null)
		{
			result.poke(psiPackage, RatedResolveResult.RATE_NORMAL);
		}
		return result;
	}

	private GlobalSearchScope getScope(Project project)
	{
		return myModule != null ? myModule.getModuleWithDependenciesAndLibrariesScope(false) : ProjectScope.getAllScope(project);
	}

	@Override
	public Object[] getCompletionVariants(String completionPrefix, PsiElement location, ProcessingContext context)
	{
		List<Object> variants = new ArrayList<Object>();
		final GlobalSearchScope scope = getScope(location.getProject());
 /*   final PsiClass[] classes = myPackage.getClasses(scope);
	for (PsiClass psiClass : classes) {
      variants.add(LookupElementBuilder.create(psiClass).withIcon(IconDescriptorUpdaters.getIcon(psiClass, 0)));
    }
    final PsiPackage[] subPackages = myPackage.getSubPackages(scope);
    for (PsiPackage subPackage : subPackages) {
      variants.add(LookupElementBuilder.create(subPackage).withIcon(IconDescriptorUpdaters.getIcon(subPackage, 0)));
    }   */
		return ArrayUtil.toObjectArray(variants);
	}

	@Override
	public String getName()
	{
		return myPackage.getPresentableQName();
	}

	@Override
	public boolean isBuiltin(TypeEvalContext context)
	{
		return false;
	}

	@Override
	public void assertValid(String message)
	{
	}
}
