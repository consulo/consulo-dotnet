package consulo.dotnet.debugger.impl;

import consulo.execution.debug.XDebugSession;
import consulo.dotnet.util.DebugConnectionInfo;
import consulo.execution.configuration.RunProfile;


/**
 * @author VISTALL
 * @since 16.04.2016
 */
public interface DotNetModuleExtensionWithDebug
{
	DotNetDebugProcess createDebuggerProcess(XDebugSession session, RunProfile runProfile, DebugConnectionInfo debugConnectionInfo);
}
