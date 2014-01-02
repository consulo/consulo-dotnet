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
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.RootPolicy;
import com.intellij.openapi.util.io.FileUtil;
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
	public static Set<String> collectDependencies(@NotNull Module module, final boolean debug, final boolean toSystemInDepend,
			final boolean goInside)
	{
		val list = new HashSet<String>();
		val processed = new HashSet<Module>();

		processed.add(module);

		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		moduleRootManager.processOrder(new RootPolicy<Object>()
		{
			@Override
			public Object visitLibraryOrderEntry(LibraryOrderEntry libraryOrderEntry, Object value)
			{
				for(VirtualFile virtualFile : libraryOrderEntry.getFiles(OrderRootType.CLASSES))
				{
					VirtualFile virtualFileForJar = ArchiveVfsUtil.getVirtualFileForJar(virtualFile);
					String path = null;
					if(virtualFileForJar != null)
					{
						path = virtualFileForJar.getPath();
					}
					else
					{
						path = virtualFile.getPath();
					}

					if(toSystemInDepend)
					{
						list.add(FileUtil.toSystemIndependentName(path));
					}
					else
					{
						list.add(path);
					}
				}
				return null;
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
					String extract = DotNetMacros.extract(depModule, debug, dependencyExtension.getTarget());
					if(toSystemInDepend)
					{
						list.add(FileUtil.toSystemIndependentName(extract));
					}
					else
					{
						list.add(extract);
					}
				}

				if(goInside)
				{
					Set<String> strings = collectDependencies(depModule, debug, toSystemInDepend, true);
					list.addAll(strings);
				}
				return null;
			}
		}, null);

		return list;
	}
}
