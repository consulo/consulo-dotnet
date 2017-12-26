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

package consulo.microsoft.dotnet.util;

import org.jetbrains.annotations.Nullable;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

/**
 * @author VISTALL
 * @since 09.03.2015
 */
public class MicrosoftDotNetUtil
{
	@Nullable
	public static String getInstallPath()
	{
		try
		{
			return Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\NET Framework Setup\\NDP\\v4\\Full",
					"InstallPath");
		}
		catch(Exception ignored)
		{
		}
		return null;
	}

	@Nullable
	public static String getTargetVersion()
	{
		try
		{
			return Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\NET Framework Setup\\NDP\\v4\\Full",
					"TargetVersion");
		}
		catch(Exception ignored)
		{
		}
		return null;
	}
}
