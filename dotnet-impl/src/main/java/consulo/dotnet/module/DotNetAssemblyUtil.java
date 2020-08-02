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

package consulo.dotnet.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import consulo.annotation.DeprecationInfo;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.module.extension.DotNetModuleLangExtension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11.02.15
 */
@Deprecated
@DeprecationInfo("Use AssemblyModuleService")
public class DotNetAssemblyUtil
{
	@Nullable
	@RequiredReadAction
	public static String getAssemblyTitle(@Nonnull PsiElement element)
	{
		Module module = ModuleUtilCore.findModuleForPsiElement(element);
		if(module == null)
		{
			return null;
		}

		return getAssemblyTitle(module);
	}

	@Nullable
	@RequiredReadAction
	public static String getAssemblyTitle(Module module)
	{
		DotNetModuleLangExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
		if(extension == null)
		{
			return null;
		}
		String assemblyTitle = extension.getAssemblyTitle();
		if(assemblyTitle == null)
		{
			return module.getName();
		}
		return assemblyTitle;
	}
}
