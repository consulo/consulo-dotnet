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

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayer;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.mono.csharp.module.extension.InnerMonoModuleExtension;
import org.mustbe.consulo.mono.dotnet.module.extension.MonoDotNetModuleExtension;
import org.mustbe.consulo.nunit.bundle.NUnitBundleType;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;

/**
 * @author VISTALL
 * @since 23.04.14
 */
public class MonoNUnitModuleExtension extends InnerMonoModuleExtension<MonoNUnitModuleExtension> implements NUnitModuleExtension<MonoNUnitModuleExtension>
{
	public MonoNUnitModuleExtension(@NotNull String id, @NotNull ModifiableRootModel rootModel)
	{
		super(id, rootModel);
	}

	@Override
	protected Sdk createSdk(VirtualFile virtualFile)
	{
		SdkImpl sdk = new SdkImpl("Mono NUnit", NUnitBundleType.getInstance());
		sdk.setHomePath(virtualFile.getPath());
		sdk.setBundled();
		sdk.setVersionString(NUnitBundleType.getInstance().getVersionString(sdk));

		SdkModificator sdkModificator = sdk.getSdkModificator();

		for(String libFile : new String[]{
				"nunit.framework.dll",
				"nunit.mocks.dll"
		})
		{
			VirtualFile fileByRelativePath = virtualFile.findFileByRelativePath(libFile);
			if(fileByRelativePath != null)
			{
				VirtualFile archiveRootForLocalFile = ArchiveVfsUtil.getArchiveRootForLocalFile(fileByRelativePath);
				if(archiveRootForLocalFile != null)
				{
					sdkModificator.addRoot(archiveRootForLocalFile, OrderRootType.CLASSES);
				}
			}
		}

		sdkModificator.commitChanges();
		return sdk;
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

		DotNetModuleExtension extension = myRootModel.getExtension(DotNetModuleExtension.class);
		assert extension != null;
		MainConfigurationLayer currentLayer = (MainConfigurationLayer) extension.getCurrentLayer();

		return MonoDotNetModuleExtension.createRunCommandLineImpl(sdk.getHomePath() + "/nunit-console.exe", currentLayer, null, myParentSdk);
	}
}
