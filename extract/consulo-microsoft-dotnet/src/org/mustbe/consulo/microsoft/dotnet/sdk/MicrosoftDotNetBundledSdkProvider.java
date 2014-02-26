/*
 * Copyright 2013-2014 must-be.org
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
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.projectRoots.BundledSdkProvider;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.Pair;

/**
 * @author VISTALL
 * @since 26.02.14
 */
public class MicrosoftDotNetBundledSdkProvider implements BundledSdkProvider
{
	@NotNull
	@Override
	public Sdk[] createBundledSdks()
	{
		MicrosoftDotNetSdkType microsoftDotNetSdkType = MicrosoftDotNetSdkType.getInstance();

		File file = new File(microsoftDotNetSdkType.suggestHomePath());
		if(!file.exists())
		{
			return Sdk.EMPTY_ARRAY;
		}


		List<Pair<String, File>> validSdkDirs = microsoftDotNetSdkType.getValidSdkDirs(file);
		if(validSdkDirs.isEmpty())
		{
			return Sdk.EMPTY_ARRAY;
		}

		List<Sdk> list = new ArrayList<Sdk>(validSdkDirs.size());
		for(Pair<String, File> validSdkDir : validSdkDirs)
		{
			File sdkPath = validSdkDir.getSecond();
			SdkImpl sdk = new SdkImpl(microsoftDotNetSdkType.suggestSdkName(null, sdkPath.getAbsolutePath()) + " " +
					"(bundled)", microsoftDotNetSdkType);
			sdk.setVersionString(validSdkDir.getFirst());
			sdk.setHomePath(sdkPath.getAbsolutePath());

			microsoftDotNetSdkType.setupSdkPaths(sdk);

			// for 3.5 dont have core libs - need manual setup
			if(sdk.getRoots(OrderRootType.CLASSES).length == 0)
			{
				continue;
			}

			list.add(sdk);
		}
		return list.toArray(new Sdk[list.size()]);
	}
}
