/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.nemerle.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerMessage;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerUtil;
import org.mustbe.consulo.dotnet.compiler.DotNetMacros;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayer;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.nemerle.module.extension.NemerleModuleExtension;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.val;

/**
 * @author VISTALL
 * @since 25.12.13.
 */
public class NemerleCompilerOptionsBuilder implements DotNetCompilerOptionsBuilder
{
	public NemerleCompilerOptionsBuilder()
	{
	}

	@Override
	public DotNetCompilerMessage convertToMessage(Module module, String line)
	{
		return new DotNetCompilerMessage(CompilerMessageCategory.INFORMATION, line, null, -1, -1);
	}

	@NotNull
	@Override
	public GeneralCommandLine createCommandLine(@NotNull Module module, @NotNull VirtualFile[] results, @NotNull String layerName, @NotNull
	MainConfigurationLayer dotNetLayer) throws IOException
	{
		Sdk sdk = ModuleUtilCore.getSdk(module, NemerleModuleExtension.class);
		assert sdk != null;

		DotNetModuleExtension<?> extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);

		assert extension != null;

		String target = null;
		switch(dotNetLayer.getTarget())
		{
			case EXECUTABLE:
				target = "exe";
				break;
			case LIBRARY:
				target = "library";
				break;
		}

		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(sdk.getHomePath() + "/ncc.exe");
		commandLine.setWorkDirectory(module.getModuleDirPath());

		List<String> arguments = new ArrayList<String>();
		arguments.add("-target:" + target);
		String outputFile = DotNetMacros.extract(module, layerName, dotNetLayer);
		arguments.add("-out:" + FileUtil.toSystemIndependentName(outputFile));

		val dependFiles = DotNetCompilerUtil.collectDependencies(module, layerName, dotNetLayer, true, false);
		if(!dependFiles.isEmpty())
		{
			arguments.add("-reference:" + StringUtils.join(dependFiles, ","));
		}

		File tempFile = FileUtil.createTempFile("consulo-nemerle", ".nn");
		for(String argument : arguments)
		{
			FileUtil.appendToFile(tempFile, argument + "\n");
		}

		for(VirtualFile result : results)
		{
			FileUtil.appendToFile(tempFile, FileUtil.toSystemDependentName(result.getPath()) + "\n");
		}

		FileUtil.createParentDirs(new File(outputFile));

		commandLine.addParameter("@" + tempFile.getAbsolutePath());
		commandLine.setRedirectErrorStream(true);
		return commandLine;
	}
}
