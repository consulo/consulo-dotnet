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

package org.mustbe.consulo.dotnet.sdk;

import java.io.File;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.types.BinariesOrderRootType;
import com.intellij.openapi.roots.types.DocumentationOrderRootType;
import com.intellij.openapi.roots.types.SourcesOrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;

/**
 * @author VISTALL
 * @since 20.12.13.
 */
public abstract class DotNetSdkType extends SdkType
{
	public static final String[] ORDER_DLLS = {
			"mscorlib.dll",
			"System.dll",
			"System.Core.dll"
	};

	public DotNetSdkType(@NonNls String name)
	{
		super(name);
	}

	@NotNull
	public File getLoaderFile()
	{
		return getLoaderFile(getClass());
	}

	@NotNull
	protected static File getLoaderFile(Class<?> clazz)
	{
		PluginClassLoader classLoader = (PluginClassLoader) clazz.getClassLoader();
		IdeaPluginDescriptor plugin = PluginManager.getPlugin(classLoader.getPluginId());
		assert plugin != null;
		return new File(new File(plugin.getPath(), "loader"), "loader.exe");
	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == BinariesOrderRootType.getInstance() || type == SourcesOrderRootType.getInstance() || type == DocumentationOrderRootType
				.getInstance();
	}

	@Override
	public void setupSdkPaths(Sdk sdk)
	{
		SdkModificator sdkModificator = sdk.getSdkModificator();

		VirtualFile homeDirectory = sdk.getHomeDirectory();
		assert homeDirectory != null;

		for(String orderDll : ORDER_DLLS)
		{
			VirtualFile dllVirtualFile = homeDirectory.findFileByRelativePath(orderDll);
			if(dllVirtualFile == null)
			{
				continue;
			}

			VirtualFile archiveRootForLocalFile = ArchiveVfsUtil.getArchiveRootForLocalFile(dllVirtualFile);
			if(archiveRootForLocalFile == null)
			{
				continue;
			}
			sdkModificator.addRoot(archiveRootForLocalFile, BinariesOrderRootType.getInstance());

			VirtualFile docFile = homeDirectory.findChild(dllVirtualFile.getNameWithoutExtension() + ".xml");
			if(docFile != null)
			{
				sdkModificator.addRoot(docFile, DocumentationOrderRootType.getInstance());
			}
		}

		postSetupSdkPaths(sdk, sdkModificator);

		sdkModificator.commitChanges();
	}

	protected void postSetupSdkPaths(Sdk sdk, @NotNull SdkModificator modificator)
	{

	}
}
