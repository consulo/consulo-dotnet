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
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.HorizontalLayout;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import org.mustbe.consulo.module.extension.LayeredModuleExtension;
import org.mustbe.consulo.module.extension.ModuleExtensionLayerUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.MutableCollectionComboBoxModel;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.IconUtil;
import com.intellij.util.ListWithSelection;
import com.intellij.util.ui.UIUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class ConfigurationProfilePanel extends JPanel
{
	private final JPanel myConfigPane;

	public ConfigurationProfilePanel(@NotNull final ModifiableRootModel modifiableRootModel, @NotNull Runnable runnable,
			final LayeredModuleExtension<?> moduleExtension)
	{
		super(new BorderLayout());

		val layers = moduleExtension.getLayersList();

		val model = new MutableCollectionComboBoxModel(layers, layers.getSelection());
		val comboBox = new ComboBox(model);

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

		val removePresentation = presentation("Remove", IconUtil.getRemoveIcon());
		val removeUpdater = new Runnable()
		{
			@Override
			public void run()
			{
				removePresentation.setEnabled(comboBox.getItemCount() > 1);
			}
		};
		removeUpdater.run();

		JPanel panel = new JPanel(new HorizontalLayout());
		panel.add(new ActionButton(new AnAction()
		{
			@Override
			public void actionPerformed(AnActionEvent anActionEvent)
			{
				String newName = Messages.showInputDialog(moduleExtension.getModule().getProject(), "Name", null, Messages.getQuestionIcon(), null,
						new InputValidator()
				{
					@Override
					public boolean checkInput(String s)
					{
						return !model.contains(s);
					}

					@Override
					public boolean canClose(String s)
					{
						return true;
					}
				});

				if(newName != null)
				{
					ModuleExtensionLayerUtil.addLayerNoCommit(modifiableRootModel, newName, moduleExtension.getHeadClass());

					ListWithSelection<String> layersList = moduleExtension.getLayersList();

					model.update(layersList);

					comboBox.setSelectedItem(newName);

					ModuleExtensionLayerUtil.setCurrentLayerNoCommit(modifiableRootModel, newName, moduleExtension.getHeadClass());

					removeUpdater.run();
				}
			}
		}, presentation("Add", IconUtil.getAddIcon()), "ConfigurationProfilePanel.ComboBox", ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE));

		panel.add(new ActionButton(new AnAction()
		{
			@Override
			public void actionPerformed(AnActionEvent anActionEvent)
			{
				String selectedItem = (String) comboBox.getSelectedItem();

				ModuleExtensionLayerUtil.removeLayerNoCommit(modifiableRootModel, selectedItem, moduleExtension.getHeadClass());

				ListWithSelection<String> layersList = moduleExtension.getLayersList();

				model.update(layersList);

				comboBox.setSelectedItem(layersList.get(0));

				ModuleExtensionLayerUtil.setCurrentLayerNoCommit(modifiableRootModel, layersList.get(0), moduleExtension.getHeadClass());

				removeUpdater.run();
			}
		}, removePresentation, "ConfigurationProfilePanel.ComboBox", ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE));

		panel.add(new ActionButton(new AnAction()
		{
			@Override
			public void actionPerformed(AnActionEvent anActionEvent)
			{
				String oldSelected = (String) comboBox.getSelectedItem();
				String newName = Messages.showInputDialog(moduleExtension.getModule().getProject(), "Name", null, Messages.getQuestionIcon(),
						oldSelected, new InputValidator()
				{
					@Override
					public boolean checkInput(String s)
					{
						return !model.contains(s);
					}

					@Override
					public boolean canClose(String s)
					{
						return true;
					}
				});

				if(newName != null)
				{
					ModuleExtensionLayerUtil.copyLayerNoCommit(modifiableRootModel, oldSelected, newName, moduleExtension.getHeadClass());

					ListWithSelection<String> layersList = moduleExtension.getLayersList();

					model.update(layersList);

					comboBox.setSelectedItem(newName);

					ModuleExtensionLayerUtil.setCurrentLayerNoCommit(modifiableRootModel, newName, moduleExtension.getHeadClass());

					removeUpdater.run();
				}
			}

		}, presentation("Copy", AllIcons.Actions.Copy), "ConfigurationProfilePanel.ComboBox", ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE));

		JPanel newPanel = new JPanel(new BorderLayout());
		newPanel.setPreferredSize(new Dimension(-1, ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE.height + 2));
		newPanel.add(new JBLabel("Profile: "), BorderLayout.WEST);
		newPanel.add(comboBox, BorderLayout.CENTER);
		newPanel.add(panel, BorderLayout.EAST);
		add(newPanel, BorderLayout.NORTH);

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

	private static Presentation presentation(String text, Icon icon)
	{
		Presentation presentation = new Presentation();
		presentation.setText(text);
		presentation.setIcon(icon);
		return presentation;
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
