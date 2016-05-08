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
import org.mustbe.consulo.dotnet.compiler.DotNetMacroUtil;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.module.extension.BaseDotNetModuleExtension;
import org.mustbe.consulo.microsoft.dotnet.sdk.MicrosoftDotNetSdkType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootLayer;
import com.intellij.xdebugger.XDebugSession;
import consulo.dotnet.debugger.DotNetDebugProcessBase;
import consulo.dotnet.debugger.DotNetModuleExtensionWithDebug;
import consulo.dotnet.microsoft.debugger.MicrosoftDebugProcess;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MicrosoftDotNetModuleExtension extends BaseDotNetModuleExtension<MicrosoftDotNetModuleExtension> implements DotNetModuleExtensionWithDebug
{
	public MicrosoftDotNetModuleExtension(@NotNull String id, @NotNull ModuleRootLayer module)
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
	public GeneralCommandLine createDefaultCommandLine(@NotNull Sdk sdk, @Nullable DebugConnectionInfo debugConnectionInfo) throws ExecutionException
	{
		String fileName = DotNetMacroUtil.expandOutputFile(this);

		return createRunCommandLineImpl(fileName, debugConnectionInfo, sdk);
	}

	@NotNull
	@Override
	public String getDebugFileExtension()
	{
		return "pdb";
	}

	@NotNull
	public static GeneralCommandLine createRunCommandLineImpl(@NotNull String fileName, @Nullable DebugConnectionInfo debugConnectionInfo, @NotNull Sdk sdk)
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();
		if(debugConnectionInfo != null)
		{
			String mssdwPath = System.getProperty("mssdw.path");
			if(mssdwPath != null)
			{
				commandLine.withExePath(mssdwPath);
			}
			else
			{
				File pluginPath = PluginManager.getPluginPath(MicrosoftDotNetModuleExtension.class);
				commandLine.setExePath(new File(pluginPath, "mssdw\\mssdw.exe").getPath());
			}
			commandLine.addParameter("--port=" + debugConnectionInfo.getPort());
			commandLine.addParameter(fileName);
		}
		else
		{
			commandLine.setExePath(fileName);
		}
		return commandLine;
	}

	@NotNull
	@Override
	public DotNetDebugProcessBase createDebuggerProcess(@NotNull XDebugSession session, @NotNull RunProfile runProfile, @NotNull DebugConnectionInfo debugConnectionInfo)
	{
		return new MicrosoftDebugProcess(session, runProfile, debugConnectionInfo);
	}
}
