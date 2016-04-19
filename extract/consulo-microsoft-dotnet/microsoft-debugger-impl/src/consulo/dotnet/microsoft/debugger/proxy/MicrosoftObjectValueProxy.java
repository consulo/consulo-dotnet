package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.ObjectValueResult;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftObjectValueProxy extends MicrosoftValueProxyBase<ObjectValueResult> implements DotNetObjectValueProxy
{
	private MicrosoftDebuggerClient myClient;

	public MicrosoftObjectValueProxy(MicrosoftDebuggerClient client, ObjectValueResult result)
	{
		super(result);
		myClient = client;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MicrosoftTypeProxy.of(myClient, myResult.Type);
	}

	@NotNull
	@Override
	public Object getValue()
	{
		return null;
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitObjectValue(this);
	}

	@Override
	public long getAddress()
	{
		return myResult.Address;
	}
}
