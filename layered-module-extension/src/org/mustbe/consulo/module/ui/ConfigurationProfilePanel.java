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

package org.mustbe.consulo.module.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import org.mustbe.consulo.module.extension.LayeredModuleExtension;
import org.mustbe.consulo.module.extension.ModuleExtensionLayerUtil;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.AbstractCollectionComboBoxModel;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class ConfigurationProfilePanel extends JPanel
{
	private final JPanel myConfigPane;

	public ConfigurationProfilePanel(final ModifiableRootModel modifiableRootModel, Runnable runnable,
			final LayeredModuleExtension<?> moduleExtension)
	{
		super(new BorderLayout());

		val layers = moduleExtension.getLayersList();

		val comboBox = new ComboBox(new AbstractCollectionComboBoxModel(layers.getSelection())
		{
			@NotNull
			@Override
			protected List getItems()
			{
				return layers;
			}
		});

		comboBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				val selectedItem = (String) comboBox.getSelectedItem();

				ModuleExtensionLayerUtil.setCurrentLayerNoCommit(modifiableRootModel, selectedItem, moduleExtension.getHeadClass());

				setActive(selectedItem);
			}
		});

		add(labeledLine("Profile: ", comboBox), BorderLayout.NORTH);

		myConfigPane = new JPanel(new CardLayout());
		myConfigPane.setBorder(IdeBorderFactory.createTitledBorder("Settings"));

		add(myConfigPane, BorderLayout.CENTER);

		for(String profile : layers)
		{
			ConfigurationLayer profileEx = moduleExtension.getLayer(profile);

			JComponent component = profileEx.createConfigurablePanel(modifiableRootModel, runnable);

			myConfigPane.add(component == null ? new JPanel() : component, profile);
		}

		setActive(layers.getSelection());
	}

	public void setActive(final String configurationProfile)
	{
		UIUtil.invokeLaterIfNeeded(new Runnable()
		{
			@Override
			public void run()
			{
				((CardLayout) myConfigPane.getLayout()).show(myConfigPane, configurationProfile);
			}
		});
	}

	public static JPanel labeledLine(String text, JComponent component)
	{
		JPanel targetPanel = new JPanel(new BorderLayout());
		targetPanel.add(new JBLabel(text), BorderLayout.WEST);
		targetPanel.add(component);
		return targetPanel;
	}
}
