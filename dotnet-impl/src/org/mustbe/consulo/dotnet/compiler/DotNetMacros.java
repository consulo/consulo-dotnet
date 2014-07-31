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

import org.consulo.compiler.ModuleCompilerPathsManager;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.roots.impl.ProductionContentFolderTypeProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;

/**
 * @author VISTALL
 * @since 27.11.13.
 */
public class DotNetMacros
{
	@NotNull
	public static String extract(@NotNull Module module, DotNetModuleExtension<?> extension)
	{
		return extract(module, extension, false);
	}

	@NotNull
	public static String extract(@NotNull Module module, DotNetModuleExtension<?> extension, boolean debugSymbols)
	{
		ModuleCompilerPathsManager compilerPathsManager = ModuleCompilerPathsManager.getInstance(module);

		String fileExtension = null;
		if(debugSymbols)
		{
			fileExtension = extension.getDebugFileExtension();
		}
		else
		{
			fileExtension = extension.getTarget().getExtension();
		}

		String path = extension.getOutputDir() + "/" + extension.getFileName();
		path = StringUtil.replace(path, DotNetModuleExtension.MODULE_OUTPUT_DIR, compilerPathsManager.getCompilerOutputUrl
				(ProductionContentFolderTypeProvider.getInstance
				()));

		String currentLayerName = ModuleRootManager.getInstance(module).getCurrentLayerName();
		path = StringUtil.replace(path, DotNetModuleExtension.CONFIGURATION, currentLayerName);
		path = StringUtil.replace(path, DotNetModuleExtension.MODULE_NAME, module.getName());
		path = StringUtil.replace(path, DotNetModuleExtension.OUTPUT_FILE_EXT, fileExtension);
		return FileUtil.toSystemDependentName(VfsUtil.urlToPath(path));
	}

	public static String getModuleOutputDirUrl(@NotNull Module module, DotNetModuleExtension<?> extension)
	{
		return extractLikeWorkDir(module, extension.getOutputDir(), true);
	}

	public static String extractLikeWorkDir(@NotNull Module module, @NotNull String path, boolean url)
	{
		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		ModuleCompilerPathsManager compilerPathsManager = ModuleCompilerPathsManager.getInstance(module);
		String compilerOutputUrl = compilerPathsManager.getCompilerOutputUrl(ProductionContentFolderTypeProvider.getInstance());

		path = StringUtil.replace(path, DotNetModuleExtension.MODULE_OUTPUT_DIR, url ? compilerOutputUrl : VfsUtil.urlToPath(compilerOutputUrl));
		path = StringUtil.replace(path, DotNetModuleExtension.CONFIGURATION, moduleRootManager.getCurrentLayerName());
		path = StringUtil.replace(path, DotNetModuleExtension.MODULE_NAME, module.getName());
		return path;
	}
}
