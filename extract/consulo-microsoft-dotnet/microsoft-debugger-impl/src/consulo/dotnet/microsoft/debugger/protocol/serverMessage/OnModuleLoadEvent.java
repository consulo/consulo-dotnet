package consulo.dotnet.microsoft.debugger.protocol.serverMessage;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class OnModuleLoadEvent implements OnEventValue
{
	public String ModuleFile;

	@Override
	public boolean accept(OnEventVisitor visitor, MicrosoftDebuggerClient context)
	{
		return visitor.visitOnModuleLoad(this, context);
	}
}
