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
import com.intellij.openapi.util.Getter;
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetMethodInfoRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
@Deprecated
public class MicrosoftMethodParameterProxyOld implements DotNetMethodParameterProxy
{
	private int myIndex;
	private GetMethodInfoRequestResult.ParameterInfo myParameterInfo;
	private Getter<DotNetTypeProxy> myType;

	public MicrosoftMethodParameterProxyOld(MicrosoftDebuggerClient context, int index, GetMethodInfoRequestResult.ParameterInfo parameterInfo)
	{
		myIndex = index;
		myParameterInfo = parameterInfo;
		myType = MicrosoftTypeProxyOld.lazyOf(context, myParameterInfo.Type);
	}

	@Override
	public int getIndex()
	{
		return myIndex;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return myType.get();
	}

	@NotNull
	@Override
	public String getName()
	{
		return myParameterInfo.Name;
	}
}
