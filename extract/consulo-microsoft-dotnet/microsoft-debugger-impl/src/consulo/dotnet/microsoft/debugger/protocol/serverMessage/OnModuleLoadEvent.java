package consulo.dotnet.microsoft.debugger.protocol.serverMessage;

import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClientContext;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class OnModuleLoadEvent implements OnEventValue
{
	public String ModuleFile;

	@Override
	public boolean accept(OnEventVisitor visitor, MicrosoftDebuggerClientContext context)
	{
		return visitor.visitOnModuleLoad(this, context);
	}
}
