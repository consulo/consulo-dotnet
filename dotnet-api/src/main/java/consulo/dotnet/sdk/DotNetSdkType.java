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

package consulo.dotnet.sdk;

import consulo.container.plugin.PluginManager;
import consulo.content.OrderRootType;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkModificator;
import consulo.content.bundle.SdkType;
import consulo.dotnet.externalAttributes.ExternalAttributesRootOrderType;
import consulo.dotnet.module.extension.BaseDotNetSimpleModuleExtension;
import consulo.util.io.FileUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * @author VISTALL
 * @since 20.12.13.
 */
public abstract class DotNetSdkType extends SdkType
{
	public DotNetSdkType(@Nonnull String name)
	{
		super(name);
	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == ExternalAttributesRootOrderType.getInstance();
	}

	@Override
	public void setupSdkPaths(@Nonnull Sdk sdk)
	{
		SdkModificator sdkModificator = sdk.getSdkModificator();

		File dir = new File(PluginManager.getPluginPath(BaseDotNetSimpleModuleExtension.class), "externalAttributes");

		FileUtil.visitFiles(dir, file ->
		{
			if(file.isDirectory())
			{
				return true;
			}

			if(file.getName().endsWith(".xml"))
			{
				sdkModificator.addRoot(VirtualFileUtil.pathToUrl(file.getPath()), ExternalAttributesRootOrderType.getInstance());
			}
			return true;
		});

		sdkModificator.commitChanges();
	}

	@Nonnull
	public File getLoaderFile(@Nonnull Sdk sdk)
	{
		return getLoaderFile(getClass(), "loader.exe");
	}

	@Nonnull
	protected static File getLoaderFile(Class<?> clazz, String fileName)
	{
		return new File(new File(PluginManager.getPluginPath(clazz), "loader"), fileName);
	}
}
