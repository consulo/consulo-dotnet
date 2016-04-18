package consulo.dotnet.microsoft.debugger.protocol.serverMessage;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public interface OnEventValue
{
	boolean accept(OnEventVisitor visitor, MicrosoftDebuggerClient context);
}
