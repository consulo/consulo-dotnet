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

package org.mustbe.consulo.microsoft.dotnet.sdk;

import java.io.File;

import javax.swing.Icon;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.sdk.DotNetSdkType;
import org.mustbe.consulo.microsoft.dotnet.MicrosoftDotNetIcons;
import org.mustbe.consulo.microsoft.dotnet.util.MicrosoftDotNetUtil;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkModificator;

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

	@Nullable
	@Override
	public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator)
	{
		SdkAdditionalData sdkAdditionalData = sdkModificator.getSdkAdditionalData();
		if(sdkAdditionalData instanceof MicrosoftDotNetSdkData)
		{
			return new MicrosoftDotNetSdkDataConfigurable((MicrosoftDotNetSdkData) sdkAdditionalData);
		}
		return null;
	}

	@Override
	public void saveAdditionalData(SdkAdditionalData additionalData, Element additional)
	{
		MicrosoftDotNetSdkData sdkData = (MicrosoftDotNetSdkData) additionalData;

		additional.setAttribute("compiler-path", sdkData.getCompilerPath());
	}

	@Nullable
	@Override
	public SdkAdditionalData loadAdditionalData(Sdk currentSdk, Element additional)
	{
		String compilerPath = additional.getAttributeValue("compiler-path");
		if(compilerPath != null)
		{
			return new MicrosoftDotNetSdkData(compilerPath);
		}
		return null;
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
		MicrosoftDotNetVersion version = MicrosoftDotNetVersion.findVersion(new File(sdkHome).getName());
		if(version != null)
		{
			return version.getPresentableName();
		}
		return null;
	}

	@Override
	public String suggestSdkName(String currentSdkName, String sdkHome)
	{
		MicrosoftDotNetVersion version = MicrosoftDotNetVersion.findVersion(new File(sdkHome).getName());
		if(version != null)
		{
			return version.getPresentableName();
		}
		return null;
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "Microsoft .NET";
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
