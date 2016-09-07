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

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 09.03.2015
 */
public class MicrosoftDotNetFramework
{
	private final MicrosoftDotNetVersion myVersion;
	@NotNull
	private final String myPath;
	private boolean myWindir;

	public MicrosoftDotNetFramework(@NotNull MicrosoftDotNetVersion version, @NotNull String path, boolean windir)
	{
		myVersion = version;
		myPath = path;
		myWindir = windir;
	}

	@NotNull
	public MicrosoftDotNetVersion getVersion()
	{
		return myVersion;
	}

	@NotNull
	public String getPath()
	{
		return myPath;
	}

	@Override
	public String toString()
	{
		if(myWindir)
		{
			return myVersion.getPresentableName() + " (windir)";
		}
		return myVersion.getPresentableName();
	}
}