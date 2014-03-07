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

package org.mustbe.consulo.dotnet.ui.profile;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.module.extension.LayeredModuleExtension;
import org.mustbe.consulo.ui.profile.ConfigurationLayerWidgetRegistrator;
import com.intellij.openapi.project.Project;

/**
 * @author VISTALL
 * @since 31.01.14
 */
public class DotNetConfigurationLayerWidgetRegistrator extends ConfigurationLayerWidgetRegistrator
{
	public DotNetConfigurationLayerWidgetRegistrator(Project project)
	{
		super(project);
	}

	@NotNull
	@Override
	public String getPrefix()
	{
		return ".NET";
	}

	@NotNull
	@Override
	public Class<? extends LayeredModuleExtension> getExtensionClass()
	{
		return DotNetModuleExtension.class;
	}
}
