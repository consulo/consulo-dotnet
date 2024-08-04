/*
 * Copyright 2013-2016 consulo.io
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

package consulo.dotnet.debugger.proxy.light;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;

/**
 * @author VISTALL
 * @since 28-Nov-16.
 */
public class LightTypeProxy implements DotNetTypeProxy
{
	private String myName;
	private String myFullName;
	private String[] myGenericParameters;

	public LightTypeProxy(DotNetTypeProxy proxy)
	{
		myName = proxy.getName();
		myFullName = proxy.getFullName();
		myGenericParameters = proxy.getGenericParameters();
	}

	@Override
	public boolean isAnnotatedBy(@Nonnull String attributeVmQName)
	{
		throw new UnsupportedOperationException();
	}

	@Nullable
	@Override
	public DotNetTypeProxy getDeclarationType()
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public String getName()
	{
		return myName;
	}

	@Nonnull
	@Override
	public String getFullName()
	{
		return myFullName;
	}

	@Nonnull
	@Override
	public String[] getGenericParameters()
	{
		return myGenericParameters;
	}

	@Override
	public boolean isArray()
	{
		throw new UnsupportedOperationException();
	}

	@Nullable
	@Override
	public DotNetTypeProxy getBaseType()
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public DotNetTypeProxy[] getInterfaces()
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public DotNetFieldProxy[] getFields()
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public DotNetPropertyProxy[] getProperties()
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public DotNetMethodProxy[] getMethods()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNested()
	{
		throw new UnsupportedOperationException();
	}

	@Nullable
	@Override
	public DotNetMethodProxy findMethodByName(@Nonnull String name, boolean deep, DotNetTypeProxy... params)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAssignableFrom(@Nonnull DotNetTypeProxy otherType)
	{
		throw new UnsupportedOperationException();
	}
}
