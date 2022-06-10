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

import consulo.annotation.UsedInPlugin;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.bundle.Sdk;
import consulo.content.library.Library;
import consulo.dotnet.DotNetTarget;
import consulo.dotnet.compiler.DotNetMacroUtil;
import consulo.dotnet.impl.roots.orderEntry.DotNetLibraryOrderEntryModel;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.ide.impl.idea.openapi.vfs.VfsUtil;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.orderEntry.*;
import consulo.util.lang.Comparing;
import consulo.util.lang.function.Condition;
import consulo.util.lang.function.Conditions;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.archive.ArchiveVfsUtil;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author VISTALL
 * @since 02.01.14.
 */
public class DotNetCompilerUtil
{
	@UsedInPlugin
	public static final Condition<OrderEntry> ACCEPT_ALL = Conditions.alwaysFalse();
	public static final Condition<OrderEntry> SKIP_STD_LIBRARIES = orderEntry -> orderEntry instanceof CustomOrderEntry && ((CustomOrderEntry) orderEntry).getModel() instanceof
			DotNetLibraryOrderEntryModel;

	@Nonnull
	public static Set<File> collectDependencies(@Nonnull final Module module, @Nonnull final DotNetTarget target, final boolean debugSymbol, @Nonnull final Condition<OrderEntry> skipCondition)
	{
		Set<File> set = new HashSet<>();

		Set<Object> processed = new HashSet<>();

		processed.add(module);

		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		moduleRootManager.processOrder(new RootPolicy<>()
		{
			@Override
			public Object visitCustomOrderEntry(CustomOrderEntry orderEntry, Object value)
			{
				CustomOrderEntryModel model = orderEntry.getModel();
				if(model instanceof DotNetLibraryOrderEntryModel)
				{
					if(skipCondition.value(orderEntry))
					{
						return null;
					}
					collectFromRoot(orderEntry);
				}

				return null;
			}

			@Override
			public Object visitLibraryOrderEntry(LibraryOrderEntry libraryOrderEntry, Object value)
			{
				if(skipCondition.value(libraryOrderEntry))
				{
					return null;
				}

				Library library = libraryOrderEntry.getLibrary();
				if(library == null || processed.contains(library))
				{
					return null;
				}

				processed.add(library);

				collectFromRoot(libraryOrderEntry);
				return null;
			}

			@Override
			public Object visitModuleExtensionSdkOrderEntry(ModuleExtensionWithSdkOrderEntry orderEntry, Object value)
			{
				if(skipCondition.value(orderEntry))
				{
					return null;
				}

				Sdk sdk = orderEntry.getSdk();
				if(sdk == null || processed.contains(sdk))
				{
					return null;
				}

				processed.add(sdk);

				collectFromRoot(orderEntry);
				return null;
			}

			private void collectFromRoot(OrderEntry orderEntry)
			{
				for(VirtualFile virtualFile : orderEntry.getFiles(BinariesOrderRootType.getInstance()))
				{
					VirtualFile virtualFileForArchive = ArchiveVfsUtil.getVirtualFileForArchive(virtualFile);
					if(virtualFileForArchive != null)
					{
						if(Comparing.equal(virtualFileForArchive.getExtension(), target.getExtension()))
						{
							set.add(VfsUtil.virtualToIoFile(virtualFileForArchive));
						}
					}
				}
			}

			@Override
			public Object visitModuleOrderEntry(ModuleOrderEntry moduleOrderEntry, Object value)
			{
				if(skipCondition.value(moduleOrderEntry))
				{
					return null;
				}

				Module depModule = moduleOrderEntry.getModule();
				if(depModule == null)
				{
					return null;
				}

				if(processed.contains(depModule))
				{
					return null;
				}

				processed.add(depModule);

				DotNetRunModuleExtension dependencyExtension = ModuleUtilCore.getExtension(depModule, DotNetRunModuleExtension.class);
				if(dependencyExtension != null && dependencyExtension.getTarget() == target)
				{
					set.add(new File(DotNetMacroUtil.expandOutputFile(dependencyExtension)));
					if(debugSymbol)
					{
						set.add(new File(DotNetMacroUtil.expandOutputFile(dependencyExtension, true)));
					}

					set.addAll(collectDependencies(depModule, target, false, skipCondition));
				}

				return null;
			}
		}, null);

		return set;
	}

}
