package consulo.dotnet.microsoft.debugger.protocol.serverMessage;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class OnBreakpointFire implements OnEventValue
{
	public int ActiveThreadId;

	public String FilePath;

	public int Line;

	@Override
	public boolean accept(OnEventVisitor visitor, MicrosoftDebuggerClient context)
	{
		return visitor.visitOnBreakpointFire(this, context);
	}
}
