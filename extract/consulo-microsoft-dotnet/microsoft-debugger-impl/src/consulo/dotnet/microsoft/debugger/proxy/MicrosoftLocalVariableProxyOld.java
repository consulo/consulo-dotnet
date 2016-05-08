package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Getter;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetLocalsRequestResult;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
@Deprecated
public class MicrosoftLocalVariableProxyOld implements DotNetLocalVariableProxy
{
	private GetLocalsRequestResult.LocalInfo myLocal;
	private Getter<DotNetTypeProxy> myType;

	public MicrosoftLocalVariableProxyOld(MicrosoftDebuggerClient client, GetLocalsRequestResult.LocalInfo local)
	{
		myLocal = local;
		myType = MicrosoftTypeProxyOld.lazyOf(client, local.Type);
	}

	public int getIndex()
	{
		return myLocal.Index;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return myType.get();
	}

	@NotNull
	@Override
	public String getName()
	{
		return myLocal.Name;
	}
}
