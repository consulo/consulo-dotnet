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

import org.consulo.module.extension.MutableModuleExtension;
import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * @author VISTALL
 * @since 22.02.2015
 */
public interface DotNetSimpleMutableModuleExtension<T extends DotNetSimpleModuleExtension<T>> extends DotNetSimpleModuleExtension<T>,
		MutableModuleExtension<T>
{
	@NotNull
	@Override
	List<String> getVariables();

	@Override
	@NotNull
	MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk();
}
