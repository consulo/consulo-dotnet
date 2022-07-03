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

package consulo.msil.impl.representation;

import consulo.annotation.component.ExtensionImpl;
import consulo.ide.impl.idea.codeInsight.daemon.impl.analysis.DefaultHighlightingSettingProvider;
import consulo.ide.impl.idea.codeInsight.daemon.impl.analysis.FileHighlightingSetting;
import consulo.msil.impl.representation.fileSystem.MsilFileRepresentationVirtualFile;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 03.06.14
 */
@ExtensionImpl
public class MsilRepresentationHighlightingSettingProvider extends DefaultHighlightingSettingProvider
{
	@Nullable
	@Override
	public FileHighlightingSetting getDefaultSetting(@Nonnull Project project, @Nonnull VirtualFile file)
	{
		if(file instanceof MsilFileRepresentationVirtualFile)
		{
			return FileHighlightingSetting.SKIP_INSPECTION;
		}
		return null;
	}
}
