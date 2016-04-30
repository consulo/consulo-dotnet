package consulo.dotnet.microsoft.debugger.protocol.clientMessage;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.microsoft.debugger.protocol.TypeRef;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class GetFieldValueRequest
{
	public int ThreadId;

	public int StackFrameIndex;

	public final TypeRef Type;

	public int ObjectId;

	public int FieldToken;

	public GetFieldValueRequest(int threadId, int stackFrameIndex, @NotNull TypeRef typeRef, int objectId, int fieldToken)
	{
		ThreadId = threadId;
		StackFrameIndex = stackFrameIndex;
		Type = typeRef;
		ObjectId = objectId;
		FieldToken = fieldToken;
	}
}
