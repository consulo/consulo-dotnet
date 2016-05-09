package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mssdw.Value;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public abstract class MicrosoftValueProxyBase<T extends Value<?>> implements DotNetValueProxy
{
	protected T myValue;

	public MicrosoftValueProxyBase(T value)
	{
		myValue = value;
	}

	public T getMirror()
	{
		return myValue;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MicrosoftTypeProxy.of(myValue.type());
	}

	@NotNull
	@Override
	public Object getValue()
	{
		return myValue.value();
	}
}
