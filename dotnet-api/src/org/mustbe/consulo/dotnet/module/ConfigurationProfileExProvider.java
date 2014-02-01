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

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.extensions.ExtensionPointName;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public interface ConfigurationProfileExProvider
{
	ExtensionPointName<ConfigurationProfileExProvider> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.configurationExProvider");

	@NotNull
	ConfigurationProfileEx createEx(@NotNull DotNetModuleExtension dotNetModuleExtension, @NotNull ConfigurationProfile profile);

	@NotNull
	ConfigurationProfileEx createExForDefaults(@NotNull DotNetModuleExtension dotNetModuleExtension, @NotNull ConfigurationProfile profile, boolean debug);
}
