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

package org.mustbe.consulo.dotnet.module.extension;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurable;
import com.intellij.openapi.roots.ui.configuration.SdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.util.Condition;

/**
 * @author VISTALL
 * @since 02.09.14
 */
public class DotNetModuleExtensionWithSdkPanel
{
	@NotNull
	public static JComponent create(@NotNull DotNetSimpleMutableModuleExtension<?> moduleExtension, @NotNull final Runnable updater)
	{
		final Class<? extends SdkType> sdkType = moduleExtension.getSdkTypeClass();
		final ProjectSdksModel projectSdksModel = ProjectStructureConfigurable.getInstance(moduleExtension.getModule().getProject())
				.getProjectSdksModel();
		final SdkComboBox sdkComboBox = new SdkComboBox(projectSdksModel, new Condition<SdkTypeId>()
		{
			@Override
			public boolean value(SdkTypeId sdkTypeId)
			{
				return sdkType.isAssignableFrom(sdkTypeId.getClass());
			}
		}, true);
		sdkComboBox.insertModuleItems(moduleExtension);

		final MutableModuleInheritableNamedPointer<Sdk> inheritableSdk = moduleExtension.getInheritableSdk();
		if(inheritableSdk.isNull())
		{
			sdkComboBox.setSelectedNoneSdk();
		}
		else
		{
			final String sdkInheritModuleName = inheritableSdk.getModuleName();
			if(sdkInheritModuleName != null)
			{
				final Module sdkInheritModule = inheritableSdk.getModule();
				if(sdkInheritModule == null)
				{
					sdkComboBox.addInvalidModuleItem(sdkInheritModuleName);
				}
				sdkComboBox.setSelectedModule(sdkInheritModuleName);
			}
			else
			{
				sdkComboBox.setSelectedSdk(inheritableSdk.getName());
			}
		}

		sdkComboBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					inheritableSdk.set(sdkComboBox.getSelectedModuleName(), sdkComboBox.getSelectedSdkName());
					updater.run();
				}
			}
		});

		return LabeledComponent.left(sdkComboBox, "Sdk:");
	}
}
