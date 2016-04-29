package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Getter;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.openapi.util.text.StringUtil;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.TypeRef;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetTypeInfoRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetTypeInfoRequestResult;

/**
 * @author VISTALL
 * @since 20.04.2016
 */
public class MicrosoftTypeProxyPointer implements Getter<DotNetTypeProxy>
{
	private NullableLazyValue<DotNetTypeProxy> myValue = new NullableLazyValue<DotNetTypeProxy>()
	{
		@Nullable
		@Override
		protected DotNetTypeProxy compute()
		{
			GetTypeInfoRequestResult requestResult = myClient.sendAndReceive(new GetTypeInfoRequest(myTypeRef), GetTypeInfoRequestResult.class);
			if(StringUtil.isEmpty(requestResult.Name))
			{
				return null;
			}
			return new MicrosoftTypeProxy(myClient, myTypeRef, requestResult);
		}
	};

	private MicrosoftDebuggerClient myClient;
	private TypeRef myTypeRef;

	public MicrosoftTypeProxyPointer(MicrosoftDebuggerClient client, TypeRef typeRef)
	{
		myClient = client;
		myTypeRef = typeRef;
	}

	@Nullable
	@Override
	public DotNetTypeProxy get()
	{
		if(myTypeRef == null)
		{
			return null;
		}
		int classToken = myTypeRef.ClassToken;
		if(classToken <= 0)
		{
			return null;
		}

		return myValue.getValue();
	}
}
