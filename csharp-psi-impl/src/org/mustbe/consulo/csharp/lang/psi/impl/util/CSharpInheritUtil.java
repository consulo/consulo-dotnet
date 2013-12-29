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

package org.mustbe.consulo.csharp.lang.psi.impl.util;

import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpInheritUtil
{
	public static boolean isInherit(DotNetTypeDeclaration parent, DotNetTypeDeclaration accessor)
	{
		if(parent == accessor)
		{
			return false;
		}

		DotNetTypeList extendTypeList = accessor.getExtendTypeList();
		if(extendTypeList == null)
		{
			return false;
		}

		DotNetRuntimeType[] runtimeTypes = extendTypeList.getRuntimeTypes();
		for(DotNetRuntimeType runtimeType : runtimeTypes)
		{
			PsiElement psiElement = runtimeType.toPsiElement();
			if(psiElement == null)
			{
				continue;
			}

			if(psiElement == parent)
			{
				return true;
			}
		}
		return false;
	}
}
