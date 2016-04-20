package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joou.UByte;
import org.joou.UInteger;
import org.joou.ULong;
import org.joou.UShort;
import consulo.dotnet.debugger.nodes.TypeTag;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNumberValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.NumberValueResult;
import edu.arizona.cs.mbel.signature.SignatureConstants;

/**
 * @author VISTALL
 * @since 20.04.2016
 */
public class MicrosoftNumberValueProxy extends MicrosoftValueProxyBase<NumberValueResult> implements DotNetNumberValueProxy
{
	private MicrosoftDebuggerClient myClient;

	public MicrosoftNumberValueProxy(MicrosoftDebuggerClient client, NumberValueResult result)
	{
		super(result);
		myClient = client;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		String type = TypeTag.typeByTag(myResult.Type);
		return MicrosoftTypeProxy.of(myClient, type);
	}

	@NotNull
	@Override
	public Number getValue()
	{
		switch(myResult.Type)
		{
			case SignatureConstants.ELEMENT_TYPE_I:
				return Long.parseLong(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_U:
				return ULong.valueOf(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_I1:
				return Byte.parseByte(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_U1:
				return UByte.valueOf(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_I2:
				return Short.parseShort(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_U2:
				return UShort.valueOf(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_I4:
				return Integer.parseInt(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_U4:
				return UInteger.valueOf(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_I8:
				return Long.parseLong(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_U8:
				return ULong.valueOf(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_R4:
				return Float.parseFloat(myResult.Value);
			case SignatureConstants.ELEMENT_TYPE_R8:
				return Double.parseDouble(myResult.Value);
		}
		throw new IllegalArgumentException(String.valueOf(myResult.Type));
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitNumberValue(this);
	}
}
