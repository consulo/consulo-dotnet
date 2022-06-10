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

package consulo.dotnet.module.extension;

import consulo.dotnet.DotNetTarget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public interface DotNetMutableModuleExtension<T extends DotNetModuleExtension<T>> extends DotNetModuleExtension<T>,
		DotNetSimpleMutableModuleExtension<T>
{
	void setFileName(@Nonnull String name);

	void setNamespacePrefix(@Nonnull String prefix);

	void setOutputDir(@Nonnull String name);

	void setMainType(@Nullable String qName);

	void setTarget(@Nonnull DotNetTarget target);

	void setAllowDebugInfo(boolean allowDebugInfo);
}
