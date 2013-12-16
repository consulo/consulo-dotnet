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

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleLangExtension;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.EmptyValidityState;
import com.intellij.openapi.compiler.FileProcessingCompiler;
import com.intellij.openapi.compiler.SourceProcessingCompiler;
import com.intellij.openapi.compiler.ValidityState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.CommonProcessors;
import com.intellij.util.containers.MultiMap;
import lombok.val;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
@Logger
public class DotNetCompiler implements FileProcessingCompiler, SourceProcessingCompiler
{
	@NotNull
	@Override
	public String getDescription()
	{
		return "#";
	}

	@Override
	public boolean validateConfiguration(CompileScope compileScope)
	{
		for(Module module : compileScope.getAffectedModules())
		{
			DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
			if(extension != null && extension.getSdk() == null)
			{
				throw new IllegalArgumentException("SDK for module " + module.getName() + " cant be empty");
			}
		}
		return true;
	}

	@Override
	public void init(@NotNull CompilerManager compilerManager)
	{
	}

	@NotNull
	@Override
	public ProcessingItem[] getProcessingItems(final CompileContext compileContext)
	{
		List<ProcessingItem> itemList = new ArrayList<ProcessingItem>();
		for(val module : compileContext.getCompileScope().getAffectedModules())
		{
			val extension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
			if(extension == null)
			{
				continue;
			}

			CommonProcessors.CollectProcessor<VirtualFile> processor = new CommonProcessors.CollectProcessor<VirtualFile>()
			{
				@Override
				protected boolean accept(VirtualFile virtualFile)
				{
					return virtualFile.getFileType() == extension.getFileType() && ModuleUtilCore.findModuleForFile(virtualFile,
							compileContext.getProject()) == module;
				}
			};

			VfsUtil.processFilesRecursively(module.getModuleDir(), processor);

			Collection<VirtualFile> results = processor.getResults();
			if(results.isEmpty())
			{
				continue;
			}

			for(VirtualFile result : results)
			{
				itemList.add(new DotNetProcessingItem(result, module));
			}

		}

		return itemList.isEmpty() ? ProcessingItem.EMPTY_ARRAY : itemList.toArray(new ProcessingItem[itemList.size()]);
	}

	@Override
	public ProcessingItem[] process(CompileContext compileContext, ProcessingItem[] processingItems)
	{
		if(processingItems.length == 0)
		{
			return ProcessingItem.EMPTY_ARRAY;
		}

		MultiMap<Module, VirtualFile> map = new MultiMap<Module, VirtualFile>();
		for(ProcessingItem processingItem : processingItems)
		{
			DotNetProcessingItem item = (DotNetProcessingItem) processingItem;
			map.putValue(item.getModule(), item.getFile());
		}

		for(Map.Entry<Module, Collection<VirtualFile>> entry : map.entrySet())
		{
			Module module = entry.getKey();

			DotNetModuleLangExtension langDotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
			DotNetModuleExtension dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);

			DotNetCompilerOptionsBuilder builder = new DotNetCompilerOptionsBuilder(dotNetModuleExtension.getSdk());

			langDotNetModuleExtension.setupCompilerOptions(builder);

			try
			{
				GeneralCommandLine commandLine = builder.createCommandLine(module, entry.getValue());

				val process = commandLine.createProcess();
				val processHandler = new CapturingProcessHandler(process);

				ProcessOutput processOutput = processHandler.runProcess();
				for(String s : processOutput.getStdoutLines())
				{
					addMessage(compileContext, module, s);
				}
			}
			catch(Exception e)
			{
				LOGGER.error(e);
			}
		}

		return processingItems;
	}

	// src\Test.cs(7,42): error CS1002: ожидалась ;  [microsoft]
	// C:\Users\VISTALL\\ConsuloProjects\\untitled30\mono-test\\Program.cs(7,17): error CS0117: error description [mono]
	private static void addMessage(CompileContext compileContext, Module module, String line)
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
		else
		{
			compileContext.addMessage(CompilerMessageCategory.INFORMATION, line, null, -1, -1);
		}
	}

	@Override
	public ValidityState createValidityState(DataInput dataInput) throws IOException
	{
		return new EmptyValidityState();
	}
}