package consulo.dotnet.microsoft.debugger.protocol.serverMessage;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class OnEventVisitor
{
	public boolean visitOnModuleLoad(OnModuleLoadEvent event, MicrosoftDebuggerClient context)
	{
		return true;
	}

	public boolean visitOnBreakpointFire(OnBreakpointFire event, MicrosoftDebuggerClient context)
	{
		return true;
	}
}
