package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Getter;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.ObjectValueResult;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftObjectValueProxy extends MicrosoftValueProxyBaseOld<ObjectValueResult> implements DotNetObjectValueProxy
{
	private MicrosoftDebuggerClient myClient;
	private Getter<DotNetTypeProxy> myType;

	public MicrosoftObjectValueProxy(MicrosoftDebuggerClient client, ObjectValueResult result)
	{
		super(result);
		myClient = client;
		myType = MicrosoftTypeProxyOld.lazyOf(myClient, myResult.Type);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return myType.get();
	}

	@NotNull
	@Override
	public Object getValue()
	{
		throw new UnsupportedOperationException();
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
