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

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.ArrayValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.BadRequestResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.BooleanValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.CharValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.NullValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.NumberValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.ObjectValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.StringValueResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.UnknownValueResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
@Logger
@Deprecated
public class MicrosoftValueProxyUtilOld
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
		if(o instanceof BadRequestResult)
		{
			MicrosoftValueProxyUtilOld.LOGGER.error("Receive bad value");
			return null;
		}
		if(o instanceof UnknownValueResult)
		{
			MicrosoftValueProxyUtilOld.LOGGER.error("Receive unknown value: " + ((UnknownValueResult) o).Type);
			return null;
		}
		if(o instanceof StringValueResult)
		{
			return new MicrosoftStringValueProxyOld(client, ((StringValueResult) o));
		}
		if(o instanceof BooleanValueResult)
		{
			return new MicrosoftBooleanValueProxyOld(client, (BooleanValueResult) o);
		}
		if(o instanceof ObjectValueResult)
		{
			return new MicrosoftObjectValueProxy(client, (ObjectValueResult) o);
		}
		if(o instanceof NullValueResult)
		{
			return new MicrosoftNullValueProxy();
		}
		if(o instanceof ArrayValueResult)
		{
			return new MicrosoftArrayValueProxy(client, (ArrayValueResult) o);
		}
		if(o instanceof NumberValueResult)
		{
			return new MicrosoftNumberValueProxy(client, (NumberValueResult) o);
		}
		if(o instanceof CharValueResult)
		{
			return new MicrosoftCharValueProxy(client, (CharValueResult) o);
		}
		throw new IllegalArgumentException("Value is not handled " + o.getClass().getName());
	}
}
