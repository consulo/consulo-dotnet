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

package org.mustbe.consulo.dotnet.run;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerConfiguration;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.module.extension.ModuleExtensionHelper;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import lombok.val;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetConfigurationType extends ConfigurationTypeBase
{
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
			public void onNewConfigurationCreated(@NotNull RunConfiguration configuration)
			{
				DotNetConfiguration dotNetConfiguration = (DotNetConfiguration) configuration;

				for(val o : ModuleManager.getInstance(configuration.getProject()).getModules())
				{
					if(ModuleUtilCore.getExtension(o, DotNetModuleExtension.class) != null)
					{
						dotNetConfiguration.setModule(o);
						break;
					}
				}
				dotNetConfiguration.setWorkingDirectory(DotNetCompilerConfiguration.getInstance(configuration.getProject()).getOutputDir());
			}

			@Override
			public boolean isApplicable(@NotNull Project project)
			{
				return ModuleExtensionHelper.getInstance(project).hasModuleExtension(DotNetModuleExtension.class);
			}
		});
	}
}
