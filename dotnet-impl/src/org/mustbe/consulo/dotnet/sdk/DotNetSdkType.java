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
import consulo.dotnet.externalAttributes.ExternalAttributesRootOrderType;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;

/**
 * @author VISTALL
 * @since 20.12.13.
 */
public abstract class DotNetSdkType extends SdkType
{
	public DotNetSdkType(@NonNls String name)
	{
		super(name);
	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == ExternalAttributesRootOrderType.getInstance();
	}

	@NotNull
	public File getLoaderFile(@NotNull Sdk sdk)
	{
		return getLoaderFile(getClass(), "loader.exe");
	}

	@NotNull
	protected static File getLoaderFile(Class<?> clazz, String fileName)
	{
		PluginClassLoader classLoader = (PluginClassLoader) clazz.getClassLoader();
		IdeaPluginDescriptor plugin = PluginManager.getPlugin(classLoader.getPluginId());
		assert plugin != null;
		return new File(new File(plugin.getPath(), "loader"), fileName);
	}
}
