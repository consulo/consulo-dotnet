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

import org.mustbe.consulo.ironPython.module.extension.IronPythonModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.jetbrains.python.codeInsight.imports.AutoImportQuickFix;
import com.jetbrains.python.codeInsight.imports.PyImportCandidateProvider;

/**
 * @author yole
 * @author VISTALL
 */
public class PyDotNetImportCandidateProvider implements PyImportCandidateProvider
{
	@Override
	public void addImportCandidates(PsiReference reference, String name, AutoImportQuickFix quickFix)
	{
		final PsiElement element = reference.getElement();
		final Project project = element.getProject();
		Module module = ModuleUtil.findModuleForPsiElement(element);

		if(module != null && ModuleUtilCore.getExtension(module, IronPythonModuleExtension.class) == null)
		{
			return;
		}
	   /*
		GlobalSearchScope scope = module == null ? ProjectScope.getAllScope(project) : module.getModuleWithDependenciesAndLibrariesScope(false);
		DotNetPsiFacade cache = DotNetPsiFacade.getInstance(project);
		final DotNetTypeDeclaration[] classesByName = cache.getClassesByName(name, scope);
		for(PsiClass psiClass : classesByName)
		{
			final QualifiedName packageQName = QualifiedName.fromDottedString(psiClass.getQualifiedName()).removeLastComponent();
			quickFix.addImport(psiClass, psiClass.getContainingFile(), packageQName);
		}   */
	}
}
