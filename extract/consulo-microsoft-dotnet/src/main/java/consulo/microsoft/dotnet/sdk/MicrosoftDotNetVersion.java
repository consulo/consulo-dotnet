/*
 * Copyright 2013-2015 must-be.org
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

package consulo.microsoft.dotnet.sdk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
* @author VISTALL
* @since 09.03.2015
*/
public enum MicrosoftDotNetVersion
{
	_2_0,
	_3_5,
	_4_0,
	_4_5,
	_4_5_1,
	_4_5_2,
	_4_6,
	_4_6_1,
	_4_6_2,
	_4_7,
	_4_7_1;

	private String myPresentableName;

	MicrosoftDotNetVersion()
	{
		String name = name();
		myPresentableName = name.substring(1, name.length()).replace("_", ".");
	}

	@Nullable
	public static MicrosoftDotNetVersion findVersion(@Nonnull String version, boolean startWith)
	{
		version = version.charAt(0) == 'v' ? version.substring(1, version.length()) : version;
		for(MicrosoftDotNetVersion microsoftDotNetVersion : values())
		{
			String presentableName = microsoftDotNetVersion.myPresentableName;
			if(startWith ? version.startsWith(presentableName) : version.equals(presentableName))
			{
				return microsoftDotNetVersion;
			}
		}
		return null;
	}

	public String getPresentableName()
	{
		return myPresentableName;
	}
}
