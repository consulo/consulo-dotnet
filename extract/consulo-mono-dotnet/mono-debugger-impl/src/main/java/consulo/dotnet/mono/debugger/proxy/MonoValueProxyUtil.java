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

import javax.annotation.Nullable;

import org.jetbrains.annotations.Contract;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mono.debugger.*;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoValueProxyUtil
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
			valueProxy = new MonoObjectValueProxy((ObjectValueMirror) value);
		}

		if(value instanceof NoObjectValueMirror)
		{
			valueProxy = new MonoNullValueProxy((NoObjectValueMirror) value);
		}

		if(value instanceof NumberValueMirror)
		{
			valueProxy = new MonoNumberValueProxy((NumberValueMirror) value);
		}

		if(value instanceof ArrayValueMirror)
		{
			valueProxy = new MonoArrayValueProxy((ArrayValueMirror) value);
		}

		if(value instanceof StringValueMirror)
		{
			valueProxy = new MonoStringValueProxy((StringValueMirror) value);
		}

		if(value instanceof BooleanValueMirror)
		{
			valueProxy = new MonoBooleanValueProxy((BooleanValueMirror) value);
		}

		if(value instanceof CharValueMirror)
		{
			valueProxy = new MonoCharValueProxy((CharValueMirror) value);
		}

		if(value instanceof StructValueMirror)
		{
			valueProxy = new MonoStructValueProxy((StructValueMirror) value);
		}

		if(value instanceof EnumValueMirror)
		{
			valueProxy = new MonoEnumValueProxy((EnumValueMirror) value);
		}

		if(valueProxy == null)
		{
			throw new IllegalArgumentException("Value " + value.getClass().getSimpleName() + " can't be wrapped");
		}
		return (T) valueProxy;
	}
}
