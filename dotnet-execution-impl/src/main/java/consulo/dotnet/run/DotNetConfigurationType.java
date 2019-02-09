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

package consulo.dotnet.run;

import javax.annotation.Nonnull;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import consulo.ui.RequiredUIAccess;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.module.extension.ModuleExtensionHelper;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetConfigurationType extends ConfigurationTypeBase
{
	public static DotNetConfigurationType getInstance()
	{
		return CONFIGURATION_TYPE_EP.findExtension(DotNetConfigurationType.class);
	}

	public DotNetConfigurationType()
	{
		super("#DotNetConfigurationType", ".NET Application", "", AllIcons.RunConfigurations.Application);

		addFactory(new ConfigurationFactoryEx(this)
		{
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
						dotNetConfiguration.setWorkingDirectory(extension.getOutputDir());
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
