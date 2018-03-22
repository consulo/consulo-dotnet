package consulo.dotnet.mono.debugger.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.util.NullableLazyValue;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.DotNetVariableProxy;
import consulo.util.pointers.Named;
import mono.debugger.MirrorWithIdAndName;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public abstract class MonoVariableProxyBase<T extends MirrorWithIdAndName> implements Named, DotNetVariableProxy
{
	private NullableLazyValue<DotNetTypeProxy> myTypeValue = NullableLazyValue.of(() -> MonoTypeProxy.of(fetchType()));
	protected T myMirror;

	public MonoVariableProxyBase(@Nonnull T mirror)
	{
		myMirror = mirror;
	}

	@Nullable
	protected abstract TypeMirror fetchType();

	@Nullable
	@Override
	public final DotNetTypeProxy getType()
	{
		return myTypeValue.getValue();
	}

	@Nonnull
	public T getMirror()
	{
		return myMirror;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MonoVariableProxyBase && myMirror.equals(((MonoVariableProxyBase) obj).myMirror);
	}

	@Override
	public int hashCode()
	{
		return myMirror.hashCode();
	}

	@Nonnull
	@Override
	public String getName()
	{
		return myMirror.name();
	}
}
