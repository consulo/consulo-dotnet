package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.util.BitUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetFieldValueRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetTypeInfoRequestResult;
import edu.arizona.cs.mbel.signature.FieldAttributes;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftFieldProxy implements DotNetFieldProxy
{
	private MicrosoftDebuggerClient myClient;
	private GetTypeInfoRequestResult.FieldInfo myField;

	public MicrosoftFieldProxy(MicrosoftDebuggerClient client, GetTypeInfoRequestResult.FieldInfo field)
	{
		myClient = client;
		myField = field;
	}

	@Override
	public boolean isStatic()
	{
		return BitUtil.isSet(myField.Attributes, FieldAttributes.Static);
	}

	@Nullable
	@Override
	public DotNetValueProxy getValue(@NotNull DotNetThreadProxy threadProxy, @NotNull DotNetValueProxy proxy)
	{
		MicrosoftObjectValueProxy objectValueProxy = (MicrosoftObjectValueProxy) proxy;
		return MicrosoftValueProxyUtil.sendAndReceive(myClient, new GetFieldValueRequest(objectValueProxy.getResult().ObjectId, myField.Token));
	}

	@Override
	public void setValue(@NotNull DotNetThreadProxy threadProxy, @NotNull DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy)
	{
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MicrosoftTypeProxy.of(myClient, myField.Type);
	}

	@NotNull
	@Override
	public String getName()
	{
		return myField.Name;
	}
}
