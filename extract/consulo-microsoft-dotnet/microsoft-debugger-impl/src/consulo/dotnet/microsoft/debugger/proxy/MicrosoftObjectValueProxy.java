package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.TypeRef;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftObjectValueProxy extends MicrosoftValueProxyBase<Object> implements DotNetObjectValueProxy
{
	private MicrosoftDebuggerClient myClient;
	private TypeRef myType;

	public MicrosoftObjectValueProxy(MicrosoftDebuggerClient client, int id, TypeRef type, Object value)
	{
		super(id, value);
		myClient = client;
		myType = type;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MicrosoftTypeProxy.of(myClient, myType);
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitObjectValue(this);
	}
}
