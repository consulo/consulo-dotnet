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

package consulo.msil.representation;

import javax.annotation.Nonnull;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Consumer;
import consulo.annotations.Exported;
import consulo.annotations.RequiredReadAction;

/**
 * @author VISTALL
 * @since 03.06.14
 */
public class MsilRepresentationNavigateUtil
{
	public static final Consumer<PsiFile> DEFAULT_NAVIGATOR = new Consumer<PsiFile>()
	{
		@Override
		public void consume(PsiFile file)
		{
			file.navigate(true);
		}
	};

	@RequiredReadAction
	@Exported
	public static void navigateToRepresentation(@Nonnull final PsiElement msilElement, @Nonnull FileType fileType)
	{
		navigateToRepresentation(msilElement, fileType, DEFAULT_NAVIGATOR);
	}

	@RequiredReadAction
	@Exported
	public static void navigateToRepresentation(@Nonnull final PsiElement msilElement, @Nonnull FileType fileType, @Nonnull Consumer<PsiFile> consumer)
	{
		MsilFileRepresentationManager manager = MsilFileRepresentationManager.getInstance(msilElement.getProject());

		VirtualFile virtualFile = PsiUtilCore.getVirtualFile(msilElement);
		if(virtualFile == null)
		{
			return;
		}

		PsiFile representationFile = manager.getRepresentationFile(fileType, virtualFile);
		if(representationFile == null)
		{
			return;
		}

		consumer.consume(representationFile);
	}
}
