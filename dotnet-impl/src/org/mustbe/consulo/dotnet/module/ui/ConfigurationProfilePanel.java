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

package org.mustbe.consulo.dotnet.module.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.ConfigurationProfile;
import org.mustbe.consulo.dotnet.module.ConfigurationProfileEx;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtensionImpl;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Key;
import com.intellij.ui.AbstractCollectionComboBoxModel;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ListCellRendererWrapper;
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

	public ConfigurationProfilePanel(ModifiableRootModel modifiableRootModel, Runnable runnable, Key<? extends ConfigurationProfileEx> key)
	{
		super(new BorderLayout());
		final DotNetModuleExtensionImpl<?> extension = modifiableRootModel.getExtension(DotNetModuleExtensionImpl.class);
		assert extension != null;

		val currentProfile = extension.getCurrentProfile();

		val comboBox = new ComboBox(new AbstractCollectionComboBoxModel(currentProfile)
		{
			@NotNull
			@Override
			protected List getItems()
			{
				return extension.getProfiles();
			}
		});
		comboBox.setRenderer(new ListCellRendererWrapper<ConfigurationProfile>()
		{
			@Override
			public void customize(JList jList, ConfigurationProfile configurationProfile, int i, boolean b, boolean b2)
			{
				setText(configurationProfile.getName());
			}
		});

		comboBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				val selectedItem = (ConfigurationProfile) comboBox.getSelectedItem();

				extension.setCurrentProfile(selectedItem.getName());

				setActive(selectedItem);
			}
		});


		add(labeledLine("Profile: ", comboBox), BorderLayout.NORTH);

		myConfigPane = new JPanel(new CardLayout());
		myConfigPane.setBorder(IdeBorderFactory.createTitledBorder("Settings"));

		add(myConfigPane, BorderLayout.CENTER);

		for(ConfigurationProfile profile : extension.getProfiles())
		{
			ConfigurationProfileEx profileEx = profile.getExtension(key);

			JComponent component = profileEx.createConfigurablePanel(modifiableRootModel, runnable);

			myConfigPane.add(component == null ? new JPanel() : component, profile.getName());
		}

		setActive(currentProfile);
	}

	public void setActive(final ConfigurationProfile configurationProfile)
	{
		UIUtil.invokeLaterIfNeeded(new Runnable()
		{
			@Override
			public void run()
			{
				((CardLayout)myConfigPane.getLayout()).show(myConfigPane, configurationProfile.getName());
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
