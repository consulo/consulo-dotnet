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

package consulo.dotnet.sdk;

import java.io.File;

import javax.swing.Icon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.dotnet.DotNetIcons;
import com.intellij.openapi.projectRoots.SdkType;

/**
 * @author VISTALL
 * @since 08.06.2015
 */
public class RoslynBundleType extends SdkType
{
	@Nonnull
	public static RoslynBundleType getInstance()
	{
		return EP_NAME.findExtension(RoslynBundleType.class);
	}

	public RoslynBundleType()
	{
		super("ROSLYN_BUNDLE");
	}

	@Override
	public boolean isValidSdkHome(String path)
	{
		return new File(path, "Roslyn.Diagnostics.Analyzers.dll").exists() ||
				new File(path, "Roslyn.Diagnostics.Analyzers.dll").exists() ||
				new File(path, "csc.exe").exists();
	}

	@Nullable
	@Override
	public String getVersionString(String sdkHome)
	{
		return "undefined";
	}

	@Override
	public String suggestSdkName(String currentSdkName, String sdkHome)
	{
		return getPresentableName();
	}

	@Nonnull
	@Override
	public String getPresentableName()
	{
		return "Roslyn";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return DotNetIcons.NetFoundation;
	}
}
