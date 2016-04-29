package consulo.dotnet.microsoft.debugger.protocol.clientMessage;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class GetLocalsRequest
{
	public int ThreadId;

	public int StackFrameIndex;

	public GetLocalsRequest(int threadId, int stackFrameIndex)
	{
		ThreadId = threadId;
		StackFrameIndex = stackFrameIndex;
	}
}
