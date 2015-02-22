/*
 * Copyright 2013-2015 must-be.org
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
import java.util.Map;

import org.consulo.annotations.Immutable;
import org.consulo.annotations.InheritImmutable;
import org.consulo.module.extension.ModuleExtension;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.OrderRootType;

/**
 * @author VISTALL
 * @since 22.02.2015
 *
 * Simple version of DotNetModuleExtension. But it dont have support for run or debug, it need provide with new impl in plugins
 */
public interface DotNetSimpleModuleExtension<T extends DotNetSimpleModuleExtension<T>> extends ModuleExtension<T>
{
	@InheritImmutable
	@Immutable
	List<String> getVariables();

	boolean isSupportCompilation();

	@NotNull
	Map<String, String> getAvailableSystemLibraries();

	@NotNull
	String[] getSystemLibraryUrls(@NotNull String name, @NotNull OrderRootType orderRootType);
}
