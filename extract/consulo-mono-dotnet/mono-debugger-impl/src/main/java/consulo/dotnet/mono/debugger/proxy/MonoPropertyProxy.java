package consulo.dotnet.mono.debugger.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThrowValueException;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mono.debugger.MethodMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.ThrowValueException;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MonoPropertyProxy extends MonoVariableProxyBase<PropertyMirror> implements DotNetPropertyProxy
{
	public MonoPropertyProxy(@Nonnull PropertyMirror mirror)
	{
		super(mirror);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MonoTypeProxy.of(myMirror.type());
	}

	@Nonnull
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
	public DotNetValueProxy getValue(@Nonnull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy)
	{
		try
		{
			MonoThreadProxy monoThreadProxy = (MonoThreadProxy) frameProxy.getThread();
			MonoValueProxyBase<?> monoValueProxyBase = (MonoValueProxyBase<?>) proxy;
			return MonoValueProxyUtil.wrap(myMirror.value(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : (ObjectValueMirror) monoValueProxyBase.getMirror()));
		}
		catch(ThrowValueException e)
		{
			throw new DotNetThrowValueException(frameProxy, MonoValueProxyUtil.wrap(e.getThrowExceptionValue()));
		}
	}

	@Override
	public void setValue(@Nonnull DotNetStackFrameProxy threadProxy, @Nullable DotNetValueProxy proxy, @Nonnull DotNetValueProxy newValueProxy)
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
