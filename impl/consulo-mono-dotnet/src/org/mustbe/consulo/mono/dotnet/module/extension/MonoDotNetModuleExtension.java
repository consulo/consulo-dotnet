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

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.DotNetVersion;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.mono.dotnet.sdk.MonoSdkType;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.util.SystemInfo;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MonoDotNetModuleExtension extends ModuleExtensionWithSdkImpl<MonoDotNetModuleExtension> implements DotNetModuleExtension<MonoDotNetModuleExtension>
{
	protected DotNetTarget myTarget = DotNetTarget.EXECUTABLE;

	public MonoDotNetModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	@Override
	protected Class<? extends SdkType> getSdkTypeClass()
	{
		return MonoSdkType.class;
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
		Sdk sdk = getSdk();
		assert sdk != null;

		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(sdk.getHomePath() + "/../../../bin/mono" + (SystemInfo.isWindows ? ".exe" : ""));
		commandLine.addParameter(fileName);
		return commandLine;
	}

	@NotNull
	@Override
	public DotNetTarget getTarget()
	{
		return myTarget;
	}

	@Override
	protected void loadStateImpl(@NotNull Element element)
	{
		super.loadStateImpl(element);

		myTarget = DotNetTarget.valueOf(element.getAttributeValue("target", DotNetTarget.EXECUTABLE.name()));
	}

	@Override
	protected void getStateImpl(@NotNull Element element)
	{
		super.getStateImpl(element);

		element.setAttribute("target", myTarget.name());
	}

	@Override
	public void commit(@NotNull MonoDotNetModuleExtension mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);
		myTarget = mutableModuleExtension.myTarget;
	}
}
