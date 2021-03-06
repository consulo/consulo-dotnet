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

import com.intellij.ide.macro.Macro;
import com.intellij.ide.macro.MacroManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import consulo.compiler.ModuleCompilerPathsManager;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.dotnet.module.macro.TargetFileExtensionMacro;
import consulo.roots.impl.ProductionContentFolderTypeProvider;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * @author VISTALL
 * @since 27.11.13.
 */
public class DotNetMacroUtil
{
	@Nonnull
	public static DataContext createContext(@Nonnull Module module, boolean debugSymbols)
	{
		SimpleDataContext.Builder builder = SimpleDataContext.builder();
		builder = builder.add(CommonDataKeys.PROJECT, module.getProject());
		builder = builder.add(LangDataKeys.MODULE, module);
		if(debugSymbols)
		{
			builder = builder.add(TargetFileExtensionMacro.DEBUG_SYMBOLS, Boolean.TRUE);
		}
		return builder.build();
	}

	@Nonnull
	public static String expandOutputFile(@Nonnull DotNetRunModuleExtension<?> extension)
	{
		return expandOutputFile(extension, false);
	}

	@Nonnull
	public static String expandOutputFile(@Nonnull DotNetRunModuleExtension<?> extension, boolean debugSymbols)
	{
		String outputDir = FileUtil.toSystemDependentName(extension.getOutputDir());
		if(StringUtil.isEmpty(outputDir))
		{
			String url = ModuleCompilerPathsManager.getInstance(extension.getModule()).getCompilerOutputUrl(ProductionContentFolderTypeProvider.getInstance());
			assert url != null;
			outputDir = FileUtil.toSystemDependentName(VfsUtil.urlToPath(url));
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

	@Nonnull
	public static String expandOutputDir(@Nonnull DotNetRunModuleExtension<?> extension)
	{
		String outputDir = FileUtil.toSystemDependentName(extension.getOutputDir());
		if(StringUtil.isEmpty(outputDir))
		{
			String url = ModuleCompilerPathsManager.getInstance(extension.getModule()).getCompilerOutputUrl(ProductionContentFolderTypeProvider.getInstance());
			assert url != null;
			outputDir = FileUtil.toSystemDependentName(VfsUtil.urlToPath(url));
		}

		return expand(extension.getModule(), outputDir, false);
	}

	@Nonnull
	public static String expand(@Nonnull Module module, @Nonnull String path, boolean debugSymbols)
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
