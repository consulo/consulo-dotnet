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

import java.io.File;

import org.consulo.module.extension.ModuleExtensionWithSdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.module.extension.LayeredModuleExtension;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public interface DotNetModuleExtension<T extends DotNetModuleExtension<T>> extends ModuleExtensionWithSdk<T>, LayeredModuleExtension<T>
{
	boolean isAllowSourceRoots();

	@NotNull
	GeneralCommandLine createDefaultCommandLine(@NotNull String fileName, @Nullable DebugConnectionInfo d);

	@NotNull
	File getLoaderPath();

	@NotNull
	GlobalSearchScope getScopeForResolving(boolean test);

	@NotNull
	String getDebugFileExtension();
}
