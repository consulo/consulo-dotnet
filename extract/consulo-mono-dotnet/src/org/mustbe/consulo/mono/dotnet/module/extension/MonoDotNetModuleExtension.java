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

package org.mustbe.consulo.mono.dotnet.module.extension;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtensionImpl;
import org.mustbe.consulo.mono.dotnet.sdk.MonoSdkType;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.SystemInfo;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MonoDotNetModuleExtension extends DotNetModuleExtensionImpl<MonoDotNetModuleExtension>
{
	public MonoDotNetModuleExtension(@NotNull String id, @NotNull ModifiableRootModel rootModel)
	{
		super(id, rootModel);
	}

	@NotNull
	@Override
	protected Class<? extends SdkType> getSdkTypeClass()
	{
		return MonoSdkType.class;
	}

	@NotNull
	@Override
	public GeneralCommandLine createRunCommandLine(@NotNull String fileName, @NotNull ConfigurationLayer configurationProfile, Executor executor)
	{
		return createRunCommandLine0(fileName, configurationProfile, executor, getSdk());
	}

	@NotNull
	public static GeneralCommandLine createRunCommandLine0(@NotNull String fileName, @NotNull ConfigurationLayer configurationProfile,
			@NotNull Executor executor, @NotNull Sdk sdk)
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();

		String runFile = null;
		if(SystemInfo.isWindows)
		{
			runFile = sdk.getHomePath() + "/../../../bin/mono.exe";
		}
		else if(SystemInfo.isMac)
		{
			runFile = sdk.getHomePath() + "/../../../bin/mono";
		}
		else if(SystemInfo.isLinux)
		{
			runFile = "/usr/bin/mono";
		}

		assert runFile != null : SystemInfo.OS_NAME;

		commandLine.setExePath(runFile);
		if(executor instanceof DefaultDebugExecutor)
		{
			commandLine.addParameter("--debug");
		}
		commandLine.addParameter(fileName);
		return commandLine;
	}
}
