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

import consulo.application.ReadAction;
import consulo.compiler.*;
import consulo.compiler.scope.CompileScope;
import consulo.dotnet.DotNetTarget;
import consulo.dotnet.compiler.DotNetMacroUtil;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.module.extension.DotNetModuleLangExtension;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import consulo.ide.impl.idea.openapi.util.io.FileUtil;
import consulo.language.util.ModuleUtilCore;
import consulo.logging.Logger;
import consulo.module.Module;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetDependencyCopier implements FileProcessingCompiler, PackagingCompiler
{
	public static final Logger LOGGER = Logger.getInstance(DotNetDependencyCopier.class);

	@Nonnull
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

	@Nonnull
	@Override
	public ProcessingItem[] getProcessingItems(final CompileContext compileContext)
	{
		List<ProcessingItem> itemList = new ArrayList<>();
		for(Module module : compileContext.getCompileScope().getAffectedModules())
		{
			DotNetModuleLangExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
			if(extension == null)
			{
				continue;
			}

			DotNetSimpleModuleExtension dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetSimpleModuleExtension.class);
			assert dotNetModuleExtension != null;

			if(!dotNetModuleExtension.isSupportCompilation() || !(dotNetModuleExtension instanceof DotNetRunModuleExtension))
			{
				continue;
			}

			Set<File> list = ReadAction.compute(() ->
			{
				Set<File> files = DotNetCompilerUtil.collectDependencies(module, DotNetTarget.LIBRARY, true, DotNetCompilerUtil.SKIP_STD_LIBRARIES);
				files.addAll(DotNetCompilerUtil.collectDependencies(module, DotNetTarget.NET_MODULE, true, DotNetCompilerUtil.SKIP_STD_LIBRARIES));

				for(DotNetDependencyCopierExtension copierExtension : DotNetDependencyCopierExtension.EP_NAME.getExtensionList())
				{
					files.addAll(copierExtension.collectDependencies(module));
				}
				return files;
			});

			for(File file : list)
			{
				if(!file.exists())
				{
					continue;
				}
				itemList.add(new DotNetProcessingItem(file, (DotNetRunModuleExtension<?>) dotNetModuleExtension));
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

		List<ProcessingItem> items = new ArrayList<>(processingItems.length);
		for(ProcessingItem processingItem : processingItems)
		{
			DotNetProcessingItem dotNetProcessingItem = (DotNetProcessingItem) processingItem;
			String moduleOutputDir = DotNetMacroUtil.expandOutputDir(dotNetProcessingItem.getExtension());

			File copyFile = new File(moduleOutputDir, processingItem.getFile().getName());

			File file = processingItem.getFile();

			try
			{
				FileUtil.copy(file, copyFile);

				items.add(new DotNetProcessingItem(copyFile, null));
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
		long l = dataInput.readLong();
		return new TimestampValidityState(l);
	}

	@Override
	public void processOutdatedItem(CompileContext compileContext, File s, @Nullable ValidityState validityState)
	{
	}
}