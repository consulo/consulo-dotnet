package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Getter;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetArrayValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetOrSetArrayValueAtRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.ArrayValueResult;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftArrayValueProxy extends MicrosoftValueProxyBase<ArrayValueResult> implements DotNetArrayValueProxy
{
	private Getter<DotNetTypeProxy> myType;
	private MicrosoftDebuggerClient myClient;

	public MicrosoftArrayValueProxy(MicrosoftDebuggerClient client, ArrayValueResult result)
	{
		super(result);
		myClient = client;
		myType = MicrosoftTypeProxyOld.lazyOf(client, myResult.Type);
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
		visitor.visitArrayValue(this);
	}

	@Override
	public long getAddress()
	{
		return myResult.Address;
	}

	@Override
	public int getLength()
	{
		return myResult.Length;
	}

	@Nullable
	@Override
	public DotNetValueProxy get(int index)
	{
		return MicrosoftValueProxyUtilOld.sendAndReceive(myClient, new GetOrSetArrayValueAtRequest(myResult.ObjectId, index, 0));
	}

	@Override
	public void set(int index, @NotNull DotNetValueProxy proxy)
	{

	}
}
