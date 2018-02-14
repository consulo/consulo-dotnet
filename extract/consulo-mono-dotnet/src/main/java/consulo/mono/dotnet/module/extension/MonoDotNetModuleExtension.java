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

package consulo.mono.dotnet.module.extension;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.dotnet.compiler.DotNetMacroUtil;
import consulo.dotnet.execution.DebugConnectionInfo;
import consulo.dotnet.module.extension.BaseDotNetModuleExtension;
import consulo.mono.dotnet.sdk.MonoSdkType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import consulo.dotnet.debugger.DotNetDebugProcessBase;
import consulo.dotnet.debugger.DotNetModuleExtensionWithDebug;
import consulo.dotnet.mono.debugger.MonoDebugProcess;
import consulo.roots.ModuleRootLayer;
import consulo.roots.types.DocumentationOrderRootType;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MonoDotNetModuleExtension extends BaseDotNetModuleExtension<MonoDotNetModuleExtension> implements DotNetModuleExtensionWithDebug
{
	public MonoDotNetModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer rootModel)
	{
		super(id, rootModel);
	}

	@Nonnull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return MonoSdkType.class;
	}

	@Nonnull
	@Override
	public DotNetDebugProcessBase createDebuggerProcess(@Nonnull XDebugSession session, @Nonnull RunProfile runProfile, @Nonnull DebugConnectionInfo debugConnectionInfo)
	{
		return new MonoDebugProcess(session, runProfile, debugConnectionInfo);
	}

	@Nonnull
	@Override
	public GeneralCommandLine createDefaultCommandLine(@Nonnull Sdk sdk, @Nullable DebugConnectionInfo debugConnectionInfo) throws ExecutionException
	{
		String fileName = DotNetMacroUtil.expandOutputFile(this);
		return createDefaultCommandLineImpl(sdk, debugConnectionInfo, fileName);
	}

	@Nonnull
	public static GeneralCommandLine createDefaultCommandLineImpl(@Nonnull Sdk sdk,
			@Nullable DebugConnectionInfo debugConnectionInfo,
			@Nonnull String fileName)
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();

		String runFile = MonoSdkType.getInstance().getExecutable(sdk);

		commandLine.setExePath(runFile);
		if(debugConnectionInfo != null)
		{
			commandLine.addParameter("--debug");
			commandLine.addParameter(generateParameterForRun(debugConnectionInfo));
		}
		commandLine.addParameter(fileName);
		return commandLine;
	}

	@Nonnull
	@Override
	public String getDebugFileExtension()
	{
		return getTarget().getExtension() + ".mdb";
	}

	@Nonnull
	@Override
	public String[] getSystemLibraryUrlsImpl(@Nonnull Sdk sdk, @Nonnull String name, @Nonnull OrderRootType orderRootType)
	{
		if(orderRootType == DocumentationOrderRootType.getInstance())
		{
			String[] systemLibraryUrls = super.getSystemLibraryUrlsImpl(sdk, name, orderRootType);

			VirtualFile homeDirectory = sdk.getHomeDirectory();
			if(homeDirectory == null)
			{
				return systemLibraryUrls;
			}
			VirtualFile docDir = homeDirectory.findFileByRelativePath("/../../monodoc/sources");
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

	private static String generateParameterForRun(@Nonnull DebugConnectionInfo debugConnectionInfo)
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
