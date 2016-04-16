package consulo.dotnet.microsoft.debugger.protocol.event;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClientContext;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class OnEvent
{
	public String Id;

	public String Type;

	public OnEventValue Object;

	public boolean accept(OnEventVisitor visitor, MicrosoftDebuggerClientContext context)
	{
		assert Object != null;

		return Object.accept(visitor, context);
	}
}
