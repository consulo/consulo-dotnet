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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.packageSupport.DotNetPackageDescriptor;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import org.mustbe.consulo.packageSupport.PackageManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class CSharpNamespaceDefRuntimeType implements DotNetRuntimeType
{
	private final String myQualifiedName;
	private final Project myProject;
	private final GlobalSearchScope myResolveScope;

	public CSharpNamespaceDefRuntimeType(String qualifiedName, Project project, GlobalSearchScope resolveScope)
	{
		myQualifiedName = qualifiedName;
		myProject = project;
		myResolveScope = resolveScope;
	}

	@Override
	public boolean isNullable()
	{
		return true;
	}

	@Nullable
	@Override
	public PsiElement toPsiElement()
	{
		return PackageManager.getInstance(myProject).findPackage(myQualifiedName, myResolveScope, DotNetPackageDescriptor.INSTANCE);
	}
}