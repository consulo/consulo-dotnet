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

package org.mustbe.consulo.dotnet.module.extension;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetVersion;
import org.mustbe.consulo.dotnet.module.ConfigurationLayer;
import org.mustbe.consulo.dotnet.module.LayeredModuleExtension;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayer;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayerImpl;
import org.mustbe.consulo.dotnet.module.ui.ConfigurationProfilePanel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Comparing;
import com.intellij.util.ListWithSelection;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public abstract class DotNetModuleExtensionImpl<S extends DotNetModuleExtensionImpl<S>> extends ModuleExtensionImpl<S> implements
		DotNetModuleExtension<S>
{
	protected Map<String, ConfigurationLayer> myLayers = new LinkedHashMap<String, ConfigurationLayer>();
	protected String myCurrentLayer;

	public DotNetModuleExtensionImpl(@NotNull String id, @NotNull Module module)
	{
		super(id, module);

		addLayer("Release");

		MainConfigurationLayerImpl debug = (MainConfigurationLayerImpl) addLayer(myCurrentLayer = "Debug");
		debug.getVariables().add("DEBUG");
		debug.setAllowDebugInfo(true);
	}

	@NotNull
	protected abstract Class<? extends SdkType> getSdkTypeClass();

	@Nullable
	public JComponent createConfigurablePanelImpl(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		return new ConfigurationProfilePanel(modifiableRootModel, runnable, this);
	}

	public boolean isModifiedImpl(S originExtension)
	{
		return myIsEnabled != originExtension.isEnabled() ||
				!Comparing.equal(myCurrentLayer, originExtension.myCurrentLayer) ||
				!myLayers.equals(originExtension.myLayers);
	}

	@NotNull
	@Override
	public String getCurrentLayerName()
	{
		return myCurrentLayer;
	}

	@NotNull
	@Override
	public Class<? extends LayeredModuleExtension> getHeadClass()
	{
		return DotNetModuleExtension.class;
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		MainConfigurationLayer currentProfileEx = (MainConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk();
	}

	@NotNull
	@Override
	public DotNetVersion getVersion()
	{
		return DotNetVersion.LAST;
	}

	@Nullable
	@Override
	public Sdk getSdk()
	{
		MainConfigurationLayer currentProfileEx = (MainConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk().get();
	}

	@Nullable
	@Override
	public String getSdkName()
	{
		MainConfigurationLayer currentProfileEx = (MainConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk().getName();
	}

	@Nullable
	@Override
	public SdkType getSdkType()
	{
		return SdkType.findInstance(getSdkTypeClass());
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

	@NotNull
	protected ConfigurationLayer createLayer()
	{
		return new MainConfigurationLayerImpl(this);
	}

	@NotNull
	public ConfigurationLayer addLayer(@NotNull String name)
	{
		ConfigurationLayer layer = createLayer();
		myLayers.put(name, layer);
		return layer;
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
}
