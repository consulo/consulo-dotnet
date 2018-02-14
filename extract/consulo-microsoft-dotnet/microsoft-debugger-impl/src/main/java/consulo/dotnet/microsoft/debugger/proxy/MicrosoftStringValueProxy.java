package consulo.dotnet.microsoft.debugger.proxy;

import javax.annotation.Nonnull;

import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mssdw.StringValueMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftStringValueProxy extends MicrosoftValueProxyBase<StringValueMirror> implements DotNetStringValueProxy
{
	public MicrosoftStringValueProxy(StringValueMirror value)
	{
		super(value);
	}

	@Nonnull
	@Override
	public String getValue()
	{
		return (String) super.getValue();
	}

	@Nonnull
	@Override
	public DotNetValueProxy getObjectValue()
	{
		return this;
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitStringValue(this);
	}
}
