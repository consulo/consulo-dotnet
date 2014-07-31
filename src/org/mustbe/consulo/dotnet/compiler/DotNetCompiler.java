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

import java.nio.charset.Charset;
import java.util.Arrays;

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
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.problems.Problem;
import com.intellij.problems.WolfTheProblemSolver;
import com.intellij.util.Chunk;
import lombok.val;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
@Logger
public class DotNetCompiler implements TranslatingCompiler
{
	@NotNull
	@Override
	public String getDescription()
	{
		return "DotNetCompiler";
	}

	@Override
	public void init(@NotNull CompilerManager compilerManager)
	{
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
	public boolean isCompilableFile(VirtualFile virtualFile, CompileContext compileContext)
	{
		if(virtualFile == null)
		{
			return false;
		}
		Project project = compileContext.getProject();
		if(project == null)
		{
			return false;
		}
		Module moduleForFile = ModuleUtilCore.findModuleForFile(virtualFile, project);
		if(moduleForFile == null)
		{
			return false;
		}
		DotNetModuleLangExtension extension = ModuleUtilCore.getExtension(moduleForFile, DotNetModuleLangExtension.class);
		return extension != null && virtualFile.getFileType() == extension.getFileType();
	}

	@Override
	public void compile(CompileContext compileContext, Chunk<Module> moduleChunk, VirtualFile[] virtualFiles, OutputSink outputSink)
	{
		Module module = moduleChunk.getNodes().iterator().next();

		DotNetModuleLangExtension<?> langDotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
		DotNetModuleExtension<?> dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);

		assert dotNetModuleExtension != null;
		assert langDotNetModuleExtension != null;

		DotNetCompilerOptionsBuilder builder = langDotNetModuleExtension.createCompilerOptionsBuilder();

		try
		{
			GeneralCommandLine commandLine = builder.createCommandLine(module, virtualFiles, dotNetModuleExtension);

			val process = commandLine.createProcess();
			val processHandler = new CapturingProcessHandler(process, Charset.forName("UTF-8"));

			ProcessOutput processOutput = processHandler.runProcess();
			for(String s : processOutput.getStdoutLines())
			{
				try
				{
					DotNetCompilerMessage m = builder.convertToMessage(module, s);
					if(m == null)
					{
						continue;
					}

					String fileUrl = m.getFileUrl();
					VirtualFile virtualFile = fileUrl == null ? null : VirtualFileManager.getInstance().findFileByUrl(fileUrl);
					if(virtualFile != null && m.getCategory() == CompilerMessageCategory.ERROR)
					{
						Problem problem = WolfTheProblemSolver.getInstance(module.getProject()).convertToProblem(virtualFile, m.getLine(),
								m.getColumn(), new String[]{m.getMessage()});
						if(problem != null)
						{
							WolfTheProblemSolver.getInstance(module.getProject()).reportProblems(virtualFile, Arrays.<Problem>asList(problem));
						}
					}

					compileContext.addMessage(m.getCategory(), m.getMessage(), m.getFileUrl(), m.getLine(), m.getColumn());
				}
				catch(Exception e)
				{
					LOGGER.error("Message with : " + s + " cant be parsed", e);
					compileContext.addMessage(CompilerMessageCategory.ERROR, s, null, -1, -1);
				}
			}
			for(String s : processOutput.getStderrLines())
			{
				compileContext.addMessage(CompilerMessageCategory.ERROR, s, null, -1, -1);
			}
			compileContext.addMessage(CompilerMessageCategory.INFORMATION, "Exit code: " + processOutput.getExitCode(), null, -1, -1);
		}
		catch(Exception e)
		{
			LOGGER.error(e);
		}
	}

	@NotNull
	@Override
	public FileType[] getInputFileTypes()
	{
		return FileType.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public FileType[] getOutputFileTypes()
	{
		return FileType.EMPTY_ARRAY;
	}
}