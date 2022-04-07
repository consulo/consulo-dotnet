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

package consulo.dotnet.module;

import consulo.content.ContentFolderTypeProvider;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.language.content.ProductionContentFolderTypeProvider;
import consulo.module.content.layer.ContentFolderSupportPatcher;
import consulo.module.content.layer.ModifiableRootModel;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author VISTALL
 * @since 31.03.14
 */
public class DotNetContentFolderSupportPatcher implements ContentFolderSupportPatcher
{
	@Override
	public void patch(@Nonnull ModifiableRootModel model, @Nonnull Set<ContentFolderTypeProvider> set)
	{
		DotNetModuleExtension extension = model.getExtension(DotNetModuleExtension.class);
		if(extension != null && extension.isAllowSourceRoots())
		{
			set.add(ProductionContentFolderTypeProvider.getInstance());
		}
	}
}
