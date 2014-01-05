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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import lombok.val;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class MSBaseDotNetCompilerOptionsBuilder implements  DotNetCompilerOptionsBuilder
{
	private String myExecutable;
	private Sdk mySdk;

	private final List<String> myArguments = new ArrayList<String>();

	public MSBaseDotNetCompilerOptionsBuilder(Sdk sdk)
	{
		mySdk = sdk;
	}

	public MSBaseDotNetCompilerOptionsBuilder addArgument(@NotNull String arg)
	{
		myArguments.add(arg + "\n");
		return this;
	}

	// src\Test.cs(7,42): error CS1002: ожидалась ;  [microsoft]
	// C:\Users\VISTALL\\ConsuloProjects\\untitled30\mono-test\\Program.cs(7,17): error CS0117: error description [mono]
	@Override
	public void addMessage(CompileContext compileContext, Module module, String line)
	{
		String[] split = line.split(": ");
		if(split.length == 3)
		{
			String fileAndPosition = split[0].trim();
			String idAndType = split[1].trim();
			String message = split[2].trim();

			String file = fileAndPosition.substring(0, fileAndPosition.lastIndexOf("("));
			String position = fileAndPosition.substring(fileAndPosition.lastIndexOf("(") + 1, fileAndPosition.length() - 1);
			String[] lineAndColumn = position.split(",");

			String[] idAndTypeArray = idAndType.split(" ");
			CompilerMessageCategory category = CompilerMessageCategory.INFORMATION;
			if(idAndTypeArray[0].equals("error"))
			{
				category = CompilerMessageCategory.ERROR;
			}
			else if(idAndTypeArray[0].equals("warning"))
			{
				category = CompilerMessageCategory.WARNING;
			}

			String fileUrl = FileUtil.toSystemIndependentName(file);
			if(!FileUtil.isAbsolute(fileUrl))
			{
				fileUrl = module.getModuleDirUrl() + "/" + fileUrl;
			}
			else
			{
				fileUrl = VirtualFileManager.constructUrl(StandardFileSystems.FILE_PROTOCOL, fileUrl);
			}

			compileContext.addMessage(category, message + " (" + idAndTypeArray[1] + ")", fileUrl, Integer.parseInt(lineAndColumn[0]),
					Integer.parseInt(lineAndColumn[1]));
		}
		else if(split.length == 2)
		{
			String idAndType = split[0].trim();
			String message = split[1].trim();

			//C:\Users\VISTALL\ConsuloProjects\\untitled30\\mono-test\\Program.cs(5,9): (Location of the symbol related to previous error)
			if(message.startsWith("(")) // only with ?
			{
				String file = idAndType.substring(0, idAndType.lastIndexOf("("));
				String position = idAndType.substring(idAndType.lastIndexOf("(") + 1, idAndType.length() - 1);
				String[] lineAndColumn = position.split(",");

				String fileUrl = FileUtil.toSystemIndependentName(file);
				if(!FileUtil.isAbsolute(fileUrl))
				{
					fileUrl = module.getModuleDirUrl() + "/" + fileUrl;
				}
				else
				{
					fileUrl = VirtualFileManager.constructUrl(StandardFileSystems.FILE_PROTOCOL, fileUrl);
				}

				message = message.substring(1, message.length());
				message = message.substring(0, message.length() - 1);
				compileContext.addMessage(CompilerMessageCategory.INFORMATION, message, fileUrl, Integer.parseInt(lineAndColumn[0]),
						Integer.parseInt(lineAndColumn[1]));
			}
			else
			{
				String[] idAndTypeArray = idAndType.split(" ");
				CompilerMessageCategory category = CompilerMessageCategory.INFORMATION;
				if(idAndTypeArray[0].equals("error"))
				{
					category = CompilerMessageCategory.ERROR;
				}
				else if(idAndTypeArray[0].equals("warning"))
				{
					category = CompilerMessageCategory.WARNING;
				}

				compileContext.addMessage(category, message + " (" + idAndTypeArray[1] + ")", null, -1, -1);
			}
		}
		else
		{
			compileContext.addMessage(CompilerMessageCategory.INFORMATION, line, null, -1, -1);
		}
	}

	@Override
	@NotNull
	public GeneralCommandLine createCommandLine(@NotNull Module module, @NotNull VirtualFile[] results, boolean debug) throws IOException
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
		String outputFile = DotNetMacros.extract(module, debug, extension.getTarget());
		addArgument("/out:" + outputFile);

		val dependFiles = DotNetCompilerUtil.collectDependencies(module, debug, false, false);
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
