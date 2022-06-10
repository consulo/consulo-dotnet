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
import consulo.ide.setting.module.CustomOrderEntryTypeEditor;
import consulo.module.content.layer.orderEntry.CustomOrderEntry;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.ui.ex.ColoredTextContainer;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 06-Jun-16
 */
public class DotNetLibraryOrderEntryTypeEditor implements CustomOrderEntryTypeEditor<DotNetLibraryOrderEntryModel>
{
	@Nonnull
	@Override
	public Consumer<ColoredTextContainer> getRender(@Nonnull CustomOrderEntry<DotNetLibraryOrderEntryModel> orderEntry, @Nonnull DotNetLibraryOrderEntryModel model)
	{
		return it -> {
			ModuleExtensionHelper moduleExtensionHelper = ModuleExtensionHelper.getInstance(orderEntry.getOwnerModule().getProject());

			DotNetSimpleModuleExtension extension = orderEntry.getModuleRootLayer().getExtension(DotNetSimpleModuleExtension.class);

			Image icon = extension == null ? AllIcons.Toolbar.Unknown : moduleExtensionHelper.getModuleExtensionIcon(extension.getId());

			it.append(model.getPresentableName());
			it.setIcon(icon);
		};
	}
}
