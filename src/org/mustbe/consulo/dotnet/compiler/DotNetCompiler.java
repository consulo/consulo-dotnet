package org.mustbe.consulo.dotnet.compiler;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.CommonProcessors;
import com.intellij.util.containers.MultiMap;
import lombok.val;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
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
	public ProcessingItem[] getProcessingItems(CompileContext compileContext)
	{
		List<ProcessingItem> itemList = new ArrayList<ProcessingItem>();
		for(Module module : ModuleManager.getInstance(compileContext.getProject()).getModules())
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
					return virtualFile.getFileType() == extension.getFileType();
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

			DotNetCompilerOptionsBuilder builder = new DotNetCompilerOptionsBuilder(compileContext.getProject(), dotNetModuleExtension.getSdk());

			langDotNetModuleExtension.setupCompilerOptions(builder);

			try
			{
				GeneralCommandLine commandLine = builder.createCommandLine(module, entry.getValue());

				val process = commandLine.createProcess();
				val processHandler = new CapturingProcessHandler(process);

				// src\Test.cs(7,42): error CS1002: ожидалась ;
				ProcessOutput processOutput = processHandler.runProcess();
				for(String s : processOutput.getStdoutLines())
				{
					compileContext.addMessage(CompilerMessageCategory.INFORMATION, s, null, -1 ,-1);
				}

				for(String s : processOutput.getStderrLines())
				{
					compileContext.addMessage(CompilerMessageCategory.ERROR, s, null, -1 ,-1);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return processingItems;
	}

	@Override
	public ValidityState createValidityState(DataInput dataInput) throws IOException
	{
		return new EmptyValidityState();
	}
}