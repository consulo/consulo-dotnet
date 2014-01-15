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

package org.mustbe.consulo.dotnet.compiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.extension.DotNetStructurableModuleExtension;
import com.intellij.compiler.impl.TranslatingCompilerFilesMonitorHelper;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 16.01.14
 */
public class DotNetTranslatingCompilerFilesMonitorHelper implements TranslatingCompilerFilesMonitorHelper
{
	@Nullable
	@Override
	public VirtualFile[] getRootsForModule(@NotNull Module module)
	{
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(extension == null || extension instanceof DotNetStructurableModuleExtension)
		{
			return null;
		}
		return ModuleRootManager.getInstance(module).getContentRoots();
	}
}
