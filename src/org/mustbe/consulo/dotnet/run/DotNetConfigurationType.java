package org.mustbe.consulo.dotnet.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetConfigurationType extends ConfigurationTypeBase
{
	public DotNetConfigurationType()
	{
		super("#DotNetConfigurationType", ".NET Application", "", AllIcons.RunConfigurations.Application);

		addFactory(new ConfigurationFactory(this)
		{
			@Override
			public RunConfiguration createTemplateConfiguration(Project project)
			{
				return new DotNetConfiguration("Unnamed", new RunConfigurationModule(project), this);
			}
		});
	}
}
