/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.impl.roots.orderEntry;

import consulo.application.AllIcons;
import consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import consulo.ide.impl.idea.openapi.roots.ui.CellAppearanceEx;
import consulo.ide.impl.idea.openapi.roots.ui.util.SimpleTextCellAppearance;
import consulo.ide.impl.roots.orderEntry.OrderEntryTypeEditor;
import consulo.module.impl.internal.extension.ModuleExtensionProviders;
import consulo.module.impl.internal.layer.ModuleExtensionProviderEP;
import consulo.module.impl.internal.layer.ModuleRootLayerImpl;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 06-Jun-16
 */
public class DotNetLibraryOrderEntryTypeEditor implements OrderEntryTypeEditor<DotNetLibraryOrderEntryImpl>
{
	@Nonnull
	@Override
	public CellAppearanceEx getCellAppearance(@Nonnull DotNetLibraryOrderEntryImpl dotNetLibraryOrderEntry)
	{
		ModuleRootLayerImpl moduleRootLayer = dotNetLibraryOrderEntry.getModuleRootLayer();

		DotNetSimpleModuleExtension extension = moduleRootLayer.getExtension(DotNetSimpleModuleExtension.class);

		ModuleExtensionProviderEP providerEP = extension == null ? null : ModuleExtensionProviders.findProvider(extension.getId());

		return SimpleTextCellAppearance.synthetic(dotNetLibraryOrderEntry.getPresentableName(), providerEP == null ? AllIcons.Toolbar.Unknown : providerEP.getIcon());
	}
}
