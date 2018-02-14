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

import java.util.Map;

import javax.annotation.Nonnull;
import com.intellij.openapi.roots.OrderRootType;
import consulo.module.extension.ModuleExtension;

/**
 * @author VISTALL
 * @since 09.06.2015
 */
public interface DotNetModuleExtensionWithLibraryProviding<T extends DotNetModuleExtensionWithLibraryProviding<T>> extends ModuleExtension<T>
{
	@Nonnull
	Map<String, String> getAvailableSystemLibraries();

	@Nonnull
	String[] getSystemLibraryUrls(@Nonnull String name, @Nonnull OrderRootType orderRootType);
}
