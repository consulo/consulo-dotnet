package consulo.dotnet.microsoft.debugger.protocol.serverMessage;

import consulo.dotnet.microsoft.debugger.protocol.TypeRef;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class ObjectValueResult
{
	public int Id;
	public long Address;
	public int ObjectId;
	public TypeRef Type;
}
