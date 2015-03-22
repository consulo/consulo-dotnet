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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.ui.ModuleExtensionSdkBoxBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredDispatchThread;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.NullableFunction;
import com.intellij.util.ui.UIUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 31.07.14
 */
public class DotNetConfigurationPanel extends JPanel
{
	@RequiredDispatchThread
	public DotNetConfigurationPanel(final DotNetMutableModuleExtension<?> extension, final List<String> variables, final Runnable updater)
	{
		super(new VerticalFlowLayout(true, true));
		val moduleExtensionSdkBoxBuilder = ModuleExtensionSdkBoxBuilder.<DotNetMutableModuleExtension<?>>create(extension, updater);
		moduleExtensionSdkBoxBuilder.sdkTypeClass(extension.getSdkTypeClass());
		moduleExtensionSdkBoxBuilder.sdkPointerFunc(new NullableFunction<DotNetMutableModuleExtension<?>,
				MutableModuleInheritableNamedPointer<Sdk>>()
		{
			@Nullable
			@Override
			public MutableModuleInheritableNamedPointer<Sdk> fun(DotNetMutableModuleExtension<?> mutableModuleExtension)
			{
				return mutableModuleExtension.getInheritableSdk();
			}
		});

		add(moduleExtensionSdkBoxBuilder.build());

		val fileNameField = new JBTextField(extension.getFileName());
		fileNameField.getEmptyText().setText(DotNetModuleExtension.DEFAULT_FILE_NAME);
		fileNameField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			protected void textChanged(DocumentEvent documentEvent)
			{
				extension.setFileName(fileNameField.getText());
			}
		});

		add(LabeledComponent.left(fileNameField, DotNetBundle.message("file.label")));

		val outputDirectoryField = new JBTextField(extension.getOutputDir());
		outputDirectoryField.getEmptyText().setText(DotNetModuleExtension.DEFAULT_OUTPUT_DIR);
		outputDirectoryField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			protected void textChanged(DocumentEvent documentEvent)
			{
				extension.setOutputDir(outputDirectoryField.getText());
			}
		});

		add(LabeledComponent.left(outputDirectoryField, DotNetBundle.message("output.dir.label")));

		val comp = new ComboBox(DotNetTarget.values());
		comp.setRenderer(new ListCellRendererWrapper<DotNetTarget>()
		{
			@Override
			public void customize(JList jList, DotNetTarget dotNetTarget, int i, boolean b, boolean b2)
			{
				setText(dotNetTarget.getDescription());
			}
		});
		comp.setSelectedItem(extension.getTarget());
		comp.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				extension.setTarget((DotNetTarget) comp.getSelectedItem());
			}
		});

		add(LabeledComponent.left(comp, DotNetBundle.message("target.label")));

		final List<Object> items = new ArrayList<Object>();
		final CollectionComboBoxModel model = new CollectionComboBoxModel(items);
		val mainClassList = new ComboBox(model);
		mainClassList.setEnabled(false);
		mainClassList.setRenderer(new ColoredListCellRenderer()
		{
			@Override
			protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus)
			{
				if(!mainClassList.isEnabled())
				{
					return;
				}

				if(value instanceof DotNetQualifiedElement)
				{
					setIcon(IconDescriptorUpdaters.getIcon((PsiElement) value, 0));
					append(((DotNetQualifiedElement) value).getPresentableQName());
				}
				else if(value instanceof String)
				{
					setIcon(AllIcons.Toolbar.Unknown);
					append((String) value, SimpleTextAttributes.ERROR_ATTRIBUTES);
				}
				else
				{
					append("<none>");
				}
			}
		});
		mainClassList.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(!mainClassList.isEnabled())
				{
					return;
				}

				Object selectedItem = mainClassList.getSelectedItem();
				if(selectedItem instanceof DotNetQualifiedElement)
				{
					extension.setMainType(((DotNetQualifiedElement) selectedItem).getPresentableQName());
				}
				else if(selectedItem instanceof String)
				{
					extension.setMainType((String) selectedItem);
				}
				else
				{
					extension.setMainType(null);
				}
			}
		});

		model.update();

		ApplicationManager.getApplication().executeOnPooledThread(new Runnable()
		{
			@Override
			public void run()
			{
				final Ref<DotNetQualifiedElement> selected = Ref.create();
				final List<Object> newItems = new ArrayList<Object>();
				newItems.add(null);

				ApplicationManager.getApplication().runReadAction(new Runnable()
				{
					@Override
					public void run()
					{
						for(PsiElement psiElement : extension.getEntryPointElements())
						{
							if(psiElement instanceof DotNetQualifiedElement)
							{
								newItems.add(psiElement);
								String mainType = extension.getMainType();
								DotNetQualifiedElement qualifiedElement = (DotNetQualifiedElement) psiElement;
								if(mainType != null && Comparing.equal(mainType, qualifiedElement.getPresentableQName()))
								{
									selected.set(qualifiedElement);
								}
							}
						}
					}
				});

				UIUtil.invokeLaterIfNeeded(new Runnable()
				{
					@Override
					public void run()
					{
						DotNetQualifiedElement selectedType = selected.get();
						String mainType = extension.getMainType();
						if(mainType != null && selectedType == null)
						{
							newItems.add(mainType);
						}

						items.clear();
						items.addAll(newItems);

						if(mainType != null)
						{
							if(selectedType != null)
							{
								model.setSelectedItem(selectedType);
							}
							else
							{
								model.setSelectedItem(mainType);
							}
						}
						else
						{
							model.setSelectedItem(null);
						}

						mainClassList.setEnabled(true);
						model.update();
					}
				});
			}
		});

		add(LabeledComponent.left(mainClassList, DotNetBundle.message("main.type.label")));

		val debugCombobox = new JBCheckBox(DotNetBundle.message("generate.debug.info.label"), extension.isAllowDebugInfo());
		debugCombobox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				extension.setAllowDebugInfo(debugCombobox.isSelected());
			}
		});
		add(debugCombobox);

		val namespacePrefixField = new JBTextField(extension.getNamespacePrefix());
		namespacePrefixField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			protected void textChanged(DocumentEvent documentEvent)
			{
				extension.setNamespacePrefix(namespacePrefixField.getText());
			}
		});

		final LabeledComponent<JBTextField> namespaceComponent = LabeledComponent.left(namespacePrefixField, "Namespace:");

		val allowSourceRootsBox = new JBCheckBox(DotNetBundle.message("allow.source.roots.label"), extension.isAllowSourceRoots());
		allowSourceRootsBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				extension.setAllowSourceRoots(allowSourceRootsBox.isSelected());
				namespaceComponent.setVisible(!allowSourceRootsBox.isSelected());

				updater.run();
			}
		});
		add(allowSourceRootsBox);
		add(namespaceComponent);

		val dataModel = new CollectionListModel<String>(variables)
		{
			@Override
			public int getSize()
			{
				return variables.size();
			}

			@Override
			public String getElementAt(int index)
			{
				return variables.get(index);
			}

			@Override
			public void add(final String element)
			{
				int i = variables.size();
				variables.add(element);
				fireIntervalAdded(this, i, i);
			}

			@Override
			public void remove(@NotNull final String element)
			{
				int i = variables.indexOf(element);
				variables.remove(element);
				fireIntervalRemoved(this, i, i);
			}

			@Override
			public void remove(int index)
			{
				variables.remove(index);
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
				String name = Messages.showInputDialog(DotNetConfigurationPanel.this, DotNetBundle.message("new.variable.message"),
						DotNetBundle.message("new.variable.title"), null, null, new InputValidator()
				{
					@Override
					public boolean checkInput(String s)
					{
						return !variables.contains(s);
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
		add(variableDecorator.createPanel());
	}
}
