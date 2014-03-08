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

import java.util.LinkedHashMap;
import java.util.Map;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Comparing;
import com.intellij.util.ListWithSelection;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 07.03.14
 */
public abstract class LayeredModuleExtensionImpl<S extends LayeredModuleExtensionImpl<S>> extends ModuleExtensionImpl<S> implements
		LayeredModuleExtension<S>
{
	protected Map<String, ConfigurationLayer> myLayers = new LinkedHashMap<String, ConfigurationLayer>();
	protected String myCurrentLayer;

	public LayeredModuleExtensionImpl(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);

		intLayers();
	}

	private void intLayers()
	{
		init(false, addLayer("Release"));
		init(false, addLayer(myCurrentLayer = "Debug"));
	}

	protected void init(boolean debug, @NotNull ConfigurationLayer layer)
	{

	}

	public boolean isModifiedImpl(S originExtension)
	{
		return myIsEnabled != originExtension.isEnabled() ||
				!Comparing.equal(myCurrentLayer, originExtension.myCurrentLayer) ||
				!myLayers.equals(originExtension.myLayers);
	}

	@NotNull
	@Override
	public abstract Class<? extends LayeredModuleExtension> getHeadClass();

	@NotNull
	protected abstract ConfigurationLayer createLayer();

	@NotNull
	@Override
	public ConfigurationLayer getLayer(@NotNull String name)
	{
		return myLayers.get(name);
	}

	@NotNull
	@Override
	public ListWithSelection<String> getLayersList()
	{
		return new ListWithSelection<String>(myLayers.keySet(), myCurrentLayer);
	}

	@NotNull
	@Override
	public ConfigurationLayer getCurrentLayer()
	{
		return myLayers.get(myCurrentLayer);
	}

	public void setCurrentLayer(@NotNull String currentLayer)
	{
		myCurrentLayer = currentLayer;
	}

	public void removeLayer(@NotNull String layer)
	{
		myLayers.remove(layer);
	}

	@NotNull
	public ConfigurationLayer addLayer(@NotNull String name)
	{
		ConfigurationLayer layer = createLayer();
		myLayers.put(name, layer);
		return layer;
	}

	@NotNull
	public ConfigurationLayer copyLayer(@NotNull String oldName, @NotNull String name)
	{
		ConfigurationLayer layer = getLayer(oldName);
		myLayers.put(name, layer = layer.clone());
		return layer;
	}

	@NotNull
	@Override
	public String getCurrentLayerName()
	{
		return myCurrentLayer;
	}

	@Override
	public void commit(@NotNull S mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);

		myCurrentLayer = mutableModuleExtension.myCurrentLayer;
		myLayers.clear();
		for(Map.Entry<String, ConfigurationLayer> entry : mutableModuleExtension.myLayers.entrySet())
		{
			myLayers.put(entry.getKey(), entry.getValue().clone());
		}
	}

	@Override
	protected void loadStateImpl(@NotNull Element element)
	{
		super.loadStateImpl(element);

		Element profiles = element.getChild("profiles");
		if(profiles != null)
		{
			myLayers.clear();

			for(Element childElement : profiles.getChildren())
			{
				String name = childElement.getAttributeValue("name");
				boolean active = Boolean.valueOf(childElement.getAttributeValue("active", "false"));
				if(active)
				{
					myCurrentLayer = name;
				}

				ConfigurationLayer layer = addLayer(name);

				layer.loadState(childElement);
			}

			if(myCurrentLayer == null)
			{
				myCurrentLayer = ContainerUtil.getFirstItem(myLayers.keySet());
			}
		}
	}

	@Override
	protected void getStateImpl(@NotNull Element element)
	{
		super.getStateImpl(element);

		Element profilesElement = new Element("profiles");
		for(Map.Entry<String, ConfigurationLayer> entry : myLayers.entrySet())
		{
			Element profileElement = new Element("profile");
			String name = entry.getKey();
			profileElement.setAttribute("name", name);
			profileElement.setAttribute("active", Boolean.toString(name.equals(myCurrentLayer)));

			ConfigurationLayer value = entry.getValue();
			value.getState(profileElement);

			profilesElement.addContent(profileElement);
		}

		element.addContent(profilesElement);
	}
}
