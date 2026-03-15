package consulo.dotnet.run;

import consulo.process.ProcessConsoleType;


/**
 * @author VISTALL
 * @since 2020-10-23
 */
public interface DotNetConfigurationConsoleTypeProvider
{
	ProcessConsoleType getConsoleType();
}
