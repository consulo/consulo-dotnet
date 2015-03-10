/*
 * Copyright 2013-2015 must-be.org
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

package org.mustbe.consulo.microsoft.dotnet.sdk;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.ui.SdkPathEditor;
import com.intellij.openapi.roots.ui.OrderRootTypeUIFactory;

/**
 * @author VISTALL
 * @since 11.03.2015
 */
public class MicrosoftCompilerDirOrderRootTypeEditor implements OrderRootTypeUIFactory
{
	@Override
	public SdkPathEditor createPathEditor(Sdk sdk)
	{
		return new SdkPathEditor(getNodeText(), MicrosoftCompilerDirOrderRootType.getInstance(),
				FileChooserDescriptorFactory.createSingleFolderDescriptor(), sdk);
	}

	@Override
	public Icon getIcon()
	{
		return AllIcons.Nodes.Folder;
	}

	@Override
	public String getNodeText()
	{
		return "Compiler paths";
	}
}
