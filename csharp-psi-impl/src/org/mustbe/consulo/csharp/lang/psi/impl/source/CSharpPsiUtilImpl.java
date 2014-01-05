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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class CSharpPsiUtilImpl
{
	public static boolean isCompiledElement(@NotNull PsiElement psi)
	{
		PsiFile containingFile = psi.getContainingFile();
		if(containingFile == null)
		{
			return false;
		}
		VirtualFile virtualFile = containingFile.getVirtualFile();
		return virtualFile != null && ProjectFileIndex.SERVICE.getInstance(psi.getProject()).isInLibraryClasses(virtualFile);
	}

	@NotNull
	public static DotNetRuntimeType toRuntimeType(@NotNull DotNetVariable variable)
	{
		DotNetType type = variable.getType();
		if(type == null)
		{
			return DotNetRuntimeType.ERROR_TYPE;
		}

		DotNetRuntimeType runtimeType = type.toRuntimeType();
		if(runtimeType == DotNetRuntimeType.AUTO_TYPE)
		{
			DotNetExpression initializer = variable.getInitializer();
			if(initializer == null)
			{
				return DotNetRuntimeType.UNKNOWN_TYPE;
			}

			return initializer.toRuntimeType();
		}
		else
		{
			return runtimeType;
		}
	}
}
