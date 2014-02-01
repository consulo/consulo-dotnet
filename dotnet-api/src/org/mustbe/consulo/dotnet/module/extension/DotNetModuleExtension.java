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

import java.util.List;

import org.consulo.annotations.Immutable;
import org.consulo.module.extension.ModuleExtensionWithSdk;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetVersion;
import org.mustbe.consulo.dotnet.module.ConfigurationProfile;
import org.mustbe.consulo.dotnet.module.ConfigurationProfileEx;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.Key;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public interface DotNetModuleExtension<T extends DotNetModuleExtension<T>> extends ModuleExtensionWithSdk<T>
{
	@NotNull
	DotNetVersion getVersion();

	@NotNull
	GeneralCommandLine createRunCommandLine(@NotNull String fileName, @NotNull ConfigurationProfile configurationProfile, Executor executor);

	@Immutable
	List<ConfigurationProfile> getProfiles();

	@NotNull
	ConfigurationProfile getCurrentProfile();

	@NotNull
	<T extends ConfigurationProfileEx<T>> T getCurrentProfileEx(@NotNull Key<T> clazz);
}
