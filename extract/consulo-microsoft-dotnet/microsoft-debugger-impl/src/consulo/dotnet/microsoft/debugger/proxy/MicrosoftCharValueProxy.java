package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetCharValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.CharValueResult;

/**
 * @author VISTALL
 * @since 20.04.2016
 */
public class MicrosoftCharValueProxy extends MicrosoftValueProxyBase<CharValueResult> implements DotNetCharValueProxy
{
	private MicrosoftDebuggerClient myClient;

	public MicrosoftCharValueProxy(MicrosoftDebuggerClient client, CharValueResult result)
	{
		super(result);
		myClient = client;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MicrosoftTypeProxy.byVmQName(myClient, DotNetTypes.System.Char);
	}

	@NotNull
	@Override
	public Character getValue()
	{
		return myResult.Value;
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitCharValue(this);
	}
}
