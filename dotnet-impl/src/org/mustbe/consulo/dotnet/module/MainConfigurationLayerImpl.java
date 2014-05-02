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

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.impl.SdkModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.DotNetRunUtil;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.compiler.DotNetMacros;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.extension.DotNetMutableModuleExtension;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import org.mustbe.consulo.module.ui.ConfigurationProfilePanel;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.Comparing;
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
import lombok.val;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class MainConfigurationLayerImpl implements MainConfigurationLayer
{
	private static final String DEFAULT_FILE_NAME = DotNetMacros.MODULE_NAME + "." + DotNetMacros.OUTPUT_FILE_EXT;
	private static final String DEFAULT_OUTPUT_DIR = DotNetMacros.MODULE_OUTPUT_DIR;

	private final DotNetModuleExtension myDotNetModuleExtension;

	private ModuleInheritableNamedPointerImpl<Sdk> mySdkPointer;
	private DotNetTarget myTarget = DotNetTarget.EXECUTABLE;
	private boolean myAllowDebugInfo;
	private boolean myAllowSourceRoots;
	private String myMainType;
	private List<String> myVariables = new ArrayList<String>();
	private String myFileName = DEFAULT_FILE_NAME;
	private String myOutputDirectory = DEFAULT_OUTPUT_DIR;

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
		myAllowSourceRoots = Boolean.valueOf(element.getAttributeValue("allow-source-roots", "false"));
		myFileName = element.getAttributeValue("file-name", DEFAULT_FILE_NAME);
		myOutputDirectory = element.getAttributeValue("output-dir", DEFAULT_OUTPUT_DIR);
		myMainType = element.getAttributeValue("main-class");

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
		element.setAttribute("allow-source-roots", Boolean.toString(myAllowSourceRoots));
		element.setAttribute("file-name", myFileName);
		element.setAttribute("output-dir", myOutputDirectory);
		if(myMainType != null)
		{
			element.setAttribute("main-class", myMainType);
		}

		for(String variable : myVariables)
		{
			element.addContent(new Element("define").setText(variable));
		}
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @NotNull final Runnable runnable)
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

		val fileNameField = new JBTextField(getFileName());
		fileNameField.getEmptyText().setText(DEFAULT_FILE_NAME);
		fileNameField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			protected void textChanged(DocumentEvent documentEvent)
			{
				myFileName = fileNameField.getText();
			}
		});

		panel.add(ConfigurationProfilePanel.labeledLine(DotNetBundle.message("file.label"), fileNameField));

		val outputDirectoryField = new JBTextField(getOutputDir());
		outputDirectoryField.getEmptyText().setText(DEFAULT_OUTPUT_DIR);
		outputDirectoryField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			protected void textChanged(DocumentEvent documentEvent)
			{
				myOutputDirectory = outputDirectoryField.getText();
			}
		});

		panel.add(ConfigurationProfilePanel.labeledLine(DotNetBundle.message("output.dir.label"), outputDirectoryField));

		val comp = new ComboBox(DotNetTarget.values());
		comp.setRenderer(new ListCellRendererWrapper<DotNetTarget>()
		{
			@Override
			public void customize(JList jList, DotNetTarget dotNetTarget, int i, boolean b, boolean b2)
			{
				setText(dotNetTarget.getDescription());
			}
		});
		comp.setSelectedItem(myTarget);
		comp.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				myTarget = (DotNetTarget) comp.getSelectedItem();
			}
		});

		panel.add(ConfigurationProfilePanel.labeledLine(DotNetBundle.message("target.label"), comp));

		DotNetPsiFacade dotNetPsiFacade = DotNetPsiFacade.getInstance(modifiableRootModel.getProject());

		DotNetTypeDeclaration selectedType = null;
		List<Object> typeDeclarations = new ArrayList<Object>();
		typeDeclarations.add(null);

		String[] allTypeNames = dotNetPsiFacade.getAllTypeNames();
		for(String allTypeName : allTypeNames)
		{
			DotNetTypeDeclaration type = dotNetPsiFacade.findType(allTypeName, extension.getScopeForResolving(false), 0);
			if(type != null && DotNetRunUtil.hasEntryPoint(type))
			{
				typeDeclarations.add(type);
				if(myMainType != null && Comparing.equal(myMainType, type.getPresentableQName()))
				{
					selectedType = type;
				}
			}
		}

		if(myMainType != null && selectedType == null)
		{
			typeDeclarations.add(myMainType);
		}

		CollectionComboBoxModel model = new CollectionComboBoxModel(typeDeclarations);
		val mainClassList = new ComboBox(model);
		mainClassList.setRenderer(new ColoredListCellRenderer()
		{
			@Override
			protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus)
			{
				if(value instanceof DotNetTypeDeclaration)
				{
					setIcon(IconDescriptorUpdaters.getIcon((PsiElement) value, 0));
					append(((DotNetTypeDeclaration) value).getPresentableQName());
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
				Object selectedItem = mainClassList.getSelectedItem();
				if(selectedItem instanceof DotNetTypeDeclaration)
				{
					myMainType = ((DotNetTypeDeclaration) selectedItem).getPresentableQName();
				}
				else if(selectedItem instanceof String)
				{
					myMainType = (String) selectedItem;
				}
				else
				{
					myMainType = null;
				}
			}
		});

		if(myMainType != null)
		{
			if(selectedType != null)
			{
				mainClassList.setSelectedItem(selectedType);
			}
			else
			{
				mainClassList.setSelectedItem(myMainType);
			}
		}

		panel.add(ConfigurationProfilePanel.labeledLine(DotNetBundle.message("main.type.label"), mainClassList));

		val debugCombobox = new JBCheckBox(DotNetBundle.message("generate.debug.info.label"), myAllowDebugInfo);
		debugCombobox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				myAllowDebugInfo = debugCombobox.isSelected();
			}
		});
		panel.add(debugCombobox);

		val allowSourceRootsBox = new JBCheckBox(DotNetBundle.message("allow.source.roots.label"), myAllowSourceRoots);
		allowSourceRootsBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				myAllowSourceRoots = allowSourceRootsBox.isSelected();
				runnable.run();
			}
		});
		panel.add(allowSourceRootsBox);

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
				String name = Messages.showInputDialog(panel, DotNetBundle.message("new.variable.message"),
						DotNetBundle.message("new.variable.title"), null, null, new InputValidator()
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
		profileEx.myAllowSourceRoots = myAllowSourceRoots;
		profileEx.setTarget(myTarget);
		profileEx.mySdkPointer.set(mySdkPointer.getModuleName(), mySdkPointer.getName());
		profileEx.myVariables.clear();
		profileEx.myVariables.addAll(myVariables);
		profileEx.myFileName = getFileName();
		profileEx.myMainType = myMainType;
		profileEx.myOutputDirectory = getOutputDir();
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
					myAllowSourceRoots == ex.isAllowSourceRoots() &&
					myVariables.equals(ex.getVariables()) &&
					myMainType.equals(ex.myMainType) &&
					getFileName().equals(ex.getFileName()) &&
					getOutputDir().equals(ex.getOutputDir());
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

	@Nullable
	@Override
	public String getMainType()
	{
		return myMainType;
	}

	@Override
	public boolean isAllowSourceRoots()
	{
		return myAllowSourceRoots;
	}

	@NotNull
	@Override
	public String getFileName()
	{
		return StringUtil.notNullize(myFileName, DEFAULT_FILE_NAME);
	}

	@NotNull
	@Override
	public String getOutputDir()
	{
		return StringUtil.notNullize(myOutputDirectory, DEFAULT_OUTPUT_DIR);
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
