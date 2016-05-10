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
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mssdw.*;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftValueProxyUtil
{
	@SuppressWarnings("unchecked")
	@Nullable
	@Contract(value = "null -> null; !null -> !null", pure = true)
	public static <T extends DotNetValueProxy> T wrap(@Nullable Value<?> value)
	{
		if(value == null)
		{
			return null;
		}

		DotNetValueProxy valueProxy = null;
		if(value instanceof ObjectValueMirror)
		{
			valueProxy = new MicrosoftObjectValueProxy((ObjectValueMirror) value);
		}

		if(value instanceof NoObjectValueMirror)
		{
			valueProxy = new MicrosoftNullValueProxy();
		}

		if(value instanceof NumberValueMirror)
		{
			valueProxy = new MicrosoftNumberValueProxy((NumberValueMirror) value);
		}

		if(value instanceof ArrayValueMirror)
		{
			valueProxy = new MicrosoftArrayValueProxy((ArrayValueMirror) value);
		}

		if(value instanceof StringValueMirror)
		{
			valueProxy = new MicrosoftStringValueProxy((StringValueMirror) value);
		}

		if(value instanceof BooleanValueMirror)
		{
			valueProxy = new MicrosoftBooleanValueProxy((BooleanValueMirror) value);
		}

		if(value instanceof CharValueMirror)
		{
			valueProxy = new MicrosoftCharValueProxy((CharValueMirror) value);
		}

		if(value instanceof StructValueMirror)
		{
			valueProxy = new MicrosoftStructValueProxy((StructValueMirror) value);
		}

		if(value instanceof EnumValueMirror)
		{
			valueProxy = new MicrosoftEnumValueProxy((EnumValueMirror) value);
		}

		if(valueProxy == null)
		{
			throw new IllegalArgumentException("Value " + value.getClass().getSimpleName() + " can't be wrapped");
		}
		return (T) valueProxy;
	}
}
