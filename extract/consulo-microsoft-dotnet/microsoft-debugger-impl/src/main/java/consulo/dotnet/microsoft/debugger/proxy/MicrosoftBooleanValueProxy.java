package consulo.dotnet.microsoft.debugger.proxy;

import javax.annotation.Nonnull;

import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mssdw.BooleanValueMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftBooleanValueProxy extends MicrosoftValueProxyBase<BooleanValueMirror> implements DotNetBooleanValueProxy
{
	public MicrosoftBooleanValueProxy(BooleanValueMirror value)
	{
		super(value);
	}

	@Nonnull
	@Override
	public Boolean getValue()
	{
		return (Boolean) super.getValue();
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitBooleanValue(this);
	}
}
