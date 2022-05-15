package consulo.dotnet.run;

import consulo.process.ProcessConsoleType;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2020-10-23
 */
public interface DotNetConfigurationConsoleTypeProvider
{
	@Nonnull
	ProcessConsoleType getConsoleType();
}
