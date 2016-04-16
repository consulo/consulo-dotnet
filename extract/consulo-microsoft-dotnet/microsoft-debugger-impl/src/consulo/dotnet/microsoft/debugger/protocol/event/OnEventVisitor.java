package consulo.dotnet.microsoft.debugger.protocol.event;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClientContext;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class OnEventVisitor
{
	public boolean visitOnModuleLoad(OnModuleLoadEvent event, MicrosoftDebuggerClientContext context)
	{
		return true;
	}

	public boolean visitOnBreakpointFire(OnBreakpointFire event, MicrosoftDebuggerClientContext context)
	{
		return true;
	}
}
