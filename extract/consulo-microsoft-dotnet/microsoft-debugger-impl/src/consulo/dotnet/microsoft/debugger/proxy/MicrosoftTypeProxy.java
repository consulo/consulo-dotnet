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
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import mssdw.CustomAttributeMirror;
import mssdw.FieldMirror;
import mssdw.MethodMirror;
import mssdw.PropertyMirror;
import mssdw.TypeMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftTypeProxy implements DotNetTypeProxy
{
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static MicrosoftTypeProxy of(@Nullable TypeMirror typeMirror)
	{
		return typeMirror == null ? null : new MicrosoftTypeProxy(typeMirror);
	}

	private TypeMirror myTypeMirror;

	private MicrosoftTypeProxy(@NotNull TypeMirror typeMirror)
	{
		myTypeMirror = typeMirror;
	}

	@Override
	public boolean isAnnotatedBy(@NotNull String attributeVmQName)
	{
		for(CustomAttributeMirror customAttributeMirror : myTypeMirror.customAttributes())
		{
			MethodMirror constructorMirror = customAttributeMirror.getConstructorMirror();
			TypeMirror typeMirror = constructorMirror.declaringType();
			if(attributeVmQName.equals(typeMirror.fullName()))
			{
				return true;
			}
		}
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
		return new MicrosoftTypeProxy(baseType);
	}

	@NotNull
	@Override
	public DotNetTypeProxy[] getInterfaces()
	{
		/*TypeMirror[] interfaces = myTypeMirror.getInterfaces();
		DotNetTypeProxy[] proxies = new DotNetTypeProxy[interfaces.length];
		for(int i = 0; i < interfaces.length; i++)
		{
			TypeMirror mirror = interfaces[i];
			proxies[i] = new MicrosoftTypeProxy(mirror);
		}
		return proxies;    */
		return new DotNetTypeProxy[0];
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
			proxies[i] = new MicrosoftFieldProxy(field);
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
			proxies[i] = new MicrosoftPropertyProxy(property);
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
			proxies[i] = new MicrosoftMethodProxy(method);
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
			MicrosoftTypeProxy param = (MicrosoftTypeProxy) params[i];
			typeMirrors[i] = param.myTypeMirror;
		}
		MethodMirror methodByName = myTypeMirror.findMethodByName(name, deep, typeMirrors);
		if(methodByName != null)
		{
			return new MicrosoftMethodProxy(methodByName);
		}
		return null;
	}

	@Override
	public boolean isAssignableFrom(@NotNull DotNetTypeProxy otherType)
	{
		MicrosoftTypeProxy MicrosoftTypeProxy = (MicrosoftTypeProxy) otherType;
		return myTypeMirror.isAssignableFrom(MicrosoftTypeProxy.myTypeMirror);
	}
}