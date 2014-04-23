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

package org.mustbe.consulo.nunit.module.extension;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.module.extension.ChildLayeredModuleExtensionImpl;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import org.mustbe.consulo.module.extension.LayeredModuleExtension;
import org.mustbe.consulo.nunit.bundle.NUnitBundleType;
import org.mustbe.consulo.nunit.module.NUnitConfigurationLayer;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class MicrosoftNUnitModuleExtension extends ChildLayeredModuleExtensionImpl<MicrosoftNUnitModuleExtension> implements
		NUnitModuleExtension<MicrosoftNUnitModuleExtension>
{
	public MicrosoftNUnitModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@NotNull
	@Override
	public Class<? extends LayeredModuleExtension> getHeadClass()
	{
		return DotNetModuleExtension.class;
	}

	@NotNull
	@Override
	protected ConfigurationLayer createLayer()
	{
		return new NUnitConfigurationLayer(this);
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		NUnitConfigurationLayer currentProfileEx = (NUnitConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk();
	}

	@Nullable
	@Override
	public Sdk getSdk()
	{
		NUnitConfigurationLayer currentProfileEx = (NUnitConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk().get();
	}

	@Nullable
	@Override
	public String getSdkName()
	{
		NUnitConfigurationLayer currentProfileEx = (NUnitConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk().getName();
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return NUnitBundleType.class;
	}

	@NotNull
	@Override
	public GeneralCommandLine createCommandLine()
	{
		Sdk sdk = getSdk();
		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(sdk.getHomePath() + "/bin/nunit-console.exe");
		return commandLine;
	}
}
