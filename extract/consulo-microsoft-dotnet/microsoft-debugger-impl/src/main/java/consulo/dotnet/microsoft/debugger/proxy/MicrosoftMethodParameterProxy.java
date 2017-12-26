package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import mssdw.MethodParameterMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftMethodParameterProxy implements DotNetMethodParameterProxy
{
	private final MethodParameterMirror myMethodParameterMirror;
	private DotNetTypeProxy myTypeProxy;

	public MicrosoftMethodParameterProxy(MethodParameterMirror methodParameterMirror)
	{
		myMethodParameterMirror = methodParameterMirror;
	}

	public MethodParameterMirror getMirror()
	{
		return myMethodParameterMirror;
	}

	@Override
	public int getIndex()
	{
		return myMethodParameterMirror.id();
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		if(myTypeProxy != null)
		{
			return myTypeProxy;
		}
		return myTypeProxy = MicrosoftTypeProxy.of(myMethodParameterMirror.type());
	}

	@NotNull
	@Override
	public String getName()
	{
		return myMethodParameterMirror.name();
	}
}
