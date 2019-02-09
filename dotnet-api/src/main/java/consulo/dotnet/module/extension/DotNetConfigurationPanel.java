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

package consulo.dotnet.module.extension;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
import consulo.ui.RequiredUIAccess;
import consulo.annotations.RequiredReadAction;
import consulo.awt.TargetAWT;
import consulo.dotnet.DotNetBundle;
import consulo.dotnet.DotNetTarget;
import consulo.extension.ui.ModuleExtensionSdkBoxBuilder;
import consulo.ide.IconDescriptorUpdaters;

/**
 * @author VISTALL
 * @since 31.07.14
 */
public class DotNetConfigurationPanel extends JPanel
{
	@RequiredUIAccess
	public DotNetConfigurationPanel(DotNetMutableModuleExtension<?> extension, DotNetElementQualifierProducer qualifierProducer, List<String> variables, Runnable updater)
	{
		super(new VerticalFlowLayout(true, true));
		ModuleExtensionSdkBoxBuilder<DotNetMutableModuleExtension<?>> moduleExtensionSdkBoxBuilder = ModuleExtensionSdkBoxBuilder.create(extension, updater);
		moduleExtensionSdkBoxBuilder.sdkTypeClass(extension.getSdkTypeClass());
		moduleExtensionSdkBoxBuilder.sdkPointerFunc(DotNetSimpleMutableModuleExtension::getInheritableSdk);

		add(moduleExtensionSdkBoxBuilder.build());

		JBTextField fileNameField = new JBTextField(extension.getFileName());
		fileNameField.getEmptyText().setText(DotNetModuleExtension.DEFAULT_FILE_NAME);
		fileNameField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			protected void textChanged(DocumentEvent documentEvent)
			{
				extension.setFileName(fileNameField.getText());
			}
		});

		add(LabeledComponent.create(fileNameField, DotNetBundle.message("file.label")));

		JBTextField outputDirectoryField = new JBTextField(extension.getOutputDir());
		outputDirectoryField.getEmptyText().setText(DotNetModuleExtension.DEFAULT_OUTPUT_DIR);
		outputDirectoryField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			protected void textChanged(DocumentEvent documentEvent)
			{
				extension.setOutputDir(outputDirectoryField.getText());
			}
		});

		add(LabeledComponent.create(outputDirectoryField, DotNetBundle.message("output.dir.label")));

		ComboBox<DotNetTarget> comp = new ComboBox<>(DotNetTarget.values());
		comp.setRenderer(new ColoredListCellRenderer<DotNetTarget>()
		{
			@Override
			protected void customizeCellRenderer(@Nonnull JList jList, DotNetTarget o, int i, boolean b, boolean b1)
			{
				append(o.getDescription());
			}
		});
		comp.setSelectedItem(extension.getTarget());
		comp.addActionListener(e -> extension.setTarget((DotNetTarget) comp.getSelectedItem()));

		add(LabeledComponent.create(comp, DotNetBundle.message("target.label")));

		final List<Object> items = new ArrayList<>();
		final CollectionComboBoxModel model = new CollectionComboBoxModel(items);
		ComboBox mainClassList = new ComboBox(model);
		mainClassList.setEnabled(false);
		mainClassList.setRenderer(new ColoredListCellRenderer()
		{
			@Override
			@RequiredReadAction
			protected void customizeCellRenderer(@Nonnull JList list, Object value, int index, boolean selected, boolean hasFocus)
			{
				if(!mainClassList.isEnabled())
				{
					return;
				}

				if(value instanceof PsiElement && qualifierProducer.isMyElement((PsiElement) value))
				{
					setIcon(TargetAWT.to(IconDescriptorUpdaters.getIcon((PsiElement) value, 0)));
					append(qualifierProducer.getQualifiedName((PsiElement) value));
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
		mainClassList.addActionListener(e ->
		{
			if(!mainClassList.isEnabled())
			{
				return;
			}

			Object selectedItem = mainClassList.getSelectedItem();
			if(selectedItem instanceof PsiElement && qualifierProducer.isMyElement((PsiElement) selectedItem))
			{
				extension.setMainType(qualifierProducer.getQualifiedName((PsiElement) selectedItem));
			}
			else if(selectedItem instanceof String)
			{
				extension.setMainType((String) selectedItem);
			}
			else
			{
				extension.setMainType(null);
			}
		});

		model.update();

		if(!DumbService.isDumb(extension.getProject()))
		{
			ApplicationManager.getApplication().executeOnPooledThread((Runnable) () ->
			{
				final Ref<PsiElement> selected = Ref.create();
				final List<Object> newItems = new ArrayList<>();
				newItems.add(null);

				ApplicationManager.getApplication().runReadAction(() ->
				{
					for(PsiElement psiElement : extension.getEntryPointElements())
					{
						if(qualifierProducer.isMyElement(psiElement))
						{
							newItems.add(psiElement);
							String mainType = extension.getMainType();
							if(mainType != null && Comparing.equal(mainType, qualifierProducer.getQualifiedName(psiElement)))
							{
								selected.set(psiElement);
							}
						}
					}
				});

				UIUtil.invokeLaterIfNeeded(() ->
				{
					PsiElement selectedType = selected.get();
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
				});
			});
		}

		add(LabeledComponent.create(mainClassList, DotNetBundle.message("main.type.label")));

		JBCheckBox debugCombobox = new JBCheckBox(DotNetBundle.message("generate.debug.info.label"), extension.isAllowDebugInfo());
		debugCombobox.addActionListener(e -> extension.setAllowDebugInfo(debugCombobox.isSelected()));
		add(debugCombobox);

		JBTextField namespacePrefixField = new JBTextField(extension.getNamespacePrefix());
		namespacePrefixField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			protected void textChanged(DocumentEvent documentEvent)
			{
				extension.setNamespacePrefix(namespacePrefixField.getText());
			}
		});

		final LabeledComponent<JBTextField> namespaceComponent = LabeledComponent.create(namespacePrefixField, "Namespace:");

		JBCheckBox allowSourceRootsBox = new JBCheckBox(DotNetBundle.message("allow.source.roots.label"), extension.isAllowSourceRoots());
		allowSourceRootsBox.addActionListener(e ->
		{
			extension.setAllowSourceRoots(allowSourceRootsBox.isSelected());
			namespaceComponent.setVisible(!allowSourceRootsBox.isSelected());

			updater.run();
		});
		add(allowSourceRootsBox);
		add(namespaceComponent);

		CollectionListModel<String> dataModel = new CollectionListModel<String>(variables)
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
			public void remove(@Nonnull final String element)
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

		JBList variableList = new JBList(dataModel);
		ToolbarDecorator variableDecorator = ToolbarDecorator.createDecorator(variableList);
		variableDecorator.setAddAction(anActionButton ->
		{
			String name = Messages.showInputDialog(DotNetConfigurationPanel.this, DotNetBundle.message("new.variable.message"), DotNetBundle.message("new.variable.title"), null, null, new
					InputValidator()
			{
				@RequiredUIAccess
				@Override
				public boolean checkInput(String s)
				{
					return !variables.contains(s);
				}

				@RequiredUIAccess
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
		});
		add(variableDecorator.createPanel());
	}
}
