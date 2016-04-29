package consulo.dotnet.mono.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import edu.arizona.cs.mbel.signature.FieldAttributes;
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

	@Nullable
	@Override
	public DotNetValueProxy getValue(@NotNull DotNetThreadProxy threadProxy, @Nullable DotNetValueProxy proxy)
	{
		MonoThreadProxy monoThreadProxy = (MonoThreadProxy) threadProxy;
		MonoValueProxyBase<?> monoValueProxyBase = (MonoValueProxyBase<?>) proxy;
		return MonoValueProxyUtil.wrap(myMirror.value(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : (ObjectValueMirror) monoValueProxyBase.getMirror()));
	}

	@Override
	public void setValue(@NotNull DotNetThreadProxy threadProxy, @Nullable DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy)
	{
		MonoThreadProxy monoThreadProxy = (MonoThreadProxy) threadProxy;
		MonoObjectValueProxy monoValueProxyBase = (MonoObjectValueProxy) proxy;
		MonoValueProxyBase<?> monoNewValueProxyBase = (MonoValueProxyBase<?>) newValueProxy;

		myMirror.setValue(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : monoValueProxyBase.getMirror(), monoNewValueProxyBase.getMirror());
	}

	@Override
	public boolean isLiteral()
	{
		return (myMirror.attributes() & FieldAttributes.Literal) == FieldAttributes.Literal;
	}
}
