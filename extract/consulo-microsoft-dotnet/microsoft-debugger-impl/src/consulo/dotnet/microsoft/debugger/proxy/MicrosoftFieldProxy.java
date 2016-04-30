package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Getter;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.util.BitUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetFieldInfoRequest;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetFieldValueRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetFieldInfoRequestResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetTypeInfoRequestResult;
import edu.arizona.cs.mbel.signature.FieldAttributes;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftFieldProxy implements DotNetFieldProxy
{
	private MicrosoftDebuggerClient myClient;
	private MicrosoftTypeProxy myParentType;
	private GetTypeInfoRequestResult.FieldInfo myField;

	private GetFieldInfoRequestResult myResult;

	private NotNullLazyValue<Getter<DotNetTypeProxy>> myTypeValue = new NotNullLazyValue<Getter<DotNetTypeProxy>>()
	{
		@NotNull
		@Override
		protected Getter<DotNetTypeProxy> compute()
		{
			return MicrosoftTypeProxy.lazyOf(myClient, info().Type);
		}
	};

	public MicrosoftFieldProxy(MicrosoftDebuggerClient client, MicrosoftTypeProxy parentType, GetTypeInfoRequestResult.FieldInfo field)
	{
		myClient = client;
		myParentType = parentType;
		myField = field;
	}

	@NotNull
	@Override
	public DotNetTypeProxy getParentType()
	{
		return myParentType;
	}

	@Override
	public boolean isStatic()
	{
		return BitUtil.isSet(info().Attributes, FieldAttributes.Static);
	}

	@Nullable
	@Override
	public DotNetValueProxy getValue(@NotNull DotNetThreadProxy threadProxy, @Nullable DotNetValueProxy proxy)
	{
		MicrosoftObjectValueProxy objectValueProxy = (MicrosoftObjectValueProxy) proxy;
		int objectId = objectValueProxy == null ? 0 : objectValueProxy.getResult().ObjectId;
		return MicrosoftValueProxyUtil.sendAndReceive(myClient, new GetFieldValueRequest((int) threadProxy.getId(), 0, myParentType.getTypeRef(), objectId, myField.Token));
	}

	@Override
	public void setValue(@NotNull DotNetThreadProxy threadProxy, @Nullable DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy)
	{
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return myTypeValue.getValue().get();
	}

	@NotNull
	@Override
	public String getName()
	{
		return myField.Name;
	}

	@Override
	public boolean isLiteral()
	{
		return BitUtil.isSet(info().Attributes, FieldAttributes.Literal);
	}

	@NotNull
	private GetFieldInfoRequestResult info()
	{
		if(myResult != null)
		{
			return myResult;
		}
		return myResult = myClient.sendAndReceive(new GetFieldInfoRequest(myParentType.getTypeRef(), myField.Token), GetFieldInfoRequestResult.class);
	}
}
