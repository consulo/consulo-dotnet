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

package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClientContext;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetFramesRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftStackFrameProxy implements DotNetStackFrameProxy
{
	private MicrosoftDebuggerClientContext myContext;
	private int myIndex;
	private GetFramesRequestResult.FrameInfo myFrame;

	public MicrosoftStackFrameProxy(MicrosoftDebuggerClientContext context, int index, GetFramesRequestResult.FrameInfo frame)
	{
		myContext = context;
		myIndex = index;
		myFrame = frame;
	}

	@Override
	public int getIndex()
	{
		return myIndex;
	}

	@Override
	public Object getEqualityObject()
	{
		return myFrame.FunctionToken;
	}

	@Nullable
	@Override
	public DotNetSourceLocation getSourceLocation()
	{
		GetFramesRequestResult.FrameInfo.SourcePosition position = myFrame.Position;
		if(position != null)
		{
			return new MicrosoftSourceLocation(myContext, position, myFrame.ModuleToken, myFrame.ClassToken, myFrame.FunctionToken);
		}
		return null;
	}
}
