package consulo.dotnet.microsoft.debugger.protocol.clientMessage;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class FindTypeInfoRequest
{
	public final String VmQName;

	public FindTypeInfoRequest(String vmQName)
	{
		VmQName = vmQName;
	}
}
