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

package org.mustbe.consulo.dotnet.compiler;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 25.09.14
 */
public interface DotNetDependencyCopierExtension
{
	ExtensionPointName<DotNetDependencyCopierExtension>
			EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.dependencyCopierExtension");

	@NotNull
	List<File> collectDependencies(@NotNull Module module);
}
