package consulo.dotnet.run;

import consulo.execution.console.ConsoleType;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2020-10-23
 */
public interface DotNetConfigurationConsoleTypeProvider
{
	@Nonnull
	ConsoleType getConsoleType();
}
