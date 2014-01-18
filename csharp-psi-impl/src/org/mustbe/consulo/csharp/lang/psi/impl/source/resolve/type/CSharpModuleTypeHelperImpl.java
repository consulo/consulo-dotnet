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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpFileFactory;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import com.intellij.ProjectTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 18.01.14
 */
public class CSharpModuleTypeHelperImpl extends CSharpModuleTypeHelper
{
	private Module myModule;

	private DotNetTypeDeclaration myArrayTypeDeclaration;

	public CSharpModuleTypeHelperImpl(Module module)
	{
		myModule = module;
		myModule.getProject().getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter()
		{
			@Override
			public void rootsChanged(ModuleRootEvent event)
			{
				myArrayTypeDeclaration = null;
			}
		});
	}

	@Nullable
	@Override
	public DotNetTypeDeclaration getArrayType()
	{
		if(myArrayTypeDeclaration != null)
		{
			return myArrayTypeDeclaration;
		}
		GlobalSearchScope searchScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule);
		DotNetTypeDeclaration type = DotNetPsiFacade.getInstance(myModule.getProject()).findType(DotNetTypes.System_Array, searchScope, 0);
		if(type == null)
		{
			return null;
		}

		String typeText =
				"public class ArrayImpl<T> : System.Array" +
				"{" +
				"}";

		DotNetTypeDeclaration typeDeclaration = CSharpFileFactory.createTypeDeclaration(myModule.getProject(), searchScope, typeText);
		myArrayTypeDeclaration = typeDeclaration;
		return typeDeclaration;
	}
}
