package consulo.dotnet.microsoft.debugger;

import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetThreadsRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetThreadsRequestResult;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class MicrosoftSuspendContext extends XSuspendContext
{
	private MicrosoftDebuggerClientContext myContext;
	private MicrosoftExecutionStack[] myExecutionStacks;
	private int myActiveThreadId;

	public MicrosoftSuspendContext(MicrosoftDebuggerClientContext context)
	{
		myContext = context;
		GetThreadsRequestResult result = myContext.sendAndReceive(new GetThreadsRequest());
		myActiveThreadId = result.ActiveThreadId;
		GetThreadsRequestResult.ThreadInfo[] threads = result.Threads;
		myExecutionStacks = new MicrosoftExecutionStack[threads.length];
		for(int i = 0; i < threads.length; i++)
		{
			GetThreadsRequestResult.ThreadInfo threadInfo = threads[i];
			myExecutionStacks[i] = new MicrosoftExecutionStack(context, "[" + threadInfo.Id + "]", threadInfo.Id);
		}
	}

	@Nullable
	@Override
	public XExecutionStack getActiveExecutionStack()
	{
		for(MicrosoftExecutionStack executionStack : myExecutionStacks)
		{
			if(executionStack.getThreadId() == myActiveThreadId)
			{
				return executionStack;
			}
		}
		return null;
	}

	@Override
	public MicrosoftExecutionStack[] getExecutionStacks()
	{
		return myExecutionStacks;
	}
}
