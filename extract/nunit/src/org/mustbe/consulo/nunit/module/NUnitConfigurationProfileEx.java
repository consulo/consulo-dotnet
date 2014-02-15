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

package org.mustbe.consulo.nunit.module;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.impl.SdkModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.ConfigurationProfileEx;
import org.mustbe.consulo.dotnet.module.MainConfigurationProfileEx;
import org.mustbe.consulo.dotnet.module.extension.DotNetMutableModuleExtension;
import org.mustbe.consulo.nunit.module.extension.NUnitModuleExtension;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.Key;
import lombok.val;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class NUnitConfigurationProfileEx implements ConfigurationProfileEx<NUnitConfigurationProfileEx>
{
	public static final Key<MainConfigurationProfileEx> KEY = Key.create("dotnet-unit");

	private final NUnitModuleExtension myNUnitModuleExtension;

	private ModuleInheritableNamedPointerImpl<Sdk> mySdkPointer;

	public NUnitConfigurationProfileEx(NUnitModuleExtension netModuleExtension)
	{
		myNUnitModuleExtension = netModuleExtension;
		mySdkPointer = new SdkModuleInheritableNamedPointerImpl(netModuleExtension.getModule().getProject(), netModuleExtension.getId());
	}

	@Override
	public void loadState(Element element)
	{
		mySdkPointer.fromXml(element);
	}

	@Override
	public void getState(Element element)
	{
		mySdkPointer.toXml(element);
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		DotNetMutableModuleExtension<?> extension = modifiableRootModel.getExtension(DotNetMutableModuleExtension.class);
		assert extension != null;

		val panel = new JPanel(new VerticalFlowLayout());
		panel.add(new ModuleExtensionWithSdkPanel(extension, runnable)
		{
			@NotNull
			@Override
			public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
			{
				return mySdkPointer;
			}
		});

		return panel;
	}

	@NotNull
	@Override
	public NUnitConfigurationProfileEx clone()
	{
		NUnitConfigurationProfileEx profileEx = new NUnitConfigurationProfileEx(myNUnitModuleExtension);
		profileEx.mySdkPointer.set(mySdkPointer.getModuleName(), mySdkPointer.getName());
		return profileEx;
	}

	@NotNull
	@Override
	public Key<?> getKey()
	{
		return KEY;
	}

	@Override
	public boolean equalsEx(@NotNull NUnitConfigurationProfileEx ex)
	{
		return mySdkPointer.equals(ex.mySdkPointer);
	}

	@NotNull
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return mySdkPointer;
	}
}
