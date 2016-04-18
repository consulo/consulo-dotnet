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
import consulo.dotnet.microsoft.debugger.protocol.TypeRef;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetMethodInfoRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetMethodInfoRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftMethodProxy implements DotNetMethodProxy
{
	private MicrosoftDebuggerClientContext myContext;
	private TypeRef myTypeRef;
	private int myFunctionToken;

	private GetMethodInfoRequestResult myResult;

	private DotNetTypeProxy myDeclarationType;

	public MicrosoftMethodProxy(MicrosoftDebuggerClientContext context, TypeRef typeRef, int functionToken)
	{
		myContext = context;
		myTypeRef = typeRef;
		myFunctionToken = functionToken;
	}

	@NotNull
	@Override
	public DotNetTypeProxy getDeclarationType()
	{
		if(myDeclarationType != null)
		{
			return myDeclarationType;
		}
		return myDeclarationType = new MicrosoftTypeProxy(myContext, myTypeRef);
	}

	@NotNull
	@Override
	public DotNetMethodParameterProxy[] getParameters()
	{
		GetMethodInfoRequestResult.ParameterInfo[] parameters = info().Parameters;
		DotNetMethodParameterProxy[] proxies = new DotNetMethodParameterProxy[parameters.length];
		for(int i = 0; i < parameters.length; i++)
		{
			GetMethodInfoRequestResult.ParameterInfo parameter = parameters[i];
			proxies[i] = new MicrosoftMethodParameterProxy(myContext, i, parameter);
		}
		return proxies;
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
		return myResult = myContext.sendAndReceive(new GetMethodInfoRequest(myTypeRef, myFunctionToken), GetMethodInfoRequestResult.class);
	}
}
