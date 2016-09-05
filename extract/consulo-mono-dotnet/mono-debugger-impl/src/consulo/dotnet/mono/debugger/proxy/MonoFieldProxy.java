package consulo.dotnet.mono.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joou.ULong;
import com.intellij.util.BitUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetEnumValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNumberValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.internal.dotnet.asm.signature.FieldAttributes;
import mono.debugger.FieldMirror;
import mono.debugger.ObjectValueMirror;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MonoFieldProxy extends MonoVariableProxyBase<FieldMirror> implements DotNetFieldProxy
{
	public MonoFieldProxy(@NotNull FieldMirror mirror)
	{
		super(mirror);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MonoTypeProxy.of(myMirror.type());
	}

	@Override
	public boolean isStatic()
	{
		return myMirror.isStatic();
	}

	@NotNull
	@Override
	public DotNetTypeProxy getParentType()
	{
		return MonoTypeProxy.of(myMirror.parent());
	}

	@Nullable
	@Override
	public DotNetValueProxy getValue(@NotNull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy)
	{
		MonoThreadProxy monoThreadProxy = (MonoThreadProxy) frameProxy.getThread();
		MonoValueProxyBase<?> monoValueProxyBase = (MonoValueProxyBase<?>) proxy;
		return MonoValueProxyUtil.wrap(myMirror.value(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : (ObjectValueMirror) monoValueProxyBase.getMirror()));
	}

	@Override
	public void setValue(@NotNull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy)
	{
		MonoThreadProxy monoThreadProxy = (MonoThreadProxy) frameProxy.getThread();
		MonoObjectValueProxy monoValueProxyBase = (MonoObjectValueProxy) proxy;
		MonoValueProxyBase<?> monoNewValueProxyBase = (MonoValueProxyBase<?>) newValueProxy;

		myMirror.setValue(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : monoValueProxyBase.getMirror(), monoNewValueProxyBase.getMirror());
	}

	@Override
	public boolean isLiteral()
	{
		return BitUtil.isSet(myMirror.attributes(), FieldAttributes.Literal);
	}

	@Override
	@Nullable
	public ULong getEnumConstantValue(@NotNull DotNetStackFrameProxy stackFrameProxy)
	{
		DotNetValueProxy fieldValue = getValue(stackFrameProxy, null);
		if(fieldValue instanceof DotNetEnumValueProxy)
		{
			Object enumValue = fieldValue.getValue();
			if(enumValue instanceof DotNetNumberValueProxy)
			{
				Number actualValue = ((DotNetNumberValueProxy) enumValue).getValue();

				return ULong.valueOf(actualValue.longValue());
			}
		}
		return null;
	}
}
