package consulo.dotnet.debugger.impl;

import consulo.execution.debug.XDebugSession;
import consulo.dotnet.util.DebugConnectionInfo;
import consulo.execution.configuration.RunProfile;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public interface DotNetModuleExtensionWithDebug
{
	@Nonnull
	DotNetDebugProcessBase createDebuggerProcess(@Nonnull XDebugSession session, @Nonnull RunProfile runProfile, @Nonnull DebugConnectionInfo debugConnectionInfo);
}
