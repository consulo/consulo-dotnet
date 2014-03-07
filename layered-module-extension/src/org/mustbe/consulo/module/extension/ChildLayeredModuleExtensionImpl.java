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

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.util.ListWithSelection;

/**
 * @author VISTALL
 * @since 07.03.14
 */
public abstract class ChildLayeredModuleExtensionImpl<S extends LayeredModuleExtensionImpl<S>> extends LayeredModuleExtensionImpl<S>
{
	public ChildLayeredModuleExtensionImpl(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	public void setEnabled(boolean b)
	{
		myIsEnabled = b;

		if(myIsEnabled)
		{
			LayeredModuleExtension<?> extension = ModuleUtilCore.getExtension(getModule(), getHeadClass());
			assert extension != null;
			ListWithSelection<String> layersList = extension.getLayersList();
			for(String s : layersList)
			{
				init(s.equals("Debug"), addLayer(s));
			}
			myCurrentLayer = layersList.getSelection();
		}
		else
		{
			myLayers.clear();
		}
	}
}
