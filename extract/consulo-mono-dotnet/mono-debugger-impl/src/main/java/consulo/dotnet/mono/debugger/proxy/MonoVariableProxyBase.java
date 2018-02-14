package consulo.dotnet.mono.debugger.proxy;

import javax.annotation.Nonnull;
import consulo.dotnet.debugger.proxy.DotNetVariableProxy;
import consulo.util.pointers.Named;
import mono.debugger.MirrorWithIdAndName;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public abstract class MonoVariableProxyBase<T extends MirrorWithIdAndName> implements Named, DotNetVariableProxy
{
	protected T myMirror;

	public MonoVariableProxyBase(@Nonnull T mirror)
	{
		myMirror = mirror;
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
