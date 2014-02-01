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

package org.mustbe.consulo.dotnet.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.impl.SdkModuleInheritableNamedPointerImpl;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.extension.DotNetMutableModuleExtension;
import org.mustbe.consulo.dotnet.module.ui.ConfigurationProfilePanel;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.Key;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBCheckBox;
import lombok.val;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class MainConfigurationProfileEx implements ConfigurationProfileEx<MainConfigurationProfileEx>
{
	public static final Key<MainConfigurationProfileEx> KEY = Key.create("main");
	private final DotNetModuleExtension myDotNetModuleExtension;

	private ModuleInheritableNamedPointerImpl<Sdk> mySdkPointer;
	private DotNetTarget myTarget = DotNetTarget.EXECUTABLE;
	private boolean myAllowDebugInfo;

	public MainConfigurationProfileEx(DotNetModuleExtension dotNetModuleExtension)
	{
		myDotNetModuleExtension = dotNetModuleExtension;
		mySdkPointer = new SdkModuleInheritableNamedPointerImpl(dotNetModuleExtension.getModule().getProject(), dotNetModuleExtension.getId());
	}

	@Override
	public void loadState(Element element)
	{
		mySdkPointer.fromXml(element);
		myTarget = DotNetTarget.valueOf(element.getAttributeValue("target", DotNetTarget.EXECUTABLE.name()));
		myAllowDebugInfo = Boolean.valueOf(element.getAttributeValue("debug", "false"));
	}

	@Override
	public void getState(Element element)
	{
		mySdkPointer.toXml(element);
		element.setAttribute("target", myTarget.name());
		element.setAttribute("debug", Boolean.toString(myAllowDebugInfo));
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		DotNetMutableModuleExtension<?> extension = modifiableRootModel.getExtension(DotNetMutableModuleExtension.class);
		assert extension != null;

		JPanel panel = new JPanel(new VerticalFlowLayout());
		panel.add(new ModuleExtensionWithSdkPanel(extension, this, runnable));

		val comp = new JComboBox(DotNetTarget.values());
		comp.setRenderer(new ListCellRendererWrapper<DotNetTarget>()
		{
			@Override
			public void customize(JList jList, DotNetTarget dotNetTarget, int i, boolean b, boolean b2)
			{
				setText(dotNetTarget.getDescription());
			}
		});
		comp.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				myTarget = (DotNetTarget) comp.getSelectedItem();
			}
		});

		panel.add(ConfigurationProfilePanel.labeledLine(DotNetBundle.message("target.label"), comp));

		val comp2 = new JBCheckBox("Generate debug information?", myAllowDebugInfo);
		comp2.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				myAllowDebugInfo = comp2.isSelected();
			}
		});
		panel.add(comp2);
		return panel;
	}

	@NotNull
	@Override
	public ConfigurationProfileEx<MainConfigurationProfileEx> clone()
	{
		MainConfigurationProfileEx profileEx = new MainConfigurationProfileEx(myDotNetModuleExtension);
		profileEx.setAllowDebugInfo(myAllowDebugInfo);
		profileEx.setTarget(myTarget);
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
	public boolean equalsEx(@NotNull MainConfigurationProfileEx ex)
	{
		return mySdkPointer.equals(ex.mySdkPointer) && myTarget.equals(ex.myTarget) && myAllowDebugInfo == ex.isAllowDebugInfo();
	}

	public boolean isAllowDebugInfo()
	{
		return myAllowDebugInfo;
	}

	public void setAllowDebugInfo(boolean allowDebugInfo)
	{
		myAllowDebugInfo = allowDebugInfo;
	}

	@NotNull
	public DotNetTarget getTarget()
	{
		return myTarget;
	}

	public void setTarget(@NotNull DotNetTarget target)
	{
		myTarget = target;
	}

	@NotNull
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return mySdkPointer;
	}
}
