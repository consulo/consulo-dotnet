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
import com.intellij.openapi.util.text.StringUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.TypeRef;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.FindTypeInfoRequest;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.GetTypeInfoRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.FindTypeInfoRequestResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetTypeInfoRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
@Deprecated
public class MicrosoftTypeProxyOld implements DotNetTypeProxy
{
	@Nullable
	public static MicrosoftTypeProxyOld byVmQName(@NotNull MicrosoftDebuggerClient client, @NotNull String vmQName)
	{
		FindTypeInfoRequestResult result = client.sendAndReceive(new FindTypeInfoRequest(vmQName), FindTypeInfoRequestResult.class);
		if(result.Type == null || result.Type.ClassToken <= 0)
		{
			return null;
		}
		GetTypeInfoRequestResult requestResult = client.sendAndReceive(new GetTypeInfoRequest(result.Type), GetTypeInfoRequestResult.class);
		if(StringUtil.isEmpty(requestResult.Name))
		{
			return null;
		}
		return new MicrosoftTypeProxyOld(client, result.Type, requestResult);
	}

	@NotNull
	public static Getter<DotNetTypeProxy> lazyOf(@NotNull final MicrosoftDebuggerClient client, @Nullable final TypeRef typeRef)
	{
		return new MicrosoftTypeProxyPointer(client, typeRef);
	}

	private MicrosoftDebuggerClient myClient;

	private TypeRef myTypeRef;

	private GetTypeInfoRequestResult myResult;

	private Getter<DotNetTypeProxy> myBaseTypeGetter;

	protected MicrosoftTypeProxyOld(MicrosoftDebuggerClient client, TypeRef typeRef, GetTypeInfoRequestResult requestResult)
	{
		myClient = client;
		myTypeRef = typeRef;
		myResult = requestResult;
		myBaseTypeGetter = lazyOf(client, requestResult.BaseType);
	}

	@Override
	public boolean isAnnotatedBy(@NotNull String attributeVmQName)
	{
		return false;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getDeclarationType()
	{
		return null;
	}

	@NotNull
	@Override
	public String getName()
	{
		return StringUtil.notNullize(myResult.Name);
	}

	@NotNull
	@Override
	public String getFullName()
	{
		return StringUtil.notNullize(myResult.FullName);
	}

	@Override
	public boolean isArray()
	{
		return myResult.IsArray;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getBaseType()
	{
		return myBaseTypeGetter.get();
	}

	@NotNull
	@Override
	public DotNetTypeProxy[] getInterfaces()
	{
		return new DotNetTypeProxy[0];
	}

	@NotNull
	@Override
	public DotNetFieldProxy[] getFields()
	{
		GetTypeInfoRequestResult.FieldInfo[] fields = myResult.Fields;

		MicrosoftFieldProxy[] fieldProxies = new MicrosoftFieldProxy[fields.length];
		for(int i = 0; i < fields.length; i++)
		{
			GetTypeInfoRequestResult.FieldInfo field = fields[i];
			fieldProxies[i] = new MicrosoftFieldProxy(myClient, this, field);
		}
		return fieldProxies;
	}

	@NotNull
	@Override
	public DotNetPropertyProxy[] getProperties()
	{
		GetTypeInfoRequestResult.PropertyInfo[] properties = myResult.Properties;

		MicrosoftPropertyProxy[] propertyProxies = new MicrosoftPropertyProxy[properties.length];
		for(int i = 0; i < properties.length; i++)
		{
			GetTypeInfoRequestResult.PropertyInfo property = properties[i];
			propertyProxies[i] = new MicrosoftPropertyProxy(myClient, this, property);
		}
		return propertyProxies;
	}

	@NotNull
	@Override
	public DotNetMethodProxy[] getMethods()
	{
		int[] methods = myResult.Methods;

		MicrosoftMethodProxyOld[] methodProxies = new MicrosoftMethodProxyOld[methods.length];
		for(int i = 0; i < methods.length; i++)
		{
			methodProxies[i] = new MicrosoftMethodProxyOld(myClient, myTypeRef, methods[i]);
		}
		return methodProxies;
	}

	@Override
	public boolean isNested()
	{
		return false;
	}

	@Nullable
	@Override
	public DotNetMethodProxy findMethodByName(@NotNull String name, boolean deep, DotNetTypeProxy... params)
	{
		for(DotNetMethodProxy proxy : getMethods())
		{
			DotNetMethodParameterProxy[] parameters = proxy.getParameters();
			//TODO [VISTALL] dummy
			if(proxy.getName().equals(name) && params.length == parameters.length)
			{
				return proxy;
			}
		}

		if(deep)
		{
			DotNetTypeProxy baseType = getBaseType();
			if(baseType != null)
			{
				return baseType.findMethodByName(name, true, params);
			}
		}
		return null;
	}

	@Override
	public boolean isAssignableFrom(@NotNull DotNetTypeProxy otherType)
	{
		return false;
	}

	public TypeRef getTypeRef()
	{
		return myTypeRef;
	}
}
