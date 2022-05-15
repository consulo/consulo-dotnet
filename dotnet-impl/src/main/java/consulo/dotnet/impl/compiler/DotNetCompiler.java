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

package consulo.dotnet.impl.compiler;

import consulo.compiler.CompileContext;
import consulo.compiler.CompilerMessageCategory;
import consulo.compiler.TranslatingCompiler;
import consulo.compiler.scope.CompileScope;
import consulo.dotnet.compiler.DotNetCompileFailedException;
import consulo.dotnet.compiler.DotNetCompilerMessage;
import consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.module.extension.DotNetModuleLangExtension;
import consulo.language.editor.wolfAnalyzer.Problem;
import consulo.language.editor.wolfAnalyzer.WolfTheProblemSolver;
import consulo.language.util.ModuleUtilCore;
import consulo.logging.Logger;
import consulo.module.Module;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.local.CapturingProcessHandler;
import consulo.process.local.ProcessOutput;
import consulo.project.Project;
import consulo.util.collection.Chunk;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetCompiler implements TranslatingCompiler
{
	public static final Logger LOGGER = Logger.getInstance(DotNetCompiler.class);

	@Nonnull
	@Override
	public String getDescription()
	{
		return "DotNetCompiler";
	}

	@Override
	public boolean validateConfiguration(CompileScope compileScope)
	{
		for(Module module : compileScope.getAffectedModules())
		{
			DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
			if(extension != null && extension.getSdk() == null && extension.isSupportCompilation())
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

		DotNetModuleExtension dotNetModuleExtension = ModuleUtilCore.getExtension(moduleForFile, DotNetModuleExtension.class);
		if(dotNetModuleExtension != null && !dotNetModuleExtension.isSupportCompilation())
		{
			return false;
		}
		DotNetModuleLangExtension extension = ModuleUtilCore.getExtension(moduleForFile, DotNetModuleLangExtension.class);
		return extension != null && virtualFile.getFileType() == extension.getFileType();
	}

	@Override
	public void compile(final CompileContext compileContext, Chunk<Module> moduleChunk, VirtualFile[] virtualFiles, OutputSink outputSink)
	{
		Module module = moduleChunk.getNodes().iterator().next();

		DotNetModuleLangExtension<?> langDotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
		DotNetModuleExtension<?> dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(dotNetModuleExtension == null || langDotNetModuleExtension == null)
		{
			return;
		}

		DotNetCompilerOptionsBuilder builder;
		try
		{
			builder = langDotNetModuleExtension.createCompilerOptionsBuilder();
		}
		catch(final DotNetCompileFailedException e)
		{
			compileContext.addMessage(CompilerMessageCategory.ERROR, e.getMessage(), null, -1, -1);
			return;
		}

		try
		{
			GeneralCommandLine commandLine = builder.createCommandLine(module, virtualFiles, dotNetModuleExtension);
			commandLine = commandLine.withCharset(StandardCharsets.UTF_8);

			CapturingProcessHandler processHandler = new CapturingProcessHandler(commandLine);

			ProcessOutput processOutput = processHandler.runProcess();
			for(String line : processOutput.getStdoutLines())
			{
				try
				{
					DotNetCompilerMessage m = builder.convertToMessage(module, line);
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
					DotNetCompiler.LOGGER.error("Message with : " + line + " cant be parsed", e);
					compileContext.addMessage(CompilerMessageCategory.ERROR, line, null, -1, -1);
				}
			}
			for(String line : processOutput.getStderrLines())
			{
				compileContext.addMessage(CompilerMessageCategory.ERROR, line, null, -1, -1);
			}
			if(processOutput.getExitCode() != 0)
			{
				compileContext.addMessage(CompilerMessageCategory.ERROR, "Exit code: " + processOutput.getExitCode(), null, -1, -1);
			}
		}
		catch(DotNetCompileFailedException e)
		{
			compileContext.addMessage(CompilerMessageCategory.ERROR, e.getMessage(), null, -1, -1);
		}
		catch(Exception e)
		{
			DotNetCompiler.LOGGER.error(e);
		}
	}

	@Nonnull
	@Override
	public FileType[] getInputFileTypes()
	{
		return FileType.EMPTY_ARRAY;
	}

	@Nonnull
	@Override
	public FileType[] getOutputFileTypes()
	{
		return FileType.EMPTY_ARRAY;
	}
}