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

package org.mustbe.consulo.microsoft.csharp.module.extension;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.compiler.MSBaseDotNetCompilerOptionsBuilder;
import org.mustbe.consulo.csharp.module.CSharpConfigurationProfileEx;
import org.mustbe.consulo.csharp.module.extension.CSharpModuleExtension;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import org.mustbe.consulo.dotnet.module.ConfigurationProfile;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class MicrosoftCSharpModuleExtension extends CSharpModuleExtension<MicrosoftCSharpModuleExtension>
{
	public MicrosoftCSharpModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@NotNull
	@Override
	public DotNetCompilerOptionsBuilder createCompilerOptionsBuilder(@NotNull Sdk dotNetSdk, ConfigurationProfile currentProfile)
	{
		MSBaseDotNetCompilerOptionsBuilder optionsBuilder = new MSBaseDotNetCompilerOptionsBuilder(dotNetSdk);
		optionsBuilder.addArgument("/fullpaths");
		optionsBuilder.addArgument("/utf8output");
		optionsBuilder.addArgument("/nostdlib+");
		CSharpConfigurationProfileEx extension = currentProfile.getExtension(CSharpConfigurationProfileEx.KEY);
		if(extension.isAllowUnsafeCode())
		{
			optionsBuilder.addArgument("/unsafe");
		}
		optionsBuilder.setExecutableFromSdk("csc.exe");
		return optionsBuilder;
	}
}
