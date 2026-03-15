package consulo.dotnet.run.coverage;

import consulo.execution.configuration.ModuleRunProfile;
import consulo.execution.configuration.RunConfigurationModule;

/**
 * @author VISTALL
 * @since 11.01.15
 */
public interface DotNetConfigurationWithCoverage extends ModuleRunProfile
{
	RunConfigurationModule getConfigurationModule();
}
