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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.text.StringUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.TypeRef;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetTypeInfoRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetTypeInfoRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftTypeProxy implements DotNetTypeProxy
{
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static MicrosoftTypeProxy of(@NotNull MicrosoftDebuggerClient client, @Nullable TypeRef typeRef)
	{
		if(typeRef == null)
		{
			return null;
		}
		int classToken = typeRef.ClassToken;
		if(classToken <= 0)
		{
			return null;
		}
		return new MicrosoftTypeProxy(client, typeRef);
	}

	private MicrosoftDebuggerClient myClient;
	private TypeRef myTypeRef;

	private GetTypeInfoRequestResult myResult;

	private MicrosoftTypeProxy(MicrosoftDebuggerClient client, TypeRef typeRef)
	{
		myClient = client;
		myTypeRef = typeRef;
	}

	@NotNull
	@Override
	public String getName()
	{
		return StringUtil.getShortName(info().Name);
	}

	@NotNull
	@Override
	public String getFullName()
	{
		return info().Name;
	}

	@Override
	public boolean isArray()
	{
		return false;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getBaseType()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetFieldProxy[] getFields()
	{
		GetTypeInfoRequestResult.FieldInfo[] fields = info().Fields;

		MicrosoftFieldProxy[] fieldProxies = new MicrosoftFieldProxy[fields.length];
		for(int i = 0; i < fields.length; i++)
		{
			GetTypeInfoRequestResult.FieldInfo field = fields[i];
			fieldProxies[i] = new MicrosoftFieldProxy(myClient, field);
		}
		return fieldProxies;
	}

	@NotNull
	@Override
	public DotNetPropertyProxy[] getProperties()
	{
		return new DotNetPropertyProxy[0];
	}

	@Override
	public boolean isNested()
	{
		return false;
	}

	@Nullable
	@Override
	public DotNetMethodProxy findMethodByName(@NotNull String name, boolean deep)
	{
		return null;
	}

	@NotNull
	private GetTypeInfoRequestResult info()
	{
		if(myResult != null)
		{
			return myResult;
		}
		return myResult = myClient.sendAndReceive(new GetTypeInfoRequest(myTypeRef), GetTypeInfoRequestResult.class);
	}
}
