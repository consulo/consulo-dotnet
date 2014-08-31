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

package org.mustbe.consulo.dotnet.module.roots;

import org.consulo.lombok.annotations.LazyInstance;
import org.consulo.module.extension.ModuleExtensionProviderEP;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.roots.OrderEntryTypeProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.roots.ModuleRootLayer;
import com.intellij.openapi.roots.impl.ModuleRootLayerImpl;
import com.intellij.openapi.roots.ui.CellAppearanceEx;
import com.intellij.openapi.roots.ui.util.SimpleTextCellAppearance;
import com.intellij.openapi.util.InvalidDataException;

/**
 * @author VISTALL
 * @since 21.08.14
 */
public class DotNetLibraryOrderEntryTypeProvider implements OrderEntryTypeProvider<DotNetLibraryOrderEntryImpl>
{
	@NotNull
	@LazyInstance
	public static DotNetLibraryOrderEntryTypeProvider getInstance()
	{
		return EP_NAME.findExtension(DotNetLibraryOrderEntryTypeProvider.class);
	}

	@NotNull
	@Override
	public String getId()
	{
		return "dot-net-library";
	}

	@NotNull
	@Override
	public DotNetLibraryOrderEntryImpl loadOrderEntry(@NotNull Element element, @NotNull ModuleRootLayer moduleRootLayer) throws InvalidDataException
	{
		String name = element.getAttributeValue("name");
		return new DotNetLibraryOrderEntryImpl((ModuleRootLayerImpl) moduleRootLayer, name);
	}

	@Override
	public void storeOrderEntry(@NotNull Element element, @NotNull DotNetLibraryOrderEntryImpl dotNetLibraryOrderEntry)
	{
		element.setAttribute("name", dotNetLibraryOrderEntry.getPresentableName());
	}

	@NotNull
	@Override
	public CellAppearanceEx getCellAppearance(@NotNull DotNetLibraryOrderEntryImpl dotNetLibraryOrderEntry)
	{
		ModuleRootLayerImpl moduleRootLayer = dotNetLibraryOrderEntry.getModuleRootLayer();

		DotNetModuleExtension extension = moduleRootLayer.getExtension(DotNetModuleExtension.class);

		ModuleExtensionProviderEP providerEP = extension == null ? null : ModuleExtensionProviderEP.findProviderEP(extension.getId());

		return SimpleTextCellAppearance.synthetic(dotNetLibraryOrderEntry.getPresentableName(), providerEP == null ? AllIcons.Toolbar.Unknown :
				providerEP.getIcon());
	}
}
