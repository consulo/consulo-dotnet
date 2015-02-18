/*
 * Copyright 2013-2015 must-be.org
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleLangExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 11.02.15
 */
public class DotNetAssemblyUtil
{
	@Nullable
	@RequiredReadAction
	public static String getAssemblyTitle(@NotNull PsiElement element)
	{
		Module module = ModuleUtilCore.findModuleForPsiElement(element);
		if(module == null)
		{
			return null;
		}

		DotNetModuleLangExtension extension = ModuleUtilCore.getExtension(element, DotNetModuleLangExtension.class);
		if(extension == null)
		{
			return module.getName();
		}
		String assemblyTitle = extension.getAssemblyTitle();
		if(assemblyTitle == null)
		{
			return module.getName();
		}
		return assemblyTitle;
	}
}
