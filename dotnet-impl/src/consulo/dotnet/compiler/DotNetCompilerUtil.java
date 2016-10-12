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

package consulo.dotnet.compiler;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleExtensionWithSdkOrderEntry;
import com.intellij.openapi.roots.ModuleOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.annotations.Exported;
import consulo.dotnet.DotNetTarget;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.roots.orderEntry.DotNetLibraryOrderEntryImpl;
import consulo.dotnet.roots.orderEntry.DotNetRootPolicy;
import consulo.roots.types.BinariesOrderRootType;
import consulo.vfs.util.ArchiveVfsUtil;

/**
 * @author VISTALL
 * @since 02.01.14.
 */
public class DotNetCompilerUtil
{
	@Exported
	public static final Condition<OrderEntry> ACCEPT_ALL = Conditions.alwaysFalse();
	public static final Condition<OrderEntry> SKIP_STD_LIBRARIES = orderEntry -> orderEntry instanceof DotNetLibraryOrderEntryImpl;

	@NotNull
	public static Set<File> collectDependencies(@NotNull final Module module, @NotNull final DotNetTarget target, final boolean debugSymbol, @NotNull final Condition<OrderEntry> skipCondition)
	{
		Set<File> set = new HashSet<>();

		Set<Object> processed = new HashSet<>();

		processed.add(module);

		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		moduleRootManager.processOrder(new DotNetRootPolicy<Object>()
		{
			@Override
			public Object visitDotNetLibrary(DotNetLibraryOrderEntryImpl orderEntry, Object value)
			{
				if(skipCondition.value(orderEntry))
				{
					return null;
				}
				collectFromRoot(orderEntry);
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

				DotNetModuleExtension dependencyExtension = ModuleUtilCore.getExtension(depModule, DotNetModuleExtension.class);
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
