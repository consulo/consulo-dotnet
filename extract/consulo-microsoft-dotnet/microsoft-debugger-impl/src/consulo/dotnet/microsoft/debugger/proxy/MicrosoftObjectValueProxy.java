package consulo.dotnet.microsoft.debugger.proxy;

import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mssdw.ObjectValueMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftObjectValueProxy extends MicrosoftValueProxyBase<ObjectValueMirror> implements DotNetObjectValueProxy
{
	public MicrosoftObjectValueProxy(ObjectValueMirror value)
	{
		super(value);
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitObjectValue(this);
	}

	@Override
	public long getAddress()
	{
		return myValue.address();
	}
}
