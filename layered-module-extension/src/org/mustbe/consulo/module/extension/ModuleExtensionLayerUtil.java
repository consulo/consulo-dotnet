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

package org.mustbe.consulo.module.extension;

import org.consulo.module.extension.ModuleExtension;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import lombok.val;

/**
 * @author VISTALL
 * @since 07.03.14
 */
public class ModuleExtensionLayerUtil
{
	public static void setCurrentLayer(@NotNull Module module,
			@NotNull String layer,
			@NotNull Class<? extends LayeredModuleExtension> clazz)
	{
		val modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();

		setCurrentLayerNoCommit(modifiableModel, layer, clazz);

		new WriteAction<Object>()
		{
			@Override
			protected void run(Result<Object> objectResult) throws Throwable
			{
				modifiableModel.commit();
			}
		}.execute();
	}

	public static void setCurrentLayerNoCommit(@NotNull ModifiableRootModel modifiableRootModel,
			@NotNull String layer,
			@NotNull Class<? extends LayeredModuleExtension> clazz)
	{
		for(ModuleExtension moduleExtension : modifiableRootModel.getExtensions())
		{
			if(moduleExtension instanceof LayeredMutableModuleExtension)
			{
				LayeredMutableModuleExtension<?> layeredMutableModuleExtension = (LayeredMutableModuleExtension) moduleExtension;
				if(layeredMutableModuleExtension.getHeadClass().isAssignableFrom(clazz))
				{
					layeredMutableModuleExtension.setCurrentLayer(layer);
				}
			}
		}
	}

	public static void addLayerNoCommit(@NotNull ModifiableRootModel modifiableRootModel,
			@NotNull String layer,
			@NotNull Class<? extends LayeredModuleExtension> clazz)
	{
		for(ModuleExtension moduleExtension : modifiableRootModel.getExtensions())
		{
			if(moduleExtension instanceof LayeredMutableModuleExtension)
			{
				LayeredMutableModuleExtension<?> layeredMutableModuleExtension = (LayeredMutableModuleExtension) moduleExtension;
				if(layeredMutableModuleExtension.getHeadClass().isAssignableFrom(clazz))
				{
					layeredMutableModuleExtension.addLayer(layer);
				}
			}
		}
	}

	public static void copyLayerNoCommit(@NotNull ModifiableRootModel modifiableRootModel,
			@NotNull String oldLayer,
			@NotNull String layer,
			@NotNull Class<? extends LayeredModuleExtension> clazz)
	{
		for(ModuleExtension moduleExtension : modifiableRootModel.getExtensions())
		{
			if(moduleExtension instanceof LayeredMutableModuleExtension)
			{
				LayeredMutableModuleExtension<?> layeredMutableModuleExtension = (LayeredMutableModuleExtension) moduleExtension;
				if(layeredMutableModuleExtension.getHeadClass().isAssignableFrom(clazz))
				{
					layeredMutableModuleExtension.copyLayer(oldLayer, layer);
				}
			}
		}
	}

	public static void removeLayerNoCommit(@NotNull ModifiableRootModel modifiableRootModel,
			@NotNull String layer,
			@NotNull Class<? extends LayeredModuleExtension> clazz)
	{
		for(ModuleExtension moduleExtension : modifiableRootModel.getExtensions())
		{
			if(moduleExtension instanceof LayeredMutableModuleExtension)
			{
				LayeredMutableModuleExtension<?> layeredMutableModuleExtension = (LayeredMutableModuleExtension) moduleExtension;
				if(layeredMutableModuleExtension.getHeadClass().isAssignableFrom(clazz))
				{
					layeredMutableModuleExtension.removeLayer(layer);
				}
			}
		}
	}
}
