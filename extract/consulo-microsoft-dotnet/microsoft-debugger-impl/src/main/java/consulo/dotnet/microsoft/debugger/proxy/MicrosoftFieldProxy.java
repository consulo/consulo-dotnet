package consulo.dotnet.microsoft.debugger.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.util.BitUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNumberValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.internal.dotnet.asm.signature.FieldAttributes;
import mssdw.FieldMirror;
import mssdw.ObjectValueMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftFieldProxy extends MicrosoftVariableProxyBase<FieldMirror> implements DotNetFieldProxy
{
	public MicrosoftFieldProxy(@Nonnull FieldMirror mirror)
	{
		super(mirror);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MicrosoftTypeProxy.of(myMirror.type());
	}

	@Override
	public boolean isStatic()
	{
		return myMirror.isStatic();
	}

	@Nonnull
	@Override
	public DotNetTypeProxy getParentType()
	{
		return MicrosoftTypeProxy.of(myMirror.parent());
	}

	@Nullable
	@Override
	public DotNetValueProxy getValue(@Nonnull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy)
	{
		MicrosoftStackFrameProxy microsoftThreadProxy = (MicrosoftStackFrameProxy) frameProxy;
		MicrosoftValueProxyBase<?> microsoftValueProxyBase = (MicrosoftValueProxyBase<?>) proxy;
		return MicrosoftValueProxyUtil.wrap(myMirror.value(microsoftThreadProxy.getFrameMirror(), microsoftValueProxyBase == null ? null : (ObjectValueMirror) microsoftValueProxyBase.getMirror()));
	}

	@Override
	public void setValue(@Nonnull DotNetStackFrameProxy threadProxy, @Nullable DotNetValueProxy proxy, @Nonnull DotNetValueProxy newValueProxy)
	{
		//MonoThreadProxy monoThreadProxy = (MonoThreadProxy) threadProxy;
		//MonoObjectValueProxy monoValueProxyBase = (MonoObjectValueProxy) proxy;
		//MonoValueProxyBase<?> monoNewValueProxyBase = (MonoValueProxyBase<?>) newValueProxy;

		//myMirror.setValue(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : monoValueProxyBase.getMirror(), monoNewValueProxyBase.getMirror());
	}

	@Override
	public boolean isLiteral()
	{
		return BitUtil.isSet(myMirror.attributes(), FieldAttributes.Literal);
	}

	@Nullable
	@Override
	public Number getEnumConstantValue(@Nonnull DotNetStackFrameProxy stackFrameProxy)
	{
		DotNetValueProxy fieldValue = getValue(stackFrameProxy, null);
		if(fieldValue instanceof DotNetNumberValueProxy)
		{
			return (Number) fieldValue.getValue();
		}
		return null;
	}
}
