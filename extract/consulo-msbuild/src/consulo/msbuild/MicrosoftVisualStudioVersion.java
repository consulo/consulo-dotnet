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

package consulo.msbuild;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 09.03.2015
 */
public enum MicrosoftVisualStudioVersion
{
	Visual_Studio("4.0"),
	Visual_Studio_97("5.0"),
	Visual_Studio_6("6.0"),
	Visual_Studio_2002("7.0"),
	Visual_Studio_2003("7.1"),
	Visual_Studio_2005("8.0"),
	Visual_Studio_2008("9.0"),
	Visual_Studio_2010("10.0"),
	Visual_Studio_2012("11.0"),
	Visual_Studio_2013("12.0"),
	Visual_Studio_2015("14.0"),
	Visual_Studio_2017("15.0");

	private final String myInternalVersion;
	private final String myPresentableName;

	MicrosoftVisualStudioVersion(String internalVersion)
	{
		myInternalVersion = internalVersion;
		myPresentableName = name().replace("_", " ");
	}

	@NotNull
	public String getPresentableName()
	{
		return myPresentableName;
	}

	public String getInternalVersion()
	{
		return myInternalVersion;
	}
}
