package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
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

	@NotNull
	@Override
	public String getValue()
	{
		return (String) super.getValue();
	}

	@NotNull
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
