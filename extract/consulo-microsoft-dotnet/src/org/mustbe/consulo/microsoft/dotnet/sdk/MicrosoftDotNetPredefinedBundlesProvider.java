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

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.bundle.PredefinedBundlesProvider;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.Consumer;

/**
 * @author VISTALL
 * @since 09.03.2015
 */
public class MicrosoftDotNetPredefinedBundlesProvider extends PredefinedBundlesProvider
{
	@Override
	public void createBundles(@NotNull Consumer<SdkImpl> consumer)
	{
		MicrosoftDotNetSdkType sdkType = MicrosoftDotNetSdkType.getInstance();

		Set<MicrosoftDotNetFramework> microsoftDotNetFrameworks = buildPaths(sdkType);
		for(MicrosoftDotNetFramework netFramework : microsoftDotNetFrameworks)
		{
			SdkImpl sdk = createSdkWithName(sdkType, sdkType.getPresentableName() + " " + netFramework.toString());
			sdk.setVersionString(netFramework.getVersion().getPresentableName());
			sdk.setHomePath(netFramework.getPath());

			MicrosoftVisualStudioVersion visualStudioVersion = netFramework.getVisualStudioVersion();
			if(visualStudioVersion != null)
			{
				String compilerPath = netFramework.getCompilerPath();
				assert compilerPath != null;
				sdk.setSdkAdditionalData(new MicrosoftDotNetSdkData(compilerPath));
			}
			consumer.consume(sdk);
		}
	}

	public Set<MicrosoftDotNetFramework> buildPaths(MicrosoftDotNetSdkType sdkType)
	{
		if(SystemInfo.isWindows)
		{
			Set<MicrosoftDotNetFramework> set = new TreeSet<MicrosoftDotNetFramework>();

			// first of all, we try to collect sdk from Windows dir, where compilers are located at same dir
			File framework = new File(System.getenv("windir"), "Microsoft.NET/Framework");
			File[] files = framework.listFiles();
			if(files != null)
			{
				for(File file : files)
				{
					MicrosoftDotNetVersion microsoftDotNetVersion = MicrosoftDotNetVersion.findVersion(file.getName());
					if(microsoftDotNetVersion != null && sdkType.isValidSdkHome(file.getPath()))
					{
						MicrosoftDotNetVersion version = MicrosoftDotNetVersion.findVersion(file.getName());
						assert version != null;
						set.add(new MicrosoftDotNetFramework(version, file.getPath(), null, null));
					}
				}
			}

			Map<MicrosoftVisualStudioVersion, String> compilerPaths = new TreeMap<MicrosoftVisualStudioVersion, String>();
			collectVisualStudioCompilerPaths(compilerPaths, "ProgramFiles");
			collectVisualStudioCompilerPaths(compilerPaths, "ProgramFiles(x86)");

			// we cant use external libraries if not external compilers
			if(!compilerPaths.isEmpty())
			{
				collectFromReferenceAssemblies(set, compilerPaths, sdkType, "ProgramFiles");
				collectFromReferenceAssemblies(set, compilerPaths, sdkType, "ProgramFiles(x86)");
			}
			return set;
		}
		return Collections.emptySet();
	}

	private void collectVisualStudioCompilerPaths(Map<MicrosoftVisualStudioVersion, String> map, String env)
	{
		String programFiles = System.getenv(env);
		if(programFiles != null)
		{
			File msbuildDir = new File(programFiles, "MSBuild");

			for(MicrosoftVisualStudioVersion version : MicrosoftVisualStudioVersion.values())
			{
				File compilerPath = new File(msbuildDir, version.getInternalVersion() + "/Bin");
				if(compilerPath.exists())
				{
					map.put(version, compilerPath.getPath());
				}
			}
		}
	}

	private void collectFromReferenceAssemblies(Set<MicrosoftDotNetFramework> set,
			@NotNull Map<MicrosoftVisualStudioVersion, String> compilerPaths,
			@NotNull MicrosoftDotNetSdkType sdkType,
			@Nullable String env)
	{
		String envValue = System.getenv(env);
		if(envValue == null)
		{
			return;
		}

		File path = new File(envValue, "Reference Assemblies\\Microsoft\\Framework\\.NETFramework");
		File[] files = path.listFiles();
		if(files == null)
		{
			return;
		}

		for(File file : files)
		{
			MicrosoftDotNetVersion microsoftDotNetVersion = MicrosoftDotNetVersion.findVersion(file.getName());
			if(microsoftDotNetVersion != null && sdkType.isValidSdkHome(file.getPath()))
			{
				for(Map.Entry<MicrosoftVisualStudioVersion, String> entry : compilerPaths.entrySet())
				{
					set.add(new MicrosoftDotNetFramework(microsoftDotNetVersion, file.getPath(), entry.getKey(), entry.getValue()));
				}
			}
		}
	}
}
