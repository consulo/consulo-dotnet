package consulo.dotnet.microsoft.debugger.protocol.clientMessage;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class GetLocalValueRequest
{
	public int ThreadId;

	public int StackFrameIndex;

	public int Index;

	public GetLocalValueRequest(int threadId, int stackFrameIndex, int index)
	{
		ThreadId = threadId;
		StackFrameIndex = stackFrameIndex;
		Index = index;
	}
}
