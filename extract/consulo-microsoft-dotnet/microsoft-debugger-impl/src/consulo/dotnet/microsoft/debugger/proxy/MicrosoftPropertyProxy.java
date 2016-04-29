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
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetTypeInfoRequestResult;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MicrosoftPropertyProxy implements DotNetPropertyProxy
{
	private MicrosoftDebuggerClient myClient;
	private MicrosoftTypeProxy myParentType;
	private GetTypeInfoRequestResult.PropertyInfo myProperty;

	private Getter<DotNetTypeProxy> myType;

	public MicrosoftPropertyProxy(MicrosoftDebuggerClient client, MicrosoftTypeProxy parentType, GetTypeInfoRequestResult.PropertyInfo property)
	{
		myClient = client;
		myParentType = parentType;
		myProperty = property;
		myType = MicrosoftTypeProxy.lazyOf(myClient, myProperty.Type);
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}

	@NotNull
	@Override
	public MicrosoftTypeProxy getParentType()
	{
		return myParentType;
	}

	@Nullable
	@Override
	public DotNetValueProxy getValue(@NotNull DotNetThreadProxy threadProxy, @Nullable DotNetValueProxy proxy)
	{
		return null;
		//MicrosoftObjectValueProxy objectValueProxy = (MicrosoftObjectValueProxy) proxy;
		//return MicrosoftValueProxyUtil.sendAndReceive(myClient, new GetFieldValueRequest(objectValueProxy.getResult().ObjectId, myProperty.Token));
	}

	@Override
	public void setValue(@NotNull DotNetThreadProxy threadProxy, @Nullable DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy)
	{
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
		return myProperty.Name;
	}

	@Override
	public boolean isArrayProperty()
	{
		return false;
	}

	@Nullable
	@Override
	public DotNetMethodProxy getGetMethod()
	{
		return null;
	}
}