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

package org.mustbe.consulo.dotnet.module;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class ConfigurationProfileImpl implements ConfigurationProfile
{
	public static final ConfigurationProfileImpl NULL = new ConfigurationProfileImpl("<null>", true);

	private String myName;
	private boolean myActive;

	private final Map<String, ConfigurationProfileEx<?>> myExtensions = new HashMap<String, ConfigurationProfileEx<?>>();

	public ConfigurationProfileImpl(String name, boolean active)
	{
		myName = name;
		myActive = active;
	}

	public void initDefaults(DotNetModuleExtension dotNetModuleExtension)
	{
		for(ConfigurationProfileExProvider exProvider : ConfigurationProfileExProvider.EP_NAME.getExtensions())
		{
			ConfigurationProfileEx profileEx = exProvider.createEx(dotNetModuleExtension, this);

			myExtensions.put(profileEx.getKey().toString(), profileEx);
		}
	}

	public void initDefaults(DotNetModuleExtension dotNetModuleExtension, boolean debug)
	{
		for(ConfigurationProfileExProvider exProvider : ConfigurationProfileExProvider.EP_NAME.getExtensions())
		{
			ConfigurationProfileEx profileEx = exProvider.createExForDefaults(dotNetModuleExtension, this, debug);

			myExtensions.put(profileEx.getKey().toString(), profileEx);
		}
	}

	@NotNull
	@Override
	public String getName()
	{
		return myName;
	}

	@Override
	public boolean isActive()
	{
		return myActive;
	}

	@Override
	public void setActive(boolean v)
	{
		myActive = v;
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConfigurationProfileEx<T>> T getExtension(@NotNull Key<T> clazz)
	{
		return (T) myExtensions.get(clazz.toString());
	}

	@NotNull
	@Override
	public Map<String, ConfigurationProfileEx<?>> getExtensions()
	{
		return myExtensions;
	}

	@NotNull
	@Override
	public ConfigurationProfile clone()
	{
		ConfigurationProfileImpl profile = new ConfigurationProfileImpl(getName(), isActive());
		for(Map.Entry<String, ConfigurationProfileEx<?>> entry : myExtensions.entrySet())
		{
			profile.myExtensions.put(entry.getKey(), entry.getValue().clone());
		}
		return profile;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ConfigurationProfile)
		{
			ConfigurationProfile obj1 = (ConfigurationProfile) obj;
			if(Comparing.equal(myName, obj1.getName()) && myActive == obj1.isActive())
			{
				for(Map.Entry<String, ConfigurationProfileEx<?>> entry : myExtensions.entrySet())
				{
					ConfigurationProfileEx<?> configurationProfileEx = obj1.getExtensions().get(entry.getKey());
					if(configurationProfileEx == null)
					{
						return false;
					}

					ConfigurationProfileEx profileEx = entry.getValue();
					if(!profileEx.equalsEx(configurationProfileEx))
					{
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
