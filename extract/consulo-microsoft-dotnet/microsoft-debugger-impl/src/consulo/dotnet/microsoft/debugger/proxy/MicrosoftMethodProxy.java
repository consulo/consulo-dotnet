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
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClientContext;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetMethodInfoRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetMethodInfoRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftMethodProxy implements DotNetMethodProxy
{
	private MicrosoftDebuggerClientContext myContext;
	private int myModuleToken;
	private int myClassToken;
	private int myFunctionToken;

	private GetMethodInfoRequestResult myResult;

	public MicrosoftMethodProxy(MicrosoftDebuggerClientContext context, int moduleToken, int classToken, int functionToken)
	{
		myContext = context;
		myModuleToken = moduleToken;
		myClassToken = classToken;
		myFunctionToken = functionToken;
	}

	@NotNull
	@Override
	public DotNetTypeProxy getDeclarationType()
	{
		return new MicrosoftTypeProxy(myContext, myModuleToken, myClassToken);
	}

	@NotNull
	@Override
	public DotNetMethodParameterProxy[] getParameters()
	{
		return new DotNetMethodParameterProxy[0];
	}

	@NotNull
	@Override
	public String getName()
	{
		return info().Name;
	}

	@NotNull
	private GetMethodInfoRequestResult info()
	{
		if(myResult != null)
		{
			return myResult;
		}
		return myResult = myContext.sendAndReceive(new GetMethodInfoRequest(myModuleToken, myClassToken, myFunctionToken), GetMethodInfoRequestResult.class);
	}
}
