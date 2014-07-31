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

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleLangExtension;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.compiler.EmptyValidityState;
import com.intellij.openapi.compiler.FileProcessingCompiler;
import com.intellij.openapi.compiler.PackagingCompiler;
import com.intellij.openapi.compiler.ValidityState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import lombok.val;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
@Logger
public class DotNetDependencyCopier implements FileProcessingCompiler, PackagingCompiler
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
				throw new IllegalArgumentException("Sdk for module " + module.getName() + " cant be empty");
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

			val dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
			assert dotNetModuleExtension != null;

			val r = new ReadAction<Set<File>>()
			{
				@Override
				protected void run(Result<Set<File>> listResult) throws Throwable
				{
					listResult.setResult(DotNetCompilerUtil.collectDependencies(module, true, false));
				}
			}.execute();

			val list = r.getResultObject();

			for(val file : list)
			{
				VirtualFile fileByIoFile = VfsUtil.findFileByIoFile(file, true);
				if(fileByIoFile == null)
				{
					continue;
				}
				itemList.add(new DotNetProcessingItem(fileByIoFile, module, dotNetModuleExtension));
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

		List<ProcessingItem> items = new ArrayList<ProcessingItem>(processingItems.length);
		for(ProcessingItem processingItem : processingItems)
		{
			DotNetProcessingItem dotNetProcessingItem = (DotNetProcessingItem) processingItem;
			String moduleOutputDirUrl = DotNetMacros.getModuleOutputDirUrl(dotNetProcessingItem.getTarget(),
					dotNetProcessingItem.getExtension());

			File copyFile = new File(VirtualFileManager.extractPath(moduleOutputDirUrl), processingItem.getFile().getName());

			File file = VfsUtilCore.virtualToIoFile(processingItem.getFile());

			try
			{
				FileUtil.copy(file, copyFile);

				items.add(new DotNetProcessingItem(VfsUtil.findFileByIoFile(copyFile, true), null, null));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return items.toArray(new ProcessingItem[items.size()]);
	}

	@Override
	public ValidityState createValidityState(DataInput dataInput) throws IOException
	{
		return new EmptyValidityState();
	}

	@Override
	public void processOutdatedItem(CompileContext compileContext, String s, @Nullable ValidityState validityState)
	{
	}
}