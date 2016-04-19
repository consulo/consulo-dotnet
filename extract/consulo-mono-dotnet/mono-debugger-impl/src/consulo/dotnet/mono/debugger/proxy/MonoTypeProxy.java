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
		return new DotNetPropertyProxy[0];
	}

	@Override
	public boolean isNested()
	{
		return myTypeMirror.isNested();
	}

	@Nullable
	@Override
	public DotNetMethodProxy findMethodByName(@NotNull String name, boolean deep)
	{
		return null;
	}
}
