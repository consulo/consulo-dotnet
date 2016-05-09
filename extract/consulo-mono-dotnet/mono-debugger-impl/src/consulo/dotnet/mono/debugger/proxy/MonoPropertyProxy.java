package consulo.dotnet.mono.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mono.debugger.MethodMirror;
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

	@NotNull
	@Override
	public DotNetTypeProxy getParentType()
	{
		return MonoTypeProxy.of(myMirror.parent());
	}

	@Override
	public boolean isStatic()
	{
		return myMirror.isStatic();
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
	public void setValue(@NotNull DotNetStackFrameProxy threadProxy, @Nullable DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy)
	{
		MonoThreadProxy monoThreadProxy = (MonoThreadProxy) threadProxy.getThread();
		MonoObjectValueProxy monoValueProxyBase = (MonoObjectValueProxy) proxy;
		MonoValueProxyBase<?> monoNewValueProxyBase = (MonoValueProxyBase<?>) newValueProxy;

		myMirror.setValue(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : monoValueProxyBase.getMirror(), monoNewValueProxyBase.getMirror());
	}

	@Override
	public boolean isArrayProperty()
	{
		return myMirror.isArrayProperty();
	}

	@Nullable
	@Override
	public DotNetMethodProxy getGetMethod()
	{
		MethodMirror methodMirror = myMirror.methodGet();
		return methodMirror == null ? null : new MonoMethodProxy(methodMirror);
	}
}
