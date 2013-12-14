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

package org.mustbe.consulo.microsoft.dotnet.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetVersion;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.microsoft.dotnet.sdk.MicrosoftDotNetSdkType;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.SdkType;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MicrosoftDotNetModuleExtension extends ModuleExtensionWithSdkImpl<MicrosoftDotNetModuleExtension> implements
		DotNetModuleExtension<MicrosoftDotNetModuleExtension>
{
	public MicrosoftDotNetModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	@Override
	protected Class<? extends SdkType> getSdkTypeClass()
	{
		return MicrosoftDotNetSdkType.class;
	}

	@NotNull
	@Override
	public DotNetVersion getVersion()
	{
		return DotNetVersion.LAST;
	}

	@NotNull
	@Override
	public GeneralCommandLine createRunCommandLine(@NotNull String fileName)
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(fileName);
		return commandLine;
	}
}
