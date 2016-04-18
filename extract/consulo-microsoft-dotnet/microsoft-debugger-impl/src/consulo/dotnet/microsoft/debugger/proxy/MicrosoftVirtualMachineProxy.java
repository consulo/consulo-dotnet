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
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetCharValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNullValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNumberValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetThreadsRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetThreadsRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftVirtualMachineProxy implements DotNetVirtualMachineProxy
{
	private MicrosoftDebuggerClient myContext;

	public MicrosoftVirtualMachineProxy(MicrosoftDebuggerClient context)
	{
		myContext = context;
	}

	@NotNull
	@Override
	public List<DotNetThreadProxy> getThreads()
	{
		List<DotNetThreadProxy> proxies = new ArrayList<DotNetThreadProxy>();

		GetThreadsRequestResult result = myContext.sendAndReceive(new GetThreadsRequest(), GetThreadsRequestResult.class);

		for(GetThreadsRequestResult.ThreadInfo thread : result.Threads)
		{
			proxies.add(new MicrosoftThreadProxy(thread.Id, thread.Name, myContext));
		}
		return proxies;
	}

	@NotNull
	@Override
	public DotNetStringValueProxy createStringValue(@NotNull String value)
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetCharValueProxy createCharValue(char value)
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetBooleanValueProxy createBooleanValue(boolean value)
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetNumberValueProxy createNumberValue(int tag, @NotNull Number value)
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetNullValueProxy createNullValue()
	{
		return null;
	}
}
