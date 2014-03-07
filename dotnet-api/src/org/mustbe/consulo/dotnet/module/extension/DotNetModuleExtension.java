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

import org.consulo.module.extension.ModuleExtensionWithSdk;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetVersion;
import org.mustbe.consulo.dotnet.module.ConfigurationLayer;
import org.mustbe.consulo.dotnet.module.LayeredModuleExtension;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public interface DotNetModuleExtension<T extends DotNetModuleExtension<T>> extends ModuleExtensionWithSdk<T>, LayeredModuleExtension<T>
{
	@NotNull
	DotNetVersion getVersion();

	@NotNull
	GeneralCommandLine createRunCommandLine(@NotNull String fileName, @NotNull ConfigurationLayer configurationProfile, Executor executor);
}
