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

package org.mustbe.consulo.nunit.bundle;

import java.io.File;

import javax.swing.Icon;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.dll.DotNetDllFileType;
import org.mustbe.consulo.nunit.NUnitIcons;
import com.intellij.ide.highlighter.XmlFileType;
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
 * @since 10.02.14
 */
public class NUnitBundleType extends SdkType
{
	public NUnitBundleType()
	{
		super("NUNIT_BUNDLE");
	}

	@Nullable
	@Override
	public String suggestHomePath()
	{
		return null;
	}

	@Override
	public boolean isValidSdkHome(String s)
	{
		return new File(s, "bin/nunit.exe").exists();
	}

	@Nullable
	@Override
	public String getVersionString(String s)
	{
		return "1";
	}

	@Override
	public String suggestSdkName(String s, String s2)
	{
		return s2;
	}

	@Nullable
	@Override
	public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator)
	{
		return null;
	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == OrderRootType.CLASSES || type == OrderRootType.DOCUMENTATION;
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return NUnitIcons.NUnit;
	}

	@Override
	public void setupSdkPaths(Sdk sdk)
	{
		SdkModificator sdkModificator = sdk.getSdkModificator();

		VirtualFile homeDirectory = sdk.getHomeDirectory();

		assert homeDirectory != null;

		VirtualFile relativePath = homeDirectory.findFileByRelativePath("bin/framework");

		if(relativePath != null)
		{
			for(VirtualFile virtualFile : relativePath.getChildren())
			{
				if(virtualFile.getFileType() == DotNetDllFileType.INSTANCE)
				{
					VirtualFile archiveRootForLocalFile = ArchiveVfsUtil.getArchiveRootForLocalFile(virtualFile);
					if(archiveRootForLocalFile != null)
					{
						sdkModificator.addRoot(archiveRootForLocalFile, OrderRootType.CLASSES);
					}
				}
				else if(virtualFile.getFileType() == XmlFileType.INSTANCE)
				{
					sdkModificator.addRoot(virtualFile, OrderRootType.DOCUMENTATION);
				}
			}
		}
		sdkModificator.commitChanges();
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "NUnit";
	}

	@Override
	public void saveAdditionalData(SdkAdditionalData sdkAdditionalData, Element element)
	{

	}
}
