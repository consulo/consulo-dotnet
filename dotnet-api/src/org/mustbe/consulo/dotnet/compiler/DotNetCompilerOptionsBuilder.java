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

package org.mustbe.consulo.dotnet.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import lombok.val;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetCompilerOptionsBuilder
{
	private String myExecutable;
	private Sdk mySdk;

	private final List<String> myArguments = new ArrayList<String>();

	public DotNetCompilerOptionsBuilder(Sdk sdk)
	{
		mySdk = sdk;
	}

	public DotNetCompilerOptionsBuilder addArgument(@NotNull String arg)
	{
		myArguments.add(arg + "\n");
		return this;
	}

	public GeneralCommandLine createCommandLine(Module module, VirtualFile[] results) throws IOException
	{
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);

		assert extension != null;
		String target = null;
		switch(extension.getTarget())
		{
			case EXECUTABLE:
				target = "exe";
				break;
			case LIBRARY:
				target = "library";
				break;
		}

		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(myExecutable);
		commandLine.setWorkDirectory(module.getModuleDirPath());

		addArgument("/target:" + target);
		String outputFile = DotNetMacros.extract(module, false, extension.getTarget());
		addArgument("/out:" + outputFile);

		val dependFiles = new SmartList<String>();
		Module[] dependencies = ModuleRootManager.getInstance(module).getDependencies();
		for(Module dependency : dependencies)
		{
			DotNetModuleExtension dependencyExtension = ModuleUtilCore.getExtension(dependency, DotNetModuleExtension.class);
			if(dependencyExtension != null)
			{
				dependFiles.add(DotNetMacros.extract(dependency, false, dependencyExtension.getTarget()));
			}
		}

		if(!dependFiles.isEmpty())
		{
			addArgument("/reference:" + StringUtils.join(dependFiles, ","));
		}

		File tempFile = FileUtil.createTempFile("consulo-dotnet-rsp", ".rsp");
		for(String argument : myArguments)
		{
			FileUtil.appendToFile(tempFile, argument);
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

	public void setExecutableFromSdk(String executableFromSdk)
	{
		myExecutable = mySdk.getHomePath() + "\\" + executableFromSdk;
	}
}
