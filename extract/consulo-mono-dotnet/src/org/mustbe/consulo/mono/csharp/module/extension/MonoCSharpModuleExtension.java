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

package org.mustbe.consulo.mono.csharp.module.extension;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.compiler.MSBaseDotNetCompilerOptionsBuilder;
import org.mustbe.consulo.csharp.module.CSharpConfigurationProfileEx;
import org.mustbe.consulo.csharp.module.extension.CSharpModuleExtension;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import org.mustbe.consulo.dotnet.module.ConfigurationProfile;
import org.mustbe.consulo.mono.dotnet.sdk.MonoSdkType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.SystemInfo;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class MonoCSharpModuleExtension extends CSharpModuleExtension<MonoCSharpModuleExtension>
{
	public MonoCSharpModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	@NotNull
	@Override
	public DotNetCompilerOptionsBuilder createCompilerOptionsBuilder(@NotNull Sdk dotNetSdk, ConfigurationProfile currentProfile)
	{
		MSBaseDotNetCompilerOptionsBuilder optionsBuilder = new MSBaseDotNetCompilerOptionsBuilder(dotNetSdk);
		CSharpConfigurationProfileEx extension = currentProfile.getExtension(CSharpConfigurationProfileEx.KEY);
		if(extension.isAllowUnsafeCode())
		{
			optionsBuilder.addArgument("/unsafe");
		}
		optionsBuilder.addArgument("/nostdlib+");

		if(SystemInfo.isWindows)
		{
			optionsBuilder.setExecutableFromSdk("/../../../bin/mcs.bat");
		}
		else if(SystemInfo.isMac)
		{
			optionsBuilder.setExecutableFromSdk("/../../../bin/mcs");
		}
		else if(SystemInfo.isLinux)
		{
			optionsBuilder.setExecutable(MonoSdkType.LINUS_COMPILER);
		}

		return optionsBuilder;
	}
}
