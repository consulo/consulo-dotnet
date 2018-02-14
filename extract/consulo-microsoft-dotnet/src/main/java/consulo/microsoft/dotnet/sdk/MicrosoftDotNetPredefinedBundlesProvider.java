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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.Consumer;
import consulo.bundle.PredefinedBundlesProvider;

/**
 * @author VISTALL
 * @since 09.03.2015
 */
public class MicrosoftDotNetPredefinedBundlesProvider extends PredefinedBundlesProvider
{
	@Override
	public void createBundles(@Nonnull Consumer<SdkImpl> consumer)
	{
		MicrosoftDotNetSdkType sdkType = MicrosoftDotNetSdkType.getInstance();

		Collection<MicrosoftDotNetFramework> microsoftDotNetFrameworks = buildPaths(sdkType);
		for(MicrosoftDotNetFramework netFramework : microsoftDotNetFrameworks)
		{
			SdkImpl sdk = createSdkWithName(sdkType, sdkType.getPresentableName() + " " + netFramework.toString());
			sdk.setHomePath(netFramework.getPath());
			sdk.setVersionString(netFramework.getVersion().getPresentableName());

			consumer.consume(sdk);
		}
	}

	public Collection<MicrosoftDotNetFramework> buildPaths(MicrosoftDotNetSdkType sdkType)
	{
		if(SystemInfo.isWindows)
		{
			List<MicrosoftDotNetFramework> list = new ArrayList<>();

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
						list.add(new MicrosoftDotNetFramework(microsoftDotNetVersion, file.getPath(), true));
					}
				}
			}

			collectFromReferenceAssemblies(list, sdkType, "ProgramFiles");
			collectFromReferenceAssemblies(list, sdkType, "ProgramFiles(x86)");
			return list;
		}
		return Collections.emptyList();
	}


	private void collectFromReferenceAssemblies(Collection<MicrosoftDotNetFramework> set,
			@Nonnull MicrosoftDotNetSdkType sdkType,
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
				set.add(new MicrosoftDotNetFramework(microsoftDotNetVersion, file.getPath(), false));
			}
		}
	}
}
