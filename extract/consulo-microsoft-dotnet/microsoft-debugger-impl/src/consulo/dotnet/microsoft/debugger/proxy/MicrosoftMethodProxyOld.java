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
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Getter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.BitUtil;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.TypeRef;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetLocalsRequest;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetMethodInfoRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetLocalsRequestResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetMethodInfoRequestResult;
import edu.arizona.cs.mbel.signature.MethodAttributes;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
@Deprecated
public class MicrosoftMethodProxyOld implements DotNetMethodProxy
{
	private MicrosoftDebuggerClient myClient;
	private TypeRef myTypeRef;
	private int myFunctionToken;

	private GetMethodInfoRequestResult myResult;

	private Getter<DotNetTypeProxy> myDeclarationType;

	public MicrosoftMethodProxyOld(MicrosoftDebuggerClient client, TypeRef typeRef, int functionToken)
	{
		myClient = client;
		myTypeRef = typeRef;
		myFunctionToken = functionToken;
		myDeclarationType = MicrosoftTypeProxyOld.lazyOf(myClient, myTypeRef);
	}

	@Override
	public boolean isStatic()
	{
		return BitUtil.isSet(info().Attributes, MethodAttributes.Static);
	}

	@NotNull
	@Override
	public DotNetTypeProxy getDeclarationType()
	{
		DotNetTypeProxy proxy = myDeclarationType.get();
		assert proxy != null;
		return proxy;
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
			proxies[i] = new MicrosoftMethodParameterProxyOld(myClient, i, parameter);
		}
		return proxies;
	}

	@NotNull
	@Override
	public DotNetLocalVariableProxy[] getLocalVariables(@NotNull DotNetStackFrameProxy frameProxy)
	{
		GetLocalsRequestResult result = myClient.sendAndReceive(new GetLocalsRequest((int) frameProxy.getThread().getId(), frameProxy.getIndex()), GetLocalsRequestResult.class);

		DotNetLocalVariableProxy[] proxies = new DotNetLocalVariableProxy[result.Locals.length];
		for(int i = 0; i < result.Locals.length; i++)
		{
			GetLocalsRequestResult.LocalInfo local = result.Locals[i];
			proxies[i] = new MicrosoftLocalVariableProxyOld(myClient, local);
		}
		return proxies;
	}

	@Nullable
	@Override
	public DotNetValueProxy invoke(@NotNull DotNetStackFrameProxy threadMirror, @Nullable DotNetValueProxy thisObject, @NotNull DotNetValueProxy... arguments)
	{
		return null;
	}

	@RequiredReadAction
	@Override
	public PsiElement findExecutableElementFromDebugInfo(@NotNull Project project, int executableChildrenAtLineIndex)
	{
		return null;
	}

	@NotNull
	@Override
	public String getName()
	{
		return StringUtil.notNullize(info().Name);
	}

	@NotNull
	private GetMethodInfoRequestResult info()
	{
		if(myResult != null)
		{
			return myResult;
		}
		return myResult = myClient.sendAndReceive(new GetMethodInfoRequest(myTypeRef, myFunctionToken), GetMethodInfoRequestResult.class);
	}
}
