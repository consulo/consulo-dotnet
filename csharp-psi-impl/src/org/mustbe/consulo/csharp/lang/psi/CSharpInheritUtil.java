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

package org.mustbe.consulo.csharp.lang.psi;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 31.12.13.
 */
public class CSharpInheritUtil
{
	public static boolean isParentOfOrSelf(CSharpTypeDeclaration typeDeclaration, @NotNull String parentClass)
	{
		String presentableQName = typeDeclaration.getPresentableQName();
		return Comparing.equal(presentableQName, parentClass) || isParentOf(typeDeclaration, parentClass);
	}

	public static boolean isParentOf(DotNetTypeDeclaration typeDeclaration, @NotNull String parentClass)
	{
		for(DotNetType dotNetType : typeDeclaration.getExtends())
		{
			DotNetRuntimeType runtimeType = dotNetType.toRuntimeType();

			PsiElement psiElement = runtimeType.toPsiElement();
			if(psiElement instanceof CSharpTypeDeclaration)
			{
				if(Comparing.equal(((CSharpTypeDeclaration) psiElement).getPresentableQName(), parentClass))
				{
					return true;
				}

				if(isParentOf((CSharpTypeDeclaration) psiElement, parentClass))
				{
					return true;
				}
			}
		}
		return false;
	}
}
