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

package consulo.dotnet.module.extension;

import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkType;
import consulo.dotnet.module.DotNetNamespaceGeneratePolicy;
import consulo.module.extension.ModuleInheritableNamedPointer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;

/**
 * @author VISTALL
 * @since 22.02.2015
 *
 * Simple version of DotNetModuleExtension. But it dont have support for run or debug, it need provide new impl in plugins
 */
public interface DotNetSimpleModuleExtension<T extends DotNetSimpleModuleExtension<T>> extends DotNetModuleExtensionWithLibraryProviding<T>
{
	@Nonnull
	ModuleInheritableNamedPointer<Sdk> getInheritableSdk();

	@Nullable
	Sdk getSdk();

	@Nullable
	String getSdkName();

	@Nonnull
	Class<? extends SdkType> getSdkTypeClass();

	@Nonnull
	List<String> getVariables();

	/**
	 * FIXME [VISTALL] this method is really needed? We can check it by instanceof DotNetModuleExtension
	 */
	boolean isSupportCompilation();

	@Nonnull
	DotNetNamespaceGeneratePolicy getNamespaceGeneratePolicy();
}
