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

package org.mustbe.consulo.csharp.ide.highlight;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpLocalVariable;
import org.mustbe.consulo.csharp.lang.psi.CSharpSoftTokens;
import org.mustbe.consulo.dotnet.psi.DotNetXXXAccessor;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 06.02.14
 */
public class CSharpHighlightUtil
{
	public static boolean isGeneratedElement(@NotNull PsiElement element)
	{
		if(element instanceof CSharpLocalVariable)
		{
			PsiElement parent = element.getParent();
			if(parent instanceof DotNetXXXAccessor)
			{
				IElementType accessorType = ((DotNetXXXAccessor) parent).getAccessorType();
				if(accessorType == CSharpSoftTokens.SET_KEYWORD && Comparing.equal(((CSharpLocalVariable) element).getName(), DotNetXXXAccessor.VALUE))
				{
					return true;
				}
			}
		}

		return false;
	}
}
