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

package consulo.dotnet.impl.module.dependency;

import consulo.dotnet.impl.roots.orderEntry.DotNetLibraryOrderEntryModel;
import consulo.dotnet.impl.roots.orderEntry.DotNetLibraryOrderEntryType;
import consulo.dotnet.module.extension.DotNetModuleExtensionWithLibraryProviding;
import consulo.ide.setting.module.AddModuleDependencyContext;
import consulo.ide.setting.module.ClasspathPanel;
import consulo.ide.setting.module.LibrariesConfigurator;
import consulo.ide.setting.module.ModulesConfigurator;
import consulo.module.content.layer.ModifiableModuleRootLayer;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.content.layer.orderEntry.CustomOrderEntry;
import consulo.module.content.layer.orderEntry.CustomOrderEntryModel;
import consulo.module.content.layer.orderEntry.OrderEntry;
import consulo.module.extension.ModuleExtension;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @since 28.09.14
 */
public class DotNetLibraryModuleDependencyContext extends AddModuleDependencyContext<List<Map.Entry<String, String>>>
{
	public DotNetLibraryModuleDependencyContext(ClasspathPanel classpathPanel, ModulesConfigurator modulesConfigurator, LibrariesConfigurator librariesConfigurator)
	{
		super(classpathPanel, modulesConfigurator, librariesConfigurator);
	}

	@Nonnull
	@Override
	public List<OrderEntry> createOrderEntries(@Nonnull ModifiableModuleRootLayer layer, @Nonnull List<Map.Entry<String, String>> selectedValues)
	{
		List<OrderEntry> orderEntries = new ArrayList<>(selectedValues.size());
		for(Map.Entry<String, String> selectedValue : selectedValues)
		{
			DotNetLibraryOrderEntryModel model = new DotNetLibraryOrderEntryModel(selectedValue.getKey());

			CustomOrderEntry<DotNetLibraryOrderEntryModel> entry = layer.addCustomOderEntry(DotNetLibraryOrderEntryType.getInstance(), model);

			orderEntries.add(entry);
		}
		return orderEntries;
	}

	@Nonnull
	public Map<String, String> getAvailableSystemLibraries()
	{
		Map<String, String> map = new HashMap<>();
		ModifiableRootModel rootModel = myClasspathPanel.getRootModel();
		for(ModuleExtension<?> moduleExtension : rootModel.getExtensions())
		{
			if(moduleExtension instanceof DotNetModuleExtensionWithLibraryProviding)
			{
				map.putAll(((DotNetModuleExtensionWithLibraryProviding<?>) moduleExtension).getAvailableSystemLibraries());
			}
		}
		return map;
	}

	public static OrderEntry findOrderEntry(String name, ModifiableModuleRootLayer layer)
	{
		OrderEntry[] orderEntries = layer.getOrderEntries();
		for(OrderEntry orderEntry : orderEntries)
		{
			if(orderEntry instanceof CustomOrderEntry)
			{
				CustomOrderEntryModel model = ((CustomOrderEntry<?>) orderEntry).getModel();

				if(model instanceof DotNetLibraryOrderEntryModel && model.getPresentableName().equalsIgnoreCase(name))
				{
					return orderEntry;
				}
			}
		}
		return null;
	}
}
