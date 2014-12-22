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

package org.mustbe.consulo.dotnet.module.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JList;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.roots.DotNetLibraryOrderEntryImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ModifiableModuleRootLayer;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.impl.ModuleRootLayerImpl;
import com.intellij.openapi.roots.ui.configuration.classpath.ClasspathPanel;
import com.intellij.openapi.roots.ui.configuration.classpath.dependencyTab.AddModuleDependencyTabContext;
import com.intellij.openapi.roots.ui.configuration.projectRoot.StructureConfigurableContext;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Computable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ColoredListCellRendererWrapper;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.UIUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.09.14
 */
public class DotNetLibraryModuleDependencyTabContext extends AddModuleDependencyTabContext
{
	private final JBList myLibraryList;

	public DotNetLibraryModuleDependencyTabContext(ClasspathPanel classpathPanel, StructureConfigurableContext context)
	{
		super(classpathPanel, context);

		myLibraryList = new JBList();
		myLibraryList.setCellRenderer(new ColoredListCellRendererWrapper<Map.Entry<String, String>>()
		{
			@Override
			protected void doCustomize(JList jList, Map.Entry<String, String> entry, int i, boolean b, boolean b2)
			{
				append(entry.getKey());
				append(" ");
				append("(" + entry.getValue() + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
			}
		});
	}

	@NotNull
	@Override
	public String getTabName()
	{
		return "Standard Library";
	}

	@NotNull
	@Override
	public JComponent getComponent()
	{
		reloadSystemLibraries();
		return myLibraryList;
	}

	@Override
	public List<OrderEntry> createOrderEntries(@NotNull ModifiableModuleRootLayer layer, DialogWrapper dialogWrapper)
	{
		Object[] selectedValues = myLibraryList.getSelectedValues();
		List<OrderEntry> orderEntries = new ArrayList<OrderEntry>(selectedValues.length);
		for(Object selectedValue : selectedValues)
		{
			Map.Entry<String, String> entry = (Map.Entry<String, String>) selectedValue;

			DotNetLibraryOrderEntryImpl orderEntry = new DotNetLibraryOrderEntryImpl((ModuleRootLayerImpl) layer, entry.getKey());

			layer.addOrderEntry(orderEntry);

			orderEntries.add(orderEntry);
		}
		return orderEntries;
	}

	private void reloadSystemLibraries()
	{
		final DotNetModuleExtension<?> extension = myClasspathPanel.getRootModel().getExtension(DotNetModuleExtension.class);
		if(extension == null)
		{
			return;
		}
		myLibraryList.setPaintBusy(true);
		ApplicationManager.getApplication().executeOnPooledThread(new Runnable()
		{
			@Override
			public void run()
			{
				val availableSystemLibraries = extension.getAvailableSystemLibraries();

				final Map<String, String> map = ApplicationManager.getApplication().runReadAction(new Computable<Map<String, String>>()
				{
					@Override
					public Map<String, String> compute()
					{
						Map<String, String>  map = new TreeMap<String, String>();

						for(Map.Entry<String, String> entry : availableSystemLibraries.entrySet())
						{
							if(findOrderEntry(entry.getKey(), myClasspathPanel.getRootModel()) != null)
							{
								continue;
							}
							map.put(entry.getKey(), entry.getValue());
						}
						return map;
					}
				});

				UIUtil.invokeLaterIfNeeded(new Runnable()
				{
					@Override
					public void run()
					{
						CollectionListModel<Map.Entry<String, String>> model = new CollectionListModel<Map.Entry<String, String>>();
						for(Map.Entry<String, String> entry : map.entrySet())
						{
							model.add(entry);
						}
						myLibraryList.setModel(model);
						myLibraryList.setPaintBusy(false);
					}
				});
			}
		});
	}

	private static OrderEntry findOrderEntry(String name, ModifiableModuleRootLayer layer)
	{
		OrderEntry[] orderEntries = layer.getOrderEntries();
		for(OrderEntry orderEntry : orderEntries)
		{
			if(orderEntry instanceof DotNetLibraryOrderEntryImpl && orderEntry.getPresentableName().equalsIgnoreCase(name))
			{
				return orderEntry;
			}
		}
		return null;
	}
}
