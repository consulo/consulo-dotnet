/*
 * Copyright 2013 must-be.org
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

import java.io.File;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.sdk.DotNetSdkType;
import consulo.microsoft.dotnet.MicrosoftDotNetIcons;
import consulo.microsoft.dotnet.util.MicrosoftDotNetUtil;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * @author VISTALL
 * @since 23.11.13.
 */
public class MicrosoftDotNetSdkType extends DotNetSdkType
{
	@NotNull
	public static MicrosoftDotNetSdkType getInstance()
	{
		return EP_NAME.findExtension(MicrosoftDotNetSdkType.class);
	}

	public MicrosoftDotNetSdkType()
	{
		super("MICROSOFT_DOTNET_SDK");
	}

	@Override
	public boolean supportsUserAdd()
	{
		return false;
	}

	@Override
	public boolean isValidSdkHome(String s)
	{
		return new File(s, "mscorlib.dll").exists();
	}

	@Nullable
	@Override
	public String getVersionString(String sdkHome)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String suggestSdkName(String currentSdkName, String sdkHome)
	{
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return ".NET";
	}

	@NotNull
	@Override
	public File getLoaderFile(@NotNull Sdk sdk)
	{
		String targetVersion = MicrosoftDotNetUtil.getTargetVersion();
		if("4.0.0".equals(targetVersion))
		{
			return getLoaderFile(MicrosoftDotNetSdkType.class, "loader4.exe");
		}
		return super.getLoaderFile(sdk);
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return MicrosoftDotNetIcons.DotNet;
	}
}
