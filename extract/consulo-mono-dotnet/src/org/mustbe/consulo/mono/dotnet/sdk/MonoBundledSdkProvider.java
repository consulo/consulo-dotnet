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

package org.mustbe.consulo.mono.dotnet.sdk;

import com.intellij.openapi.projectRoots.BundledSdkProvider;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.util.SystemInfo;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

/**
 * @author VISTALL
 * @since 18.20 26.02.2014
 */
public class MonoBundledSdkProvider implements BundledSdkProvider
{
	@NotNull
	@Override
	public Sdk[] createBundledSdks()
	{
		val monoSdkType = MonoSdkType.getInstance();
		val list = new ArrayList<Sdk>();
		if(SystemInfo.isMac)
		{
			val lib = new File(monoSdkType.suggestHomePath(), "lib/mono");
			for (File file : lib.listFiles())
			{
				if(monoSdkType.isValidSdkHome(file.getAbsolutePath()))
				{
					SdkImpl sdk = new SdkImpl(monoSdkType.suggestSdkName(null, file.getAbsolutePath()) + " " +
							"(bundled)", monoSdkType);
					sdk.setVersionString(file.getName());
					sdk.setHomePath(file.getAbsolutePath());

					monoSdkType.setupSdkPaths(sdk);

					list.add(sdk);
				}
			}
		}
		return list.toArray(new Sdk[list.size()]);
	}
}
