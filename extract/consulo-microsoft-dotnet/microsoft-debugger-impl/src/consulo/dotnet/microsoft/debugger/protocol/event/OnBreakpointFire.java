package consulo.dotnet.microsoft.debugger.protocol.event;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClientContext;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class OnBreakpointFire implements OnEventValue
{
	public String FilePath;

	public int Line;

	@Override
	public boolean accept(OnEventVisitor visitor, MicrosoftDebuggerClientContext context)
	{
		return visitor.visitOnBreakpointFire(this, context);
	}
}
