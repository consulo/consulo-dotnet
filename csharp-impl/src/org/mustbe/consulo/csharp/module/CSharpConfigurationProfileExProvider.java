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

package org.mustbe.consulo.csharp.module;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.ConfigurationProfile;
import org.mustbe.consulo.dotnet.module.ConfigurationProfileEx;
import org.mustbe.consulo.dotnet.module.ConfigurationProfileExProvider;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class CSharpConfigurationProfileExProvider implements ConfigurationProfileExProvider
{
	@NotNull
	@Override
	public ConfigurationProfileEx createEx(@NotNull DotNetModuleExtension dotNetModuleExtension, @NotNull ConfigurationProfile profile)
	{
		return new CSharpConfigurationProfileEx();
	}

	@NotNull
	@Override
	public ConfigurationProfileEx createExForDefaults(@NotNull DotNetModuleExtension dotNetModuleExtension, @NotNull ConfigurationProfile profile, boolean debug)
	{
		CSharpConfigurationProfileEx profileEx = new CSharpConfigurationProfileEx();

		return profileEx;
	}
}
