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

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.module.extension.BaseDotNetModuleExtension;
import org.mustbe.consulo.microsoft.dotnet.sdk.MicrosoftDotNetSdkType;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MicrosoftDotNetModuleExtension extends BaseDotNetModuleExtension<MicrosoftDotNetModuleExtension>
{
	public MicrosoftDotNetModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return MicrosoftDotNetSdkType.class;
	}

	@NotNull
	@Override
	public GeneralCommandLine createDefaultCommandLine(
			@NotNull String fileName, @Nullable DebugConnectionInfo d)
	{
		return createRunCommandLineImpl(fileName, d, getSdk());
	}

	@NotNull
	@Override
	public File getLoaderPath()
	{
		return getLoaderPath(MicrosoftDotNetModuleExtension.class);
	}

	@NotNull
	@Override
	public String getDebugFileExtension()
	{
		return "pdb";
	}

	@NotNull
	public static GeneralCommandLine createRunCommandLineImpl(@NotNull String fileName, @Nullable DebugConnectionInfo d, Sdk sdk)
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(fileName);
		return commandLine;
	}
}
