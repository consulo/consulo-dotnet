package consulo.dotnet.microsoft.debugger.proxy;

import org.consulo.util.pointers.Named;
import org.jetbrains.annotations.NotNull;
import consulo.dotnet.debugger.proxy.DotNetVariableProxy;
import mssdw.MirrorWithIdAndName;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public abstract class MicrosoftVariableProxyBase<T extends MirrorWithIdAndName> implements Named, DotNetVariableProxy
{
	protected T myMirror;

	public MicrosoftVariableProxyBase(@NotNull T mirror)
	{
		myMirror = mirror;
	}

	@NotNull
	public T getMirror()
	{
		return myMirror;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MicrosoftVariableProxyBase && myMirror.equals(((MicrosoftVariableProxyBase) obj).myMirror);
	}

	@Override
	public int hashCode()
	{
		return myMirror.hashCode();
	}

	@NotNull
	@Override
	public String getName()
	{
		return myMirror.name();
	}
}
