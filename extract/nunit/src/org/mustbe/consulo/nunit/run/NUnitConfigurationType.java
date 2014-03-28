package org.mustbe.consulo.nunit.run;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.module.extension.ModuleExtensionHelper;
import org.mustbe.consulo.nunit.NUnitIcons;
import org.mustbe.consulo.nunit.module.extension.NUnitModuleExtension;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfigurationType extends ConfigurationTypeBase
{
	public NUnitConfigurationType()
	{
		super("#NUnitConfigurationType", "NUnit", "", NUnitIcons.NUnit);
		addFactory(new ConfigurationFactoryEx(this)
		{
			@Override
			public RunConfiguration createTemplateConfiguration(Project project)
			{
				return new NUnitConfiguration("Unnamed", new RunConfigurationModule(project), this);
			}

			@Override
			public boolean isApplicable(@NotNull Project project)
			{
				return ModuleExtensionHelper.getInstance(project).hasModuleExtension(NUnitModuleExtension.class);
			}

			@Override
			public void onNewConfigurationCreated(@NotNull RunConfiguration configuration)
			{
				NUnitConfiguration dotNetConfiguration = (NUnitConfiguration) configuration;

				for(val module : ModuleManager.getInstance(configuration.getProject()).getModules())
				{
					NUnitModuleExtension extension = ModuleUtilCore.getExtension(module, NUnitModuleExtension.class);
					if(extension != null)
					{
						dotNetConfiguration.setName(module.getName());
						dotNetConfiguration.setModule(module);
						break;
					}
				}
			}
		});
	}
}
