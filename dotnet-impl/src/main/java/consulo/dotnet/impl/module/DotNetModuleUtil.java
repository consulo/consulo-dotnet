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

package consulo.dotnet.impl.module;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleFileIndex;
import consulo.module.content.ModuleRootManager;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class DotNetModuleUtil
{
	@RequiredReadAction
	public static boolean isUnderSourceRoot(@Nonnull PsiElement element)
	{
		Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(element);
		if(moduleForPsiElement == null)
		{
			return false;
		}
		PsiFile containingFile = element.getContainingFile();
		if(containingFile == null)
		{
			return false;
		}
		VirtualFile virtualFile = containingFile.getVirtualFile();
		if(virtualFile == null)
		{
			return false;
		}
		ModuleFileIndex fileIndex = ModuleRootManager.getInstance(moduleForPsiElement).getFileIndex();
		return fileIndex.isInSourceContent(virtualFile) || fileIndex.isInTestSourceContent(virtualFile);
	}
}
