package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetLocalsRequestResult;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftLocalVariableProxy implements DotNetLocalVariableProxy
{
	private GetLocalsRequestResult.LocalInfo myLocal;

	public MicrosoftLocalVariableProxy(GetLocalsRequestResult.LocalInfo local)
	{
		myLocal = local;
	}

	public int getIndex()
	{
		return myLocal.Index;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return null;
	}

	@NotNull
	@Override
	public String getName()
	{
		return myLocal.Name;
	}
}
