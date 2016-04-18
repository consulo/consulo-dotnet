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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoTypeProxy implements DotNetTypeProxy
{
	private TypeMirror myTypeMirror;

	public MonoTypeProxy(TypeMirror typeMirror)
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
		return new MonoTypeProxy(myTypeMirror.baseType());
	}
}
