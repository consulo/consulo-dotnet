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

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeRuntimeType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 08.01.14
 */
public class CSharpTypeUtil
{
	/**
	 * We have expression
	 * int a = "test";
	 * <p/>
	 * "test" - string type, ill be 'top' parameter
	 * int - int type, ill 'target'
	 * return false due it not be casted
	 */
	public static boolean isInheritable(@NotNull DotNetRuntimeType top, @NotNull DotNetRuntimeType target)
	{
		if(top == DotNetRuntimeType.ERROR_TYPE || target == DotNetRuntimeType.ERROR_TYPE)
		{
			return false;
		}

		if(top == DotNetRuntimeType.NULL_TYPE && target.isNullable())
		{
			return true;
		}

		if(top.equals(target))
		{
			return true;
		}

		PsiElement topElement = top.toPsiElement();
		PsiElement targetElement = target.toPsiElement();
		if(Comparing.equal(topElement, targetElement))
		{
			return true;
		}

		if(topElement instanceof DotNetTypeDeclaration && target instanceof CSharpNativeRuntimeType)
		{
			if(Comparing.equal(((DotNetTypeDeclaration) topElement).getPresentableQName(), ((CSharpNativeRuntimeType) target)
					.getWrapperQualifiedClass()))
			{
				return true;
			}
		}


		return false;
	}
}
