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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtension;
import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetVersion;
import org.mustbe.consulo.dotnet.module.ConfigurationProfile;
import org.mustbe.consulo.dotnet.module.ConfigurationProfileEx;
import org.mustbe.consulo.dotnet.module.ConfigurationProfileImpl;
import org.mustbe.consulo.dotnet.module.MainConfigurationProfileEx;
import org.mustbe.consulo.dotnet.module.MainConfigurationProfileExImpl;
import org.mustbe.consulo.dotnet.module.ui.ConfigurationProfilePanel;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public abstract class DotNetModuleExtensionImpl<S extends DotNetModuleExtensionImpl<S>> extends ModuleExtensionImpl<S> implements
		DotNetModuleExtension<S>
{
	protected List<ConfigurationProfile> myProfiles = new ArrayList<ConfigurationProfile>(2);

	public DotNetModuleExtensionImpl(@NotNull String id, @NotNull ModifiableRootModel rootModel)
	{
		super(id, rootModel);
	}

	@NotNull
	protected abstract Class<? extends SdkType> getSdkTypeClass();

	@Nullable
	public JComponent createConfigurablePanelImpl(@Nullable Runnable runnable)
	{
		return new ConfigurationProfilePanel(myRootModel, runnable, MainConfigurationProfileExImpl.KEY);
	}

	public boolean isModifiedImpl(S originExtension)
	{
		return myIsEnabled != originExtension.isEnabled() ||

				!myProfiles.equals(originExtension.getProfiles());
	}

	public void setCurrentProfile(@NotNull String name)
	{
		ConfigurationProfile profile = null;
		for(ConfigurationProfile configurationProfile : myProfiles)
		{
			if(Comparing.equal(configurationProfile.getName(), name))
			{
				profile = configurationProfile;
			}
			configurationProfile.setActive(false);
		}

		if(profile != null)
		{
			profile.setActive(true);
		}
		else
		{
			myProfiles.get(0).setActive(true);
		}
	}

	public void setEnabled(boolean b)
	{
		assert this instanceof MutableModuleExtension;

		myIsEnabled = b;

		if(myProfiles.isEmpty())
		{
			initDefaultProfiles();
		}
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		MainConfigurationProfileEx<?> currentProfileEx = getCurrentProfileEx(MainConfigurationProfileExImpl.KEY);
		return currentProfileEx.getInheritableSdk();
	}

	@NotNull
	@Override
	public DotNetVersion getVersion()
	{
		return DotNetVersion.LAST;
	}

	@Override
	public List<ConfigurationProfile> getProfiles()
	{
		return myProfiles;
	}

	@Nullable
	@Override
	public Sdk getSdk()
	{
		MainConfigurationProfileEx<?> currentProfileEx = getCurrentProfileEx(MainConfigurationProfileEx.KEY);
		return currentProfileEx.getInheritableSdk().get();
	}

	@Nullable
	@Override
	public String getSdkName()
	{
		MainConfigurationProfileEx<?> currentProfileEx = getCurrentProfileEx(MainConfigurationProfileEx.KEY);
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
	public ConfigurationProfile getCurrentProfile()
	{
		for(ConfigurationProfile profile : myProfiles)
		{
			if(profile.isActive())
			{
				return profile;
			}
		}
		return ConfigurationProfileImpl.NULL;
	}

	@NotNull
	@Override
	public <T extends ConfigurationProfileEx<T>> T getCurrentProfileEx(@NotNull Key<T> clazz)
	{
		ConfigurationProfile currentProfile = getCurrentProfile();
		return currentProfile.getExtension(clazz);
	}

	@Override
	protected void loadStateImpl(@NotNull Element element)
	{
		super.loadStateImpl(element);

		Element profiles = element.getChild("profiles");
		if(profiles == null)
		{
			initDefaultProfiles();
		}
		else
		{
			for(Element childElement : profiles.getChildren())
			{
				String name = childElement.getAttributeValue("name");
				boolean active = Boolean.valueOf(childElement.getAttributeValue("active", "false"));
				ConfigurationProfileImpl profile = new ConfigurationProfileImpl(name, active);
				profile.initDefaults(this);

				for(Element profileExElement : childElement.getChildren("profile_ex"))
				{
					String key = profileExElement.getAttributeValue("key");

					ConfigurationProfileEx configurationProfileEx = profile.getExtensions().get(key);
					if(configurationProfileEx != null)
					{
						configurationProfileEx.loadState(profileExElement);
					}
				}

				myProfiles.add(profile);
			}
		}
	}

	@Override
	protected void getStateImpl(@NotNull Element element)
	{
		super.getStateImpl(element);

		Element profilesElement = new Element("profiles");
		for(ConfigurationProfile profile : myProfiles)
		{
			Element profileElement = new Element("profile");
			profileElement.setAttribute("name", profile.getName());
			profileElement.setAttribute("active", Boolean.toString(profile.isActive()));

			for(Map.Entry<String, ConfigurationProfileEx<?>> pair : profile.getExtensions().entrySet())
			{
				Element profileExElement = new Element("profile_ex");
				profileExElement.setAttribute("key", pair.getKey());

				pair.getValue().getState(profileExElement);

				profileElement.addContent(profileExElement);
			}

			profilesElement.addContent(profileElement);
		}

		element.addContent(profilesElement);
	}

	protected void initDefaultProfiles()
	{
		ConfigurationProfileImpl release = new ConfigurationProfileImpl("Release", false);
		release.initDefaults(this, false);
		myProfiles.add(release);

		ConfigurationProfileImpl debug = new ConfigurationProfileImpl("Debug", true);
		debug.initDefaults(this, true);
		myProfiles.add(debug);
	}

	@Override
	public void commit(@NotNull S mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);

		myProfiles.clear();
		for(ConfigurationProfile configurationProfile : mutableModuleExtension.getProfiles())
		{
			myProfiles.add(configurationProfile.clone());
		}
	}
}
