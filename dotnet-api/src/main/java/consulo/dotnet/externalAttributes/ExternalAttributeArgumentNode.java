/*
 * Copyright 2013-2014 must-be.org
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

package consulo.dotnet.externalAttributes;

import consulo.dotnet.DotNetTypes;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 02.09.14
 */
public class ExternalAttributeArgumentNode
{
	private final String myType;
	private final String myValue;

	public ExternalAttributeArgumentNode(String type, String value)
	{
		myType = type;
		myValue = value;
	}

	public String getValue()
	{
		return myValue;
	}

	public String getType()
	{
		return myType;
	}

	@Nonnull
	public Object toJavaObject()
	{
		if(DotNetTypes.System.String.equals(myType))
		{
			return myValue;
		}
		//TODO [VISTALL] others?
		return myValue;
	}
}
