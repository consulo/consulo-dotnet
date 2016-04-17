/*
 * Copyright 2013-2016 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.dotnet.microsoft.debugger;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetFramesRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetFramesRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftExecutionStack extends XExecutionStack
{
	private int myThreadId;

	private MicrosoftStackFrame[] myFrames;

	public MicrosoftExecutionStack(MicrosoftDebuggerClientContext context, String displayName, int threadId)
	{
		super(displayName);
		myThreadId = threadId;

		GetFramesRequestResult o = context.sendAndReceive(new GetFramesRequest(threadId));

		GetFramesRequestResult.FrameInfo[] frames = o.Frames;
		myFrames = new MicrosoftStackFrame[frames.length];
		for(int i = 0; i < frames.length; i++)
		{
			myFrames[i] = new MicrosoftStackFrame(i, frames[i]);
		}
	}

	@Nullable
	@Override
	public XStackFrame getTopFrame()
	{
		if(myFrames.length > 0)
		{
			return myFrames[0];
		}
		return null;
	}

	@Override
	public void computeStackFrames(XStackFrameContainer container)
	{
		container.addStackFrames(Arrays.asList(myFrames), true);
	}

	@Override
	public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container)
	{
		throw new IllegalArgumentException();
	}

	public int getThreadId()
	{
		return myThreadId;
	}
}
