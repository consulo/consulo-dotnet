package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetArrayValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.ArrayValueResult;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftArrayValueProxy extends MicrosoftValueProxyBase<ArrayValueResult> implements DotNetArrayValueProxy
{
	public MicrosoftArrayValueProxy(ArrayValueResult result)
	{
		super(result);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return null;
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
		return null;
	}

	@Override
	public void set(int index, @NotNull DotNetValueProxy proxy)
	{

	}
}
