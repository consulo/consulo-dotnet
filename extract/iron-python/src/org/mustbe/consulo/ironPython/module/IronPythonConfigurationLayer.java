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

package org.mustbe.consulo.ironPython.module;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.impl.SdkModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.ironPython.module.extension.IronPythonModuleExtension;
import org.mustbe.consulo.ironPython.module.extension.IronPythonMutableModuleExtension;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.VerticalFlowLayout;
import lombok.val;

/**
 * @author VISTALL
 * @since 30.03.14
 */
public class IronPythonConfigurationLayer implements ConfigurationLayer
{
	private final IronPythonModuleExtension myModuleExtension;
	private ModuleInheritableNamedPointerImpl<Sdk> mySdkPointer;

	public IronPythonConfigurationLayer(IronPythonModuleExtension moduleExtension)
	{
		myModuleExtension = moduleExtension;
		mySdkPointer = new SdkModuleInheritableNamedPointerImpl(moduleExtension.getProject(), moduleExtension.getId());
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
		IronPythonMutableModuleExtension extension = modifiableRootModel.getExtension(IronPythonMutableModuleExtension.class);
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
	public IronPythonConfigurationLayer clone()
	{
		IronPythonConfigurationLayer profileEx = new IronPythonConfigurationLayer(myModuleExtension);
		profileEx.mySdkPointer.set(mySdkPointer.getModuleName(), mySdkPointer.getName());
		return profileEx;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof IronPythonConfigurationLayer)
		{
			return mySdkPointer.equals(((IronPythonConfigurationLayer) obj).mySdkPointer);
		}
		return false;
	}

	@NotNull
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return mySdkPointer;
	}
}
