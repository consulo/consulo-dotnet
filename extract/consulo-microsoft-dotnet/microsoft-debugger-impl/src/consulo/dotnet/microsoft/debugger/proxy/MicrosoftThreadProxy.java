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

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetFramesRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetFramesRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftThreadProxy implements DotNetThreadProxy
{
	private int myId;
	private String myName;
	private MicrosoftDebuggerClient myContext;

	public MicrosoftThreadProxy(int id, String name, MicrosoftDebuggerClient context)
	{
		myId = id;
		myName = name;
		myContext = context;
	}

	@Override
	public long getId()
	{
		return myId;
	}

	@Override
	public boolean isRunning()
	{
		return false;
	}

	@Nullable
	@Override
	public String getName()
	{
		return myName;
	}

	@NotNull
	@Override
	public List<DotNetStackFrameProxy> getFrames()
	{
		GetFramesRequestResult o = myContext.sendAndReceive(new GetFramesRequest(myId), GetFramesRequestResult.class);
		GetFramesRequestResult.FrameInfo[] frames = o.Frames;
		List<DotNetStackFrameProxy> proxies = new ArrayList<DotNetStackFrameProxy>(frames.length);
		for(int i = 0; i < frames.length; i++)
		{
			GetFramesRequestResult.FrameInfo frame = frames[i];

			proxies.add(new MicrosoftStackFrameProxy(myContext, this, i, frame));
		}
		return proxies;
	}
}
