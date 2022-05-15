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

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.msil.MsilFileType;
import consulo.msil.lang.psi.MsilFile;
import consulo.project.Project;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public abstract class MsilFileRepresentationManager
{
	@Nonnull
	public static MsilFileRepresentationManager getInstance(@Nonnull Project project)
	{
		return project.getInstance(MsilFileRepresentationManager.class);
	}

	protected final Project myProject;

	protected MsilFileRepresentationManager(Project project)
	{
		myProject = project;
	}

	@Nonnull
	public List<Pair<String, ? extends FileType>> getRepresentFileInfos(@Nonnull MsilFile msilFile)
	{
		VirtualFile virtualFile = msilFile.getVirtualFile();
		if(virtualFile == null)
		{
			return Collections.emptyList();
		}
		return getRepresentFileInfos(msilFile, virtualFile);
	}

	@Nonnull
	public abstract List<Pair<String, ? extends FileType>> getRepresentFileInfos(@Nonnull MsilFile msilFile, @Nonnull VirtualFile virtualFile);

	@Nullable
	@RequiredReadAction
	public abstract PsiFile getRepresentationFile(@Nonnull FileType fileType, @Nonnull MsilFile msilFile);

	@Nullable
	@RequiredReadAction
	public PsiFile getRepresentationFile(@Nonnull FileType fileType, @Nonnull VirtualFile virtualFile)
	{
		if(virtualFile.getFileType() != MsilFileType.INSTANCE)
		{
			return null;
		}
		PsiFile file = PsiManager.getInstance(myProject).findFile(virtualFile);
		return file instanceof MsilFile ? getRepresentationFile(fileType, (MsilFile) file) : null;
	}
}
