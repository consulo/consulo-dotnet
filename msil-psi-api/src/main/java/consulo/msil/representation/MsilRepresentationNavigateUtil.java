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

import consulo.annotation.UsedInPlugin;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiUtilCore;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 03.06.14
 */
public class MsilRepresentationNavigateUtil
{
	public static final Consumer<PsiFile> DEFAULT_NAVIGATOR = file -> file.navigate(true);

	@RequiredReadAction
	@UsedInPlugin
	public static void navigateToRepresentation(@Nonnull final PsiElement msilElement, @Nonnull FileType fileType)
	{
		navigateToRepresentation(msilElement, fileType, DEFAULT_NAVIGATOR);
	}

	@RequiredReadAction
	@UsedInPlugin
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

		consumer.accept(representationFile);
	}
}
