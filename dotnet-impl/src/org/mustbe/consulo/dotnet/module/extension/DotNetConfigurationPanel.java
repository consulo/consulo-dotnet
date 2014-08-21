package org.mustbe.consulo.dotnet.module.extension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.DotNetRunUtil;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.module.roots.DotNetLibraryOrderEntryImpl;
import org.mustbe.consulo.dotnet.module.roots.DotNetLibraryOrderEntryTypeProvider;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ModifiableModuleRootLayer;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.impl.ModuleRootLayerImpl;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.ui.*;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 31.07.14
 */
public class DotNetConfigurationPanel extends JPanel
{
	public DotNetConfigurationPanel(final DotNetMutableModuleExtension<?> extension, final List<String> variables, final Runnable updater)
	{
		super(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, true));
		add(new ModuleExtensionWithSdkPanel(extension, updater));

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
				if(!mainClassList.isEnabled())
				{
					return;
				}

				Object selectedItem = mainClassList.getSelectedItem();
				if(selectedItem instanceof DotNetTypeDeclaration)
				{
					extension.setMainType(((DotNetTypeDeclaration) selectedItem).getPresentableQName());
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
				final DotNetPsiFacade dotNetPsiFacade = DotNetPsiFacade.getInstance(extension.getProject());

				final Ref<DotNetTypeDeclaration> selected = Ref.create();
				final List<Object> newItems = new ArrayList<Object>();
				newItems.add(null);

				ApplicationManager.getApplication().runReadAction(new Runnable()
				{
					@Override
					public void run()
					{
						String[] allTypeNames = dotNetPsiFacade.getAllTypeNames();
						for(String allTypeName : allTypeNames)
						{
							DotNetTypeDeclaration[] types = dotNetPsiFacade.getTypesByName(allTypeName, extension.getScopeForResolving(false));
							for(DotNetTypeDeclaration type : types)
							{
								if(type != null && type.getGenericParametersCount() == 0 && DotNetRunUtil.hasEntryPoint(type))
								{
									newItems.add(type);
									String mainType = extension.getMainType();
									if(mainType != null && Comparing.equal(mainType, type.getPresentableQName()))
									{
										selected.set(type);
									}
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
						DotNetTypeDeclaration selectedType = selected.get();
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

		val allowSourceRootsBox = new JBCheckBox(DotNetBundle.message("allow.source.roots.label"), extension.isAllowSourceRoots());
		allowSourceRootsBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				extension.setAllowSourceRoots(allowSourceRootsBox.isSelected());
				updater.run();
			}
		});
		add(allowSourceRootsBox);

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

		//add(new JBLabel("Libraries: "));
		final CheckBoxList<String> checkBoxList = new CheckBoxList<String>();
		checkBoxList.setPaintBusy(true);

		val moduleRootLayer = extension.getModuleRootLayer();

		ApplicationManager.getApplication().executeOnPooledThread(new Runnable()
		{
			@Override
			public void run()
			{
				Map<String, Boolean> map = new TreeMap<String, Boolean>();

				for(String s : new String[]{"System", "System.Core", "System.Xml"})
				{
					map.put(s, findOrderEntry(s, moduleRootLayer) != null);
				}

				checkBoxList.setStringItems(map);
				checkBoxList.setPaintBusy(false);
			}
		});

		checkBoxList.setCheckBoxListListener(new CheckBoxListListener()
		{
			@Override
			public void checkBoxSelectionChanged(int i, boolean b)
			{
				String itemAt = (String) checkBoxList.getItemAt(i);

				if(b)
				{
					DotNetLibraryOrderEntryImpl dotNetLibraryOrderEntry = new DotNetLibraryOrderEntryImpl(DotNetLibraryOrderEntryTypeProvider
							.getInstance(), (ModuleRootLayerImpl) moduleRootLayer, itemAt);

					moduleRootLayer.addOrderEntry(dotNetLibraryOrderEntry);
				}
				else
				{
					OrderEntry orderEntry = findOrderEntry(itemAt, moduleRootLayer);
					if(orderEntry != null)
					{
						moduleRootLayer.removeOrderEntry(orderEntry);
					}
				}

				UIUtil.invokeLaterIfNeeded(updater);
			}
		});
		//add(ScrollPaneFactory.createScrollPane(checkBoxList, true));
	}

	private static OrderEntry findOrderEntry(String name, ModifiableModuleRootLayer layer)
	{
		OrderEntry[] orderEntries = layer.getOrderEntries();
		for(OrderEntry orderEntry : orderEntries)
		{
			if(orderEntry instanceof DotNetLibraryOrderEntryImpl && orderEntry.getPresentableName().equals(name))
			{
				return orderEntry;
			}
		}
		return null;
	}
}
