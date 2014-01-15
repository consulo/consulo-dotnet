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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
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


	@Nullable
	public static DotNetNamedElement findSingleElement(PsiFile file)
	{
		if(!(file instanceof CSharpFileImpl))
		{
			return null;
		}

		DotNetNamedElement[] members = ((CSharpFileImpl) file).getMembers();
		if(members.length != 1)
		{
			return null;
		}

		DotNetNamedElement member = members[0];
		if(member instanceof DotNetNamespaceDeclaration)
		{
			DotNetNamedElement[] namespacesDeclarations = ((DotNetNamespaceDeclaration) member).getMembers();
			if(namespacesDeclarations.length != 1)
			{
				return null;
			}
			DotNetNamedElement namespacesDeclaration = namespacesDeclarations[0];
			if(Comparing.equal(FileUtil.getNameWithoutExtension(file.getName()), namespacesDeclaration.getName()))
			{
				return namespacesDeclaration;
			}
			return null;
		}
		else
		{
			if(Comparing.equal(FileUtil.getNameWithoutExtension(file.getName()), member.getName()))
			{
				return member;
			}
		}

		return null;
	}

	@NotNull
	public static DotNetTypeRef toRuntimeType(@NotNull DotNetVariable variable)
	{
		DotNetType type = variable.getType();
		if(type == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}

		DotNetTypeRef runtimeType = type.toTypeRef();
		if(runtimeType == DotNetTypeRef.AUTO_TYPE)
		{
			DotNetExpression initializer = variable.getInitializer();
			if(initializer == null)
			{
				return DotNetTypeRef.UNKNOWN_TYPE;
			}

			return initializer.toTypeRef();
		}
		else
		{
			return runtimeType;
		}
	}
}
