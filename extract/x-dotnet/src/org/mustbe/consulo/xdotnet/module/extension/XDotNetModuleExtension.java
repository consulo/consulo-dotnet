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

package org.mustbe.consulo.xdotnet.module.extension;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtensionImpl;
import org.mustbe.consulo.dotnet.sdk.DotNetSdkType;
import org.mustbe.consulo.microsoft.dotnet.module.extension.MicrosoftDotNetModuleExtension;
import org.mustbe.consulo.microsoft.dotnet.sdk.MicrosoftDotNetSdkType;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import org.mustbe.consulo.mono.dotnet.module.extension.MonoDotNetModuleExtension;
import org.mustbe.consulo.mono.dotnet.sdk.MonoSdkType;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 31.03.14
 */
public class XDotNetModuleExtension extends DotNetModuleExtensionImpl<XDotNetModuleExtension>
{
	public XDotNetModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return DotNetSdkType.class;
	}

	@NotNull
	@Override
	public GeneralCommandLine createRunCommandLine(@NotNull String fileName, @NotNull ConfigurationLayer configurationProfile, Executor executor)
	{
		Sdk sdk = getSdk();
		assert sdk != null;
		SdkTypeId sdkType = sdk.getSdkType();
		if(sdkType instanceof MicrosoftDotNetSdkType)
		{
			return MicrosoftDotNetModuleExtension.createRunCommandLineImpl(fileName, configurationProfile, executor, sdk);
		}
		else if(sdkType instanceof MonoSdkType)
		{
			return MonoDotNetModuleExtension.createRunCommandLineImpl(fileName, configurationProfile, executor, sdk);
		}
		throw  new IllegalArgumentException(sdkType.getName());
	}
}
