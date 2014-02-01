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

package org.mustbe.consulo.dotnet.module;

import org.consulo.module.extension.ModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class DotNetModuleUtil
{
	public static boolean isUnderSourceRoot(@NotNull PsiElement element)
	{
		Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(element);
		if(moduleForPsiElement == null)
		{
			return false;
		}
		VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
		if(virtualFile == null)
		{
			return false;
		}
		ModuleFileIndex fileIndex = ModuleRootManager.getInstance(moduleForPsiElement).getFileIndex();
		return fileIndex.isInSourceContent(virtualFile) || fileIndex.isInTestSourceContent(virtualFile);
	}

	@Nullable
	public static <T extends ConfigurationProfileEx<T>> T getProfileEx(@NotNull PsiElement element, @NotNull Key<T> key,
			@NotNull Class<? extends ModuleExtension> clazz)
	{
		Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(element);
		if(moduleForPsiElement == null)
		{
			return null;
		}
		return getProfileEx(moduleForPsiElement, key, clazz);
	}

	@Nullable
	public static <T extends ConfigurationProfileEx<T>> T getProfileEx(@NotNull Module module, @NotNull Key<T> key,
			@NotNull Class<? extends ModuleExtension> clazz)
	{
		ModuleExtension<?> extension = ModuleUtilCore.getExtension(module, clazz);
		if(extension == null)
		{
			return null;
		}

		DotNetModuleExtension<?> dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		assert dotNetModuleExtension != null;
		return dotNetModuleExtension.getCurrentProfileEx(key);
	}
}
