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

package org.mustbe.consulo.dotnet.module.dependency;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.roots.ui.configuration.classpath.ClasspathPanel;
import com.intellij.openapi.roots.ui.configuration.projectRoot.StructureConfigurableContext;
import consulo.roots.ModuleRootLayer;
import consulo.roots.ui.configuration.classpath.dependencyTab.AddModuleDependencyTabContext;
import consulo.roots.ui.configuration.classpath.dependencyTab.AddModuleDependencyTabFactory;

/**
 * @author VISTALL
 * @since 28.09.14
 */
public class DotNetLibraryModuleDependencyTabFactory implements AddModuleDependencyTabFactory
{
	@Override
	public boolean isAvailable(@NotNull ModuleRootLayer layer)
	{
		return layer.getExtension(DotNetSimpleModuleExtension.class) != null;
	}

	@NotNull
	@Override
	public AddModuleDependencyTabContext createTabContext(@NotNull Disposable parent, @NotNull ClasspathPanel panel, @NotNull StructureConfigurableContext context)
	{
		return new DotNetLibraryModuleDependencyTabContext(panel, context);
	}
}
