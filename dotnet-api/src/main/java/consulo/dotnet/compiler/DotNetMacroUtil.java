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

import consulo.compiler.ModuleCompilerPathsManager;
import consulo.dataContext.DataContext;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.dotnet.module.macro.TargetFileExtensionMacro;
import consulo.language.content.ProductionContentFolderTypeProvider;
import consulo.module.Module;
import consulo.pathMacro.Macro;
import consulo.pathMacro.MacroManager;
import consulo.project.Project;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;

import java.io.File;

/**
 * @author VISTALL
 * @since 27.11.13.
 */
public class DotNetMacroUtil
{
	public static DataContext createContext(Module module, boolean debugSymbols)
	{
		DataContext.Builder builder = DataContext.builder();
		builder = builder.add(Project.KEY, module.getProject());
		builder = builder.add(Module.KEY, module);
		if(debugSymbols)
		{
			builder = builder.add(TargetFileExtensionMacro.DEBUG_SYMBOLS, Boolean.TRUE);
		}
		return builder.build();
	}

	public static String expandOutputFile(DotNetRunModuleExtension<?> extension)
	{
		return expandOutputFile(extension, false);
	}

	public static String expandOutputFile(DotNetRunModuleExtension<?> extension, boolean debugSymbols)
	{
		String outputDir = FileUtil.toSystemDependentName(extension.getOutputDir());
		if(StringUtil.isEmpty(outputDir))
		{
			String url = ModuleCompilerPathsManager.getInstance(extension.getModule()).getCompilerOutputUrl(ProductionContentFolderTypeProvider.getInstance());
			assert url != null;
			outputDir = FileUtil.toSystemDependentName(VirtualFileUtil.urlToPath(url));
		}

		if(outputDir.charAt(outputDir.length() - 1) == File.separatorChar)
		{
			return expand(extension.getModule(), outputDir + extension.getFileName(), debugSymbols);
		}
		else
		{
			return expand(extension.getModule(), outputDir + File.separatorChar + extension.getFileName(), debugSymbols);
		}
	}

	public static String expandOutputDir(DotNetRunModuleExtension<?> extension)
	{
		String outputDir = FileUtil.toSystemDependentName(extension.getOutputDir());
		if(StringUtil.isEmpty(outputDir))
		{
			String url = ModuleCompilerPathsManager.getInstance(extension.getModule()).getCompilerOutputUrl(ProductionContentFolderTypeProvider.getInstance());
			assert url != null;
			outputDir = FileUtil.toSystemDependentName(VirtualFileUtil.urlToPath(url));
		}

		return expand(extension.getModule(), outputDir, false);
	}

	public static String expand(Module module, String path, boolean debugSymbols)
	{
		String newPath;
		try
		{
			newPath = MacroManager.getInstance().expandSilentMarcos(path, true, createContext(module, debugSymbols));
			return FileUtil.toSystemDependentName(newPath);
		}
		catch(Macro.ExecutionCancelledException e)
		{
			return path;
		}
	}
}
