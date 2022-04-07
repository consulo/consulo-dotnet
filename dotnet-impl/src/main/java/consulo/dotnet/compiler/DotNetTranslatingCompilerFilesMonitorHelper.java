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

package consulo.dotnet.compiler;

import consulo.compiler.TranslatingCompilerFilesMonitorHelper;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.module.extension.ModuleExtension;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 16.01.14
 */
public class DotNetTranslatingCompilerFilesMonitorHelper implements TranslatingCompilerFilesMonitorHelper
{
	@Nullable
	@Override
	public VirtualFile[] getRootsForModule(@Nonnull Module module)
	{
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(extension == null || extension.isAllowSourceRoots())
		{
			return null;
		}
		return ModuleRootManager.getInstance(module).getContentRoots();
	}

	@Override
	public boolean isModuleExtensionAffectToCompilation(ModuleExtension<?> extension)
	{
		return extension instanceof DotNetModuleExtension;
	}
}
