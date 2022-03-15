package consulo.dotnet.debugger;

import consulo.debugger.XDebugSession;
import consulo.dotnet.execution.DebugConnectionInfo;
import consulo.execution.configuration.RunProfile;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public interface DotNetModuleExtensionWithDebug
{
	@Nonnull
	DotNetDebugProcessBase createDebuggerProcess(@Nonnull XDebugSession session, @Nonnull RunProfile runProfile, @Nonnull DebugConnectionInfo debugConnectionInfo);
}
