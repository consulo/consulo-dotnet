package consulo.dotnet.microsoft.debugger.protocol.clientMessage;

/**
 * @author VISTALL
 * @since 01.05.2016
 */
public class GetOrSetArrayValueAtRequest
{
	public int ObjectId;

	public int Index;

	public int AnotherObjectId;

	public GetOrSetArrayValueAtRequest(int objectId, int index, int anotherObjectId)
	{
		ObjectId = objectId;
		Index = index;
		AnotherObjectId = anotherObjectId;
	}
}
