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
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.bundle.PredefinedBundlesProvider;
import org.mustbe.consulo.dotnet.sdk.DotNetCompilerDirOrderRootType;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;

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

		Collection<MicrosoftDotNetFramework> microsoftDotNetFrameworks = buildPaths(sdkType);
		for(MicrosoftDotNetFramework netFramework : microsoftDotNetFrameworks)
		{
			SdkImpl sdk = createSdkWithName(sdkType, sdkType.getPresentableName() + " " + netFramework.toString());
			sdk.setHomePath(netFramework.getPath());
			sdk.setVersionString(netFramework.getVersion().getPresentableName());

			String compilerDir = null;
			MicrosoftVisualStudioVersion visualStudioVersion = netFramework.getVisualStudioVersion();
			if(visualStudioVersion != null)
			{
				compilerDir = netFramework.getCompilerPath();
			}
			else
			{
				compilerDir = netFramework.getPath();
			}

			assert compilerDir != null;
			VirtualFile compilerVirtualDir = LocalFileSystem.getInstance().findFileByPath(compilerDir);
			assert compilerVirtualDir != null;

			SdkModificator sdkModificator = sdk.getSdkModificator();
			sdkModificator.addRoot(compilerVirtualDir, DotNetCompilerDirOrderRootType.getInstance());
			sdkModificator.commitChanges();

			consumer.consume(sdk);
		}
	}

	public Collection<MicrosoftDotNetFramework> buildPaths(MicrosoftDotNetSdkType sdkType)
	{
		if(SystemInfo.isWindows)
		{
			List<MicrosoftDotNetFramework> list = new ArrayList<MicrosoftDotNetFramework>();

			// first of all, we try to collect sdk from Windows dir, where compilers are located at same dir
			File framework = new File(System.getenv("windir"), "Microsoft.NET/Framework");
			File[] files = framework.listFiles();
			if(files != null)
			{
				for(File file : files)
				{
					MicrosoftDotNetVersion microsoftDotNetVersion = MicrosoftDotNetVersion.findVersion(file.getName(), true);
					if(microsoftDotNetVersion != null && sdkType.isValidSdkHome(file.getPath()))
					{
						list.add(new MicrosoftDotNetFramework(microsoftDotNetVersion, file.getPath(), null, null));
					}
				}
			}

			Map<MicrosoftVisualStudioVersion, String> compilerPaths = new TreeMap<MicrosoftVisualStudioVersion, String>();
			collectVisualStudioCompilerPaths(compilerPaths, "ProgramFiles");
			collectVisualStudioCompilerPaths(compilerPaths, "ProgramFiles(x86)");

			// we cant use external libraries if not external compilers
			if(!compilerPaths.isEmpty())
			{
				collectFromReferenceAssemblies(list, compilerPaths, sdkType, "ProgramFiles");
				collectFromReferenceAssemblies(list, compilerPaths, sdkType, "ProgramFiles(x86)");
			}

			ContainerUtil.sort(list, new Comparator<MicrosoftDotNetFramework>()
			{
				@Override
				public int compare(MicrosoftDotNetFramework o1, MicrosoftDotNetFramework o2)
				{
					return getWeight(o1) - getWeight(o2);
				}

				private int getWeight(MicrosoftDotNetFramework f)
				{
					int value = 0;
					if(f.getVisualStudioVersion() != null)
					{
						value += 100 + f.getVisualStudioVersion().ordinal();
					}
					value += f.getVersion().ordinal();
					return value;
				}
			});
			return list;
		}
		return Collections.emptyList();
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

	private void collectFromReferenceAssemblies(Collection<MicrosoftDotNetFramework> set,
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
			MicrosoftDotNetVersion microsoftDotNetVersion = MicrosoftDotNetVersion.findVersion(file.getName(), false);
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
