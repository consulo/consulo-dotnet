package consulo.dotnet.mono.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mono.debugger.ObjectValueMirror;
import mono.debugger.PropertyMirror;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MonoPropertyProxy extends MonoVariableProxyBase<PropertyMirror> implements DotNetPropertyProxy
{
	public MonoPropertyProxy(@NotNull PropertyMirror mirror)
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
	public DotNetValueProxy getValue(@NotNull DotNetThreadProxy threadProxy, @NotNull DotNetValueProxy proxy)
	{
		MonoThreadProxy monoThreadProxy = (MonoThreadProxy) threadProxy;
		MonoValueProxyBase<?> monoValueProxyBase = (MonoValueProxyBase<?>) proxy;
		return MonoValueProxyUtil.wrap(myMirror.value(monoThreadProxy.getThreadMirror(), (ObjectValueMirror) monoValueProxyBase.getMirror()));
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
	public boolean isArrayProperty()
	{
		return myMirror.isArrayProperty();
	}
}
