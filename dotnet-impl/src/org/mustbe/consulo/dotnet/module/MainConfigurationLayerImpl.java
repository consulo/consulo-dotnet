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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.impl.SdkModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
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
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import lombok.val;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class MainConfigurationLayerImpl implements MainConfigurationLayer
{
	private final DotNetModuleExtension myDotNetModuleExtension;

	private ModuleInheritableNamedPointerImpl<Sdk> mySdkPointer;
	private DotNetTarget myTarget = DotNetTarget.EXECUTABLE;
	private boolean myAllowDebugInfo;
	private List<String> myVariables = new ArrayList<String>();

	public MainConfigurationLayerImpl(DotNetModuleExtension dotNetModuleExtension)
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

		for(Element defineElement : element.getChildren("define"))
		{
			myVariables.add(defineElement.getText());
		}
	}

	@Override
	public void getState(Element element)
	{
		mySdkPointer.toXml(element);
		element.setAttribute("target", myTarget.name());
		element.setAttribute("debug", Boolean.toString(myAllowDebugInfo));

		for(String variable : myVariables)
		{
			element.addContent(new Element("define").setText(variable));
		}
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

		val dataModel = new CollectionListModel<String>(myVariables)
		{
			@Override
			public int getSize()
			{
				return myVariables.size();
			}

			@Override
			public String getElementAt(int index)
			{
				return myVariables.get(index);
			}

			@Override
			public void add(final String element)
			{
				int i = myVariables.size();
				myVariables.add(element);
				fireIntervalAdded(this, i, i);
			}

			@Override
			public void remove(@NotNull final String element)
			{
				int i = myVariables.indexOf(element);
				myVariables.remove(element);
				fireIntervalRemoved(this, i, i);
			}

			@Override
			public void remove(int index)
			{
				myVariables.remove(index);
				fireIntervalRemoved(this, index, index);
			}
		};

		val variableList = new JBList(dataModel);
		ToolbarDecorator variableDecorator = ToolbarDecorator.createDecorator(variableList);
		variableDecorator.setAddAction(new AnActionButtonRunnable()
		{
			@Override
			public void run(AnActionButton anActionButton)
			{
				String name = Messages.showInputDialog(panel, "Name", "Enter Variable Name", null, null, new InputValidator()
				{
					@Override
					public boolean checkInput(String s)
					{
						return !myVariables.contains(s);
					}

					@Override
					public boolean canClose(String s)
					{
						return true;
					}
				});

				if(StringUtil.isEmpty(name))
				{
					return;
				}

				dataModel.add(name);
			}
		});
		panel.add(variableDecorator.createPanel());
		return panel;
	}

	@NotNull
	@Override
	public ConfigurationLayer clone()
	{
		MainConfigurationLayerImpl profileEx = new MainConfigurationLayerImpl(myDotNetModuleExtension);
		profileEx.setAllowDebugInfo(myAllowDebugInfo);
		profileEx.setTarget(myTarget);
		profileEx.mySdkPointer.set(mySdkPointer.getModuleName(), mySdkPointer.getName());
		profileEx.myVariables.clear();
		profileEx.myVariables.addAll(myVariables);
		return profileEx;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof MainConfigurationLayerImpl)
		{
			MainConfigurationLayerImpl ex = (MainConfigurationLayerImpl) obj;
			return mySdkPointer.equals(ex.mySdkPointer) &&
					myTarget.equals(ex.myTarget) &&
					myAllowDebugInfo == ex.isAllowDebugInfo() &&
					myVariables.equals(ex.getVariables());
		}
		return false;
	}

	@Override
	public List<String> getVariables()
	{
		return myVariables;
	}

	@Override
	public boolean isAllowDebugInfo()
	{
		return myAllowDebugInfo;
	}

	public void setAllowDebugInfo(boolean allowDebugInfo)
	{
		myAllowDebugInfo = allowDebugInfo;
	}

	@Override
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
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return mySdkPointer;
	}
}
