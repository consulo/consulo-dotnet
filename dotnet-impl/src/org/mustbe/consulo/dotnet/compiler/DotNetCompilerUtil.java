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

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayer;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleExtensionWithSdkOrderEntry;
import com.intellij.openapi.roots.ModuleOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.RootPolicy;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 02.01.14.
 */
public class DotNetCompilerUtil
{
	@NotNull
	public static Set<String> collectDependencies(@NotNull Module module, @NotNull final String layerName, @NotNull final MainConfigurationLayer p,
			final boolean toSystemInDepend, final boolean forDependCopy)
	{
		val list = new HashSet<String>();
		val processed = new HashSet<Module>();

		processed.add(module);

		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		moduleRootManager.processOrder(new RootPolicy<Object>()
		{
			@Override
			public Object visitModuleJdkOrderEntry(ModuleExtensionWithSdkOrderEntry jdkOrderEntry, Object value)
			{
				if(!forDependCopy)
				{
					collectFromRoot(jdkOrderEntry);
				}
				return null;
			}

			@Override
			public Object visitLibraryOrderEntry(LibraryOrderEntry libraryOrderEntry, Object value)
			{
				collectFromRoot(libraryOrderEntry);
				return null;
			}

			private void collectFromRoot(OrderEntry orderEntry)
			{
				for(VirtualFile virtualFile : orderEntry.getFiles(OrderRootType.CLASSES))
				{
					VirtualFile virtualFileForJar = ArchiveVfsUtil.getVirtualFileForArchive(virtualFile);
					String path;
					if(virtualFileForJar != null)
					{
						path = virtualFileForJar.getPath();
					}
					else
					{
						path = virtualFile.getPath();
					}

					add(list, toSystemInDepend, path);
				}
			}

			@Override
			public Object visitModuleOrderEntry(ModuleOrderEntry moduleOrderEntry, Object value)
			{
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
				if(dependencyExtension != null)
				{
					String depCurrentLayerName = dependencyExtension.getCurrentLayerName();
					MainConfigurationLayer depCurrentLayer = (MainConfigurationLayer) dependencyExtension.getCurrentLayer();
					String extract = DotNetMacros.extract(depModule, depCurrentLayerName, depCurrentLayer);
					add(list, toSystemInDepend, extract);
					if(forDependCopy)
					{
						Set<String> strings = collectDependencies(depModule, depCurrentLayerName, depCurrentLayer, toSystemInDepend, true);
						list.addAll(strings);
					}
				}

				return null;
			}
		}, null);

		return list;
	}

	private static void add(Set<String> list, boolean toSystemInDepend, String extract)
	{
		if(toSystemInDepend)
		{
			extract = FileUtil.toSystemIndependentName(extract);
		}

		list.add(StringUtil.QUOTER.fun(extract));
	}
}
