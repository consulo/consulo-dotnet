package consulo.dotnet.run.coverage;

import consulo.execution.configuration.ModuleRunProfile;
import consulo.execution.configuration.RunConfigurationModule;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 11.01.15
 */
public interface DotNetConfigurationWithCoverage extends ModuleRunProfile
{
	@Nonnull
	RunConfigurationModule getConfigurationModule();
}
