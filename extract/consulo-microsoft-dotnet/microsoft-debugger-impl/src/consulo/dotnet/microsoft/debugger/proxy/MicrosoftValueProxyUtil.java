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
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.BooleanValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.ObjectValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.StringValueResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftValueProxyUtil
{
	@Nullable
	public static DotNetValueProxy sendAndReceive(MicrosoftDebuggerClient client, Object request)
	{
		Object o = client.sendAndReceive(request, Object.class);
		return wrap(client, o);
	}

	@Nullable
	public static DotNetValueProxy wrap(@NotNull MicrosoftDebuggerClient client, @Nullable Object o)
	{
		if(o instanceof StringValueResult)
		{
			return new MicrosoftStringValueProxy(((StringValueResult) o));
		}
		if(o instanceof BooleanValueResult)
		{
			return new MicrosoftBooleanValueProxy((BooleanValueResult) o);
		}
		if(o instanceof ObjectValueResult)
		{
			return new MicrosoftObjectValueProxy(client, (ObjectValueResult) o);
		}
		return null;
	}
}
