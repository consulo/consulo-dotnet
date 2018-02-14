package consulo.dotnet.run.coverage;

import javax.annotation.Nonnull;

import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunConfigurationModule;

/**
 * @author VISTALL
 * @since 11.01.15
 */
public interface DotNetConfigurationWithCoverage extends ModuleRunProfile
{
	@Nonnull
	RunConfigurationModule getConfigurationModule();
}
