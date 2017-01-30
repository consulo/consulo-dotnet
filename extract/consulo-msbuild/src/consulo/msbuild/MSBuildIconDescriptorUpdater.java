/*
 * Copyright 2013-2017 consulo.io
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

package consulo.msbuild;

import org.jetbrains.annotations.NotNull;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import consulo.annotations.RequiredReadAction;
import consulo.ide.IconDescriptor;
import consulo.ide.IconDescriptorUpdater;
import consulo.msbuild.roots.MSBuildGeneratedSourcesFilter;

/**
 * @author VISTALL
 * @since 29-Jan-17
 */
public class MSBuildIconDescriptorUpdater implements IconDescriptorUpdater
{
	@RequiredReadAction
	@Override
	public void updateIcon(@NotNull IconDescriptor iconDescriptor, @NotNull PsiElement element, int flags)
	{
		if(element instanceof PsiFile)
		{
			if(MSBuildGeneratedSourcesFilter.isGeneratedFile(((PsiFile) element).getVirtualFile(), element.getProject()))
			{
				iconDescriptor.addLayerIcon(AllIcons.Modules.GeneratedMark);
			}
		}
	}
}
