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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.NullableLazyValue;
import consulo.dotnet.debugger.proxy.DotNetInvalidObjectException;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetArgumentRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetFramesRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
@Deprecated
public class MicrosoftStackFrameProxyOld implements DotNetStackFrameProxy
{
	private NullableLazyValue<DotNetSourceLocation> myLocationValue = new NullableLazyValue<DotNetSourceLocation>()
	{
		@Nullable
		@Override
		protected DotNetSourceLocation compute()
		{
			GetFramesRequestResult.FrameInfo.SourcePosition position = myFrame.Position;
			if(position != null)
			{
				return new MicrosoftSourceLocationOld(myClient, position, myFrame.Type, myFrame.FunctionToken);
			}
			return null;
		}
	};

	private MicrosoftDebuggerClient myClient;
	private MicrosoftThreadProxyOld myThreadProxy;
	private int myIndex;
	private GetFramesRequestResult.FrameInfo myFrame;

	public MicrosoftStackFrameProxyOld(MicrosoftDebuggerClient client, MicrosoftThreadProxyOld threadProxy, int index, GetFramesRequestResult.FrameInfo frame)
	{
		myClient = client;
		myThreadProxy = threadProxy;
		myIndex = index;
		myFrame = frame;
	}

	@Override
	public int getIndex()
	{
		return myIndex;
	}

	@NotNull
	@Override
	public DotNetThreadProxy getThread()
	{
		return myThreadProxy;
	}

	@NotNull
	@Override
	public Object getEqualityObject()
	{
		return myFrame.FunctionToken;
	}

	@Nullable
	@Override
	public DotNetSourceLocation getSourceLocation()
	{
		return myLocationValue.getValue();
	}

	@NotNull
	@Override
	public DotNetValueProxy getThisObject() throws DotNetInvalidObjectException
	{
		if(isStaticFrame())
		{
			return new MicrosoftNullValueProxy();
		}

		DotNetValueProxy valueProxy = MicrosoftValueProxyUtilOld.sendAndReceive(myClient, new GetArgumentRequest((int) myThreadProxy.getId(), myIndex, 0));
		return valueProxy == null ? new MicrosoftNullValueProxy() : valueProxy;
	}

	private boolean isStaticFrame()
	{
		DotNetSourceLocation sourceLocation = getSourceLocation();
		if(sourceLocation == null)
		{
			return true;
		}

		if(sourceLocation.getMethod().isStatic())
		{
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public DotNetValueProxy getParameterValue(@NotNull DotNetMethodParameterProxy parameterProxy)
	{
		int parameterIndex = parameterProxy.getIndex();
		if(!isStaticFrame())
		{
			// zero is this
			parameterIndex++;
		}
		return MicrosoftValueProxyUtilOld.sendAndReceive(myClient, new GetArgumentRequest((int) myThreadProxy.getId(), myIndex, parameterIndex));
	}

	@Override
	public void setParameterValue(@NotNull DotNetMethodParameterProxy parameterProxy, @NotNull DotNetValueProxy valueProxy)
	{
	}

	@Nullable
	@Override
	public DotNetValueProxy getLocalValue(@NotNull DotNetLocalVariableProxy localVariableProxy)
	{
		return null;
	}

	@Override
	public void setLocalValue(@NotNull DotNetLocalVariableProxy localVariableProxy, @NotNull DotNetValueProxy valueProxy)
	{

	}
}
