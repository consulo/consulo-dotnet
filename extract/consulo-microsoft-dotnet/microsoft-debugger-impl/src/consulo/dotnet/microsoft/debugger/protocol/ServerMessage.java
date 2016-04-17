package consulo.dotnet.microsoft.debugger.protocol;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClientContext;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.OnEventValue;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.OnEventVisitor;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class ServerMessage
{
	public String Id;

	public String Type;

	public Object Object;

	public boolean accept(OnEventVisitor visitor, MicrosoftDebuggerClientContext context)
	{
		assert Object != null;

		return !(Object instanceof OnEventValue) || ((OnEventValue) Object).accept(visitor, context);
	}
}
