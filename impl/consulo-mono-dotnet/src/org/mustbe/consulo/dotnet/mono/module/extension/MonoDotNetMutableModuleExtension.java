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

package org.mustbe.consulo.dotnet.mono.module.extension;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

import org.consulo.module.extension.MutableModuleExtensionWithSdk;
import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.DotNetTarget;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBLabel;
import lombok.val;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MonoDotNetMutableModuleExtension extends MonoDotNetModuleExtension implements MutableModuleExtensionWithSdk<MonoDotNetModuleExtension>
{
	private MonoDotNetModuleExtension myOriginalModuleExtension;

	public MonoDotNetMutableModuleExtension(@NotNull String id, @NotNull Module module, @NotNull MonoDotNetModuleExtension originalModuleExtension)
	{
		super(id, module);
		myOriginalModuleExtension = originalModuleExtension;
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		JPanel panel = new JPanel(new VerticalFlowLayout());
		panel.add(new ModuleExtensionWithSdkPanel(this, runnable));

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
				setTarget((DotNetTarget) comp.getSelectedItem());
			}
		});

		panel.add(labeledLine(DotNetBundle.message("target.label"), comp));

		return wrapToNorth(panel);
	}

	private static JPanel labeledLine(String text, JComponent component)
	{
		JPanel targetPanel = new JPanel(new BorderLayout());
		targetPanel.add(new JBLabel(text), BorderLayout.WEST);
		targetPanel.add(component);
		return targetPanel;
	}

	public void setTarget(DotNetTarget target)
	{
		myTarget = target;
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified()
	{
		return isModifiedImpl(myOriginalModuleExtension) || myTarget != myOriginalModuleExtension.getTarget();
	}

	@Override
	public void commit()
	{
		myOriginalModuleExtension.commit(this);
	}
}
