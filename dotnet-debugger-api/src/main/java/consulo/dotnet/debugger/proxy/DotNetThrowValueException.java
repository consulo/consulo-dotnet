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

package consulo.dotnet.debugger.proxy;

import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class DotNetThrowValueException extends RuntimeException
{
	private DotNetStackFrameProxy myFrameProxy;
	private DotNetValueProxy myThrowValue;

	private String myType;
	private String myForceMessage;

	public DotNetThrowValueException(DotNetStackFrameProxy frameProxy, @Nonnull DotNetValueProxy throwValue)
	{
		myFrameProxy = frameProxy;
		myThrowValue = throwValue;
	}

	public DotNetThrowValueException(@Nonnull String type, @Nullable String message)
	{
		myType = type;
		myForceMessage = message;
	}

	@Override
	public String getMessage()
	{
		if(myType != null)
		{
			return "throw " + myType + (myForceMessage != null ? "(" + myForceMessage + ")" : " ");
		}

		DotNetTypeProxy type = myThrowValue.getType();
		if(type == null)
		{
			return "unknown exception";
		}
		DotNetMethodProxy getMethod = DotNetDebuggerSearchUtil.findGetterForProperty("Message", type);
		if(getMethod == null)
		{
			return "throw " + type.getFullName();
		}

		try
		{
			DotNetValueProxy invoke = getMethod.invoke(myFrameProxy, myThrowValue);
			if(invoke instanceof DotNetStringValueProxy)
			{
				String value = ((DotNetStringValueProxy) invoke).getValue();
				if(StringUtil.isEmptyOrSpaces(value))
				{
					return "throw " + type.getFullName();
				}
				return "throw " + type.getFullName() + "(" + value + ")";
			}
		}
		catch(Exception ignored)
		{
		}

		return "throw " + type.getFullName();
	}

	@Nullable
	public String getForceMessage()
	{
		return myForceMessage;
	}

	@Nullable
	public String getType()
	{
		return myType;
	}

	@Nullable
	public DotNetValueProxy getThrowExceptionValue()
	{
		return myThrowValue;
	}
}
