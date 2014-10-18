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

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.module.extension.BaseDotNetModuleExtension;
import org.mustbe.consulo.mono.dotnet.sdk.MonoSdkType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootLayer;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.types.DocumentationOrderRootType;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MonoDotNetModuleExtension extends BaseDotNetModuleExtension<MonoDotNetModuleExtension>
{
	public MonoDotNetModuleExtension(@NotNull String id, @NotNull ModuleRootLayer rootModel)
	{
		super(id, rootModel);
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return MonoSdkType.class;
	}

	@NotNull
	@Override
	public GeneralCommandLine createDefaultCommandLine(@NotNull String fileName, @Nullable DebugConnectionInfo d) throws ExecutionException
	{
		Sdk sdk = getSdk();
		if(sdk == null)
		{
			throw new ExecutionException(".NET SDK for module is not defined");
		}
		return createRunCommandLineImpl(fileName, d, sdk);
	}

	@NotNull
	@Override
	public String getDebugFileExtension()
	{
		return getTarget().getExtension() + ".mdb";
	}

	@NotNull
	@Override
	public String[] getSystemLibraryUrlsImpl(@NotNull Sdk sdk, @NotNull String name, @NotNull OrderRootType orderRootType)
	{
		if(orderRootType == DocumentationOrderRootType.getInstance())
		{
			String[] systemLibraryUrls = super.getSystemLibraryUrlsImpl(sdk, name, orderRootType);

			VirtualFile docDir = sdk.getHomeDirectory().findFileByRelativePath("/../../monodoc/sources");
			if(docDir == null)
			{
				return systemLibraryUrls;
			}

			List<String> list = new ArrayList<String>();
			ContainerUtil.addAll(list, systemLibraryUrls);

			for(VirtualFile virtualFile : docDir.getChildren())
			{
				if(Comparing.equal(virtualFile.getExtension(), "source"))
				{
					list.add(virtualFile.getUrl());
				}
			}
			return ArrayUtil.toStringArray(list);
		}
		return super.getSystemLibraryUrlsImpl(sdk, name, orderRootType);
	}

	@NotNull
	public static GeneralCommandLine createRunCommandLineImpl(@NotNull String fileName, @Nullable DebugConnectionInfo d, @NotNull Sdk sdk)
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
		if(d != null)
		{
			commandLine.addParameter("--debug");
			commandLine.addParameter(generateParameterForRun(d));
		}
		commandLine.addParameter(fileName);
		return commandLine;
	}

	private static String generateParameterForRun(@NotNull DebugConnectionInfo debugConnectionInfo)
	{
		StringBuilder builder = new StringBuilder("--debugger-agent=transport=dt_socket,address=");
		builder.append(debugConnectionInfo.getHost());
		builder.append(":");
		builder.append(debugConnectionInfo.getPort());
		if(debugConnectionInfo.isServer())
		{
			builder.append(",suspend=y,server=y");
		}
		else
		{
			builder.append(",suspend=y,server=n");
		}
		return builder.toString();
	}
}
