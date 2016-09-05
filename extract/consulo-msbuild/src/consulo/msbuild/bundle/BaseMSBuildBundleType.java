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

package consulo.msbuild.bundle;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.projectRoots.SdkType;

/**
 * @author VISTALL
 * @since 08.06.2015
 */
public abstract class BaseMSBuildBundleType extends SdkType
{
	public BaseMSBuildBundleType(@NonNls String name)
	{
		super(name);
	}

	@Override
	public String suggestSdkName(String currentSdkName, String sdkHome)
	{
		return getPresentableName() + " " + getVersionString(sdkHome);
	}
}
