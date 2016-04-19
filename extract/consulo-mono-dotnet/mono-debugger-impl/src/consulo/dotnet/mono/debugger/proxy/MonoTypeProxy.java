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

package consulo.dotnet.mono.debugger.proxy;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import mono.debugger.FieldMirror;
import mono.debugger.MethodMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoTypeProxy implements DotNetTypeProxy
{
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static MonoTypeProxy of(@Nullable TypeMirror typeMirror)
	{
		return typeMirror == null ? null : new MonoTypeProxy(typeMirror);
	}

	private TypeMirror myTypeMirror;

	private MonoTypeProxy(@NotNull TypeMirror typeMirror)
	{
		myTypeMirror = typeMirror;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getDeclarationType()
	{
		TypeMirror parentType = myTypeMirror.parentType();
		if(parentType == null)
		{
			return null;
		}
		return new MonoTypeProxy(parentType);
	}

	@NotNull
	@Override
	public String getName()
	{
		return myTypeMirror.name();
	}

	@NotNull
	@Override
	public String getFullName()
	{
		return myTypeMirror.fullName();
	}

	@Override
	public boolean isArray()
	{
		return myTypeMirror.isArray();
	}

	@Nullable
	@Override
	public DotNetTypeProxy getBaseType()
	{
		TypeMirror baseType = myTypeMirror.baseType();
		if(baseType == null)
		{
			return null;
		}
		return new MonoTypeProxy(baseType);
	}

	@NotNull
	@Override
	public DotNetTypeProxy[] getInterfaces()
	{
		TypeMirror[] interfaces = myTypeMirror.getInterfaces();
		DotNetTypeProxy[] proxies = new DotNetTypeProxy[interfaces.length];
		for(int i = 0; i < interfaces.length; i++)
		{
			TypeMirror mirror = interfaces[i];
			proxies[i] = new MonoTypeProxy(mirror);
		}
		return proxies;
	}

	@NotNull
	@Override
	public DotNetFieldProxy[] getFields()
	{
		FieldMirror[] fields = myTypeMirror.fields();
		DotNetFieldProxy[] proxies = new DotNetFieldProxy[fields.length];
		for(int i = 0; i < fields.length; i++)
		{
			FieldMirror field = fields[i];
			proxies[i] = new MonoFieldProxy(field);
		}
		return proxies;
	}

	@NotNull
	@Override
	public DotNetPropertyProxy[] getProperties()
	{
		PropertyMirror[] properties = myTypeMirror.properties();
		DotNetPropertyProxy[] proxies = new DotNetPropertyProxy[properties.length];
		for(int i = 0; i < properties.length; i++)
		{
			PropertyMirror property = properties[i];
			proxies[i] = new MonoPropertyProxy(property);
		}
		return proxies;
	}

	@NotNull
	@Override
	public DotNetMethodProxy[] getMethods()
	{
		MethodMirror[] methods = myTypeMirror.methods();
		DotNetMethodProxy[] proxies = new DotNetMethodProxy[methods.length];
		for(int i = 0; i < methods.length; i++)
		{
			MethodMirror method = methods[i];
			proxies[i] = new MonoMethodProxy(method);
		}
		return proxies;
	}

	@Override
	public boolean isNested()
	{
		return myTypeMirror.isNested();
	}

	@Nullable
	@Override
	public DotNetMethodProxy findMethodByName(@NotNull String name, boolean deep, DotNetTypeProxy... params)
	{
		TypeMirror[] typeMirrors = new TypeMirror[params.length];
		for(int i = 0; i < params.length; i++)
		{
			MonoTypeProxy param = (MonoTypeProxy) params[i];
			typeMirrors[i] = param.myTypeMirror;
		}
		MethodMirror methodByName = myTypeMirror.findMethodByName(name, deep, typeMirrors);
		if(methodByName != null)
		{
			return new MonoMethodProxy(methodByName);
		}
		return null;
	}
}
