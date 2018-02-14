package consulo.dotnet.microsoft.debugger.proxy;

import javax.annotation.Nonnull;
import consulo.dotnet.debugger.proxy.value.DotNetEnumValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mssdw.EnumValueMirror;

/**
 * @author VISTALL
 * @since 5/10/2016
 */
public class MicrosoftEnumValueProxy extends MicrosoftValueProxyBase<EnumValueMirror> implements DotNetEnumValueProxy
{
	public MicrosoftEnumValueProxy(EnumValueMirror value)
	{
		super(value);
	}

	@Nonnull
	@Override
	public Object getValue()
	{
		return MicrosoftValueProxyUtil.wrap(myValue.value());
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitEnumValue(this);
	}
}
