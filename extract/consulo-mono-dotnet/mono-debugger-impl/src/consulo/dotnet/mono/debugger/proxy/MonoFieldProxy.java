package consulo.dotnet.mono.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
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
	public DotNetValueProxy getValue(@NotNull DotNetThreadProxy threadProxy, @NotNull DotNetValueProxy proxy)
	{
		MonoThreadProxy monoThreadProxy = (MonoThreadProxy) threadProxy;
		MonoValueProxyBase<?> monoValueProxyBase = (MonoValueProxyBase<?>) proxy;
		return MonoValueProxyUtil.wrap(myMirror.value(monoThreadProxy.getThreadMirror(), (ObjectValueMirror) monoValueProxyBase.getMirror()));
	}

	@Override
	public void setValue(@NotNull DotNetThreadProxy threadProxy, @NotNull DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy)
	{

	}
}
