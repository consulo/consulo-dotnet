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

package consulo.dotnet.run.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.execution.localize.DotNetExecutionLocalize;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.execution.configuration.ConfigurationFactory;
import consulo.execution.configuration.ConfigurationTypeBase;
import consulo.execution.configuration.RunConfiguration;
import consulo.execution.configuration.RunConfigurationModule;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
@ExtensionImpl
public class DotNetConfigurationType extends ConfigurationTypeBase
{
	@Nonnull
	public static DotNetConfigurationType getInstance()
	{
		return EP_NAME.findExtensionOrFail(DotNetConfigurationType.class);
	}

	public DotNetConfigurationType()
	{
		super("#DotNetConfigurationType", DotNetExecutionLocalize.dotnetApplicationName(), PlatformIconGroup.runconfigurationsApplication());

		addFactory(new ConfigurationFactory(this)
		{
			@Nonnull
			@Override
			public String getId()
			{
				return ".NET Application";
			}

			@Override
			public RunConfiguration createTemplateConfiguration(Project project)
			{
				return new DotNetConfiguration("Unnamed", new RunConfigurationModule(project), this);
			}

			@Override
			@RequiredUIAccess
			public void onNewConfigurationCreated(@Nonnull RunConfiguration configuration)
			{
				DotNetConfiguration dotNetConfiguration = (DotNetConfiguration) configuration;

				for(Module module : ModuleManager.getInstance(configuration.getProject()).getModules())
				{
					DotNetRunModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetRunModuleExtension.class);
					if(extension != null)
					{
						dotNetConfiguration.setName(module.getName());
						dotNetConfiguration.setModule(module);
						break;
					}
				}
			}

			@Override
			public boolean isApplicable(@Nonnull Project project)
			{
				return ModuleExtensionHelper.getInstance(project).hasModuleExtension(DotNetRunModuleExtension.class);
			}
		});
	}
}
