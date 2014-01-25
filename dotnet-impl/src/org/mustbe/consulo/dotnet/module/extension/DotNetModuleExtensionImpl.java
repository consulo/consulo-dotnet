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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

import org.consulo.module.extension.MutableModuleExtensionWithSdk;
import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.DotNetVersion;
import org.mustbe.consulo.lang.variableProfile.VariableProfile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBLabel;
import lombok.val;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public abstract class DotNetModuleExtensionImpl<S extends DotNetModuleExtensionImpl<S>> extends ModuleExtensionWithSdkImpl<S> implements
		DotNetModuleExtension<S>
{
	protected DotNetTarget myTarget = DotNetTarget.EXECUTABLE;

	public DotNetModuleExtensionImpl(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	@Nullable
	public JComponent createConfigurablePanelImpl(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		JPanel panel = new JPanel(new VerticalFlowLayout());
		panel.add(new ModuleExtensionWithSdkPanel((MutableModuleExtensionWithSdk<?>) this, runnable));

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

		panel.add(labeledLine(DotNetBundle.message("target.label"), comp));

		return wrapToNorth(panel);
	}

	public boolean isModifiedImpl(S originExtension)
	{
		return super.isModifiedImpl(originExtension) || myTarget != originExtension.getTarget();
	}

	private static JPanel labeledLine(String text, JComponent component)
	{
		JPanel targetPanel = new JPanel(new BorderLayout());
		targetPanel.add(new JBLabel(text), BorderLayout.WEST);
		targetPanel.add(component);
		return targetPanel;
	}

	@NotNull
	@Override
	public DotNetTarget getTarget()
	{
		return myTarget;
	}

	@NotNull
	@Override
	public DotNetVersion getVersion()
	{
		return DotNetVersion.LAST;
	}

	@NotNull
	@Override
	public VariableProfile getCurrentProfile()
	{
		VariableProfile variableProfile = new VariableProfile("Current");
		variableProfile.addVariable("CONSULO_TEST");
		return variableProfile;
	}

	@Override
	protected void loadStateImpl(@NotNull Element element)
	{
		super.loadStateImpl(element);

		myTarget = DotNetTarget.valueOf(element.getAttributeValue("target", DotNetTarget.EXECUTABLE.name()));
	}

	@Override
	protected void getStateImpl(@NotNull Element element)
	{
		super.getStateImpl(element);

		element.setAttribute("target", myTarget.name());
	}

	@Override
	public void commit(@NotNull S mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);
		myTarget = mutableModuleExtension.myTarget;
	}
}
