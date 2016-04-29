package consulo.dotnet.microsoft.debugger.protocol.clientMessage;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class GetFieldValueRequest
{
	public int ObjectId;

	public int FieldToken;

	public GetFieldValueRequest(int objectId, int fieldToken)
	{
		ObjectId = objectId;
		FieldToken = fieldToken;
	}
}
