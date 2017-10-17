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

import gnu.trove.THashMap;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import com.intellij.ide.macro.Macro;
import com.intellij.ide.macro.MacroManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.module.macro.TargetFileExtensionMacro;

/**
 * @author VISTALL
 * @since 27.11.13.
 */
public class DotNetMacroUtil
{
	@NotNull
	public static DataContext createContext(@NotNull Module module, boolean debugSymbols)
	{
		Map<Key, Object> map = new THashMap<>();
		map.put(CommonDataKeys.PROJECT, module.getProject());
		map.put(LangDataKeys.MODULE, module);
		if(debugSymbols)
		{
			map.put(TargetFileExtensionMacro.DEBUG_SYMBOLS, Boolean.TRUE);
		}
		return SimpleDataContext.getSimpleContext(map, null);
	}

	@NotNull
	public static String expandOutputFile(@NotNull DotNetModuleExtension<?> extension)
	{
		return expandOutputFile(extension, false);
	}

	@NotNull
	public static String expandOutputFile(@NotNull DotNetModuleExtension<?> extension, boolean debugSymbols)
	{
		return expand(extension.getModule(), extension.getOutputDir() + "/" + extension.getFileName(), debugSymbols);
	}

	@NotNull
	public static String expandOutputDir(@NotNull DotNetModuleExtension<?> extension)
	{
		return expand(extension.getModule(), extension.getOutputDir(), false);
	}

	@NotNull
	public static String expand(@NotNull Module module, @NotNull String path, boolean debugSymbols)
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
