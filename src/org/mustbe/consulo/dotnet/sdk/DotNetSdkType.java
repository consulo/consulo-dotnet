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

package org.mustbe.consulo.dotnet.sdk;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;

/**
 * @author VISTALL
 * @since 20.12.13.
 */
public abstract class DotNetSdkType extends SdkType
{
	private static final String[] ORDER_DLLS = {"mscorlib.dll", "System.dll", "System.Core.dll"};

	public DotNetSdkType(@NonNls String name)
	{
		super(name);
	}

	@Nullable
	@Override
	public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator)
	{
		return null;
	}

	@Override
	public void saveAdditionalData(SdkAdditionalData sdkAdditionalData, Element element)
	{

	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == OrderRootType.CLASSES || type == OrderRootType.SOURCES || type == OrderRootType.DOCUMENTATION;
	}

	@Override
	public void setupSdkPaths(Sdk sdk)
	{
		SdkModificator sdkModificator = sdk.getSdkModificator();

		VirtualFile homeDirectory = sdk.getHomeDirectory();
		assert homeDirectory != null;

		for(String orderDll : ORDER_DLLS)
		{
			VirtualFile dllVirtualFile = homeDirectory.findFileByRelativePath(orderDll);
			if(dllVirtualFile == null)
			{
				continue;
			}

			VirtualFile jarRootForLocalFile = ArchiveVfsUtil.getJarRootForLocalFile(dllVirtualFile);
			if(jarRootForLocalFile == null)
			{
				continue;
			}
			sdkModificator.addRoot(jarRootForLocalFile, OrderRootType.CLASSES);
		}
		sdkModificator.commitChanges();
	}
}
