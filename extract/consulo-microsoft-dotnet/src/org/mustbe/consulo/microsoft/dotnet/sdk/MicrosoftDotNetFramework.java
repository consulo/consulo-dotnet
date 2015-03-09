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

package org.mustbe.consulo.microsoft.dotnet.sdk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 09.03.2015
 */
public class MicrosoftDotNetFramework implements Comparable<MicrosoftDotNetFramework>
{
	private final MicrosoftDotNetVersion myVersion;
	@NotNull
	private final String myPath;
	private final MicrosoftVisualStudioVersion myVisualStudioVersion;
	private final String myCompilerPath;

	public MicrosoftDotNetFramework(@NotNull MicrosoftDotNetVersion version, @NotNull String path, MicrosoftVisualStudioVersion visualStudioVersion,
			String compilerPath)
	{
		myVersion = version;
		myPath = path;
		myVisualStudioVersion = visualStudioVersion;
		myCompilerPath = compilerPath;
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

	@Nullable
	public String getCompilerPath()
	{
		return myCompilerPath;
	}

	@Nullable
	public MicrosoftVisualStudioVersion getVisualStudioVersion()
	{
		return myVisualStudioVersion;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(myVersion.getPresentableName());
		if(myVisualStudioVersion != null)
		{
			builder.append(" (").append(myVisualStudioVersion.getPresentableName()).append(")");
		}
		return builder.toString();
	}

	@Override
	public int compareTo(@NotNull MicrosoftDotNetFramework o)
	{
		return getWeight() - o.getWeight();
	}

	private int getWeight()
	{
		int value = Integer.MAX_VALUE;
		if(myVisualStudioVersion != null)
		{
			value += myVisualStudioVersion.ordinal();
		}
		value += myVersion.ordinal();
		return value;
	}
}
