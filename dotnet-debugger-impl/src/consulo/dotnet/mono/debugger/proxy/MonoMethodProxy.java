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
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import mono.debugger.MethodMirror;
import mono.debugger.MethodParameterMirror;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoMethodProxy implements DotNetMethodProxy
{
	private MethodMirror myMethodMirror;

	public MonoMethodProxy(MethodMirror methodMirror)
	{
		myMethodMirror = methodMirror;
	}

	@NotNull
	@Override
	public DotNetTypeProxy getDeclarationType()
	{
		return new MonoTypeProxy(myMethodMirror.declaringType());
	}

	@NotNull
	@Override
	public DotNetMethodParameterProxy[] getParameters()
	{
		MethodParameterMirror[] parameters = myMethodMirror.parameters();
		DotNetMethodParameterProxy[] proxies = new DotNetMethodParameterProxy[parameters.length];
		for(int i = 0; i < parameters.length; i++)
		{
			MethodParameterMirror parameter = parameters[i];
			proxies[i] = new MonoMethodParameterProxy(i, parameter);
		}
		return proxies;
	}

	@NotNull
	@Override
	public String getName()
	{
		return myMethodMirror.name();
	}
}
