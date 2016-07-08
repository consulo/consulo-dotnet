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

package org.mustbe.consulo.msil.representation;

import java.util.Collections;
import java.util.List;

import consulo.lombok.annotations.ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.msil.MsilFileType;
import org.mustbe.consulo.msil.lang.psi.MsilFile;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

/**
 * @author VISTALL
 * @since 27.05.14
 */
@ProjectService
public abstract class MsilFileRepresentationManager
{
	protected final Project myProject;

	protected MsilFileRepresentationManager(Project project)
	{
		myProject = project;
	}

	@NotNull
	public List<Pair<String, ? extends FileType>> getRepresentFileInfos(@NotNull MsilFile msilFile)
	{
		VirtualFile virtualFile = msilFile.getVirtualFile();
		if(virtualFile == null)
		{
			return Collections.emptyList();
		}
		return getRepresentFileInfos(msilFile, virtualFile);
	}

	@NotNull
	public abstract List<Pair<String, ? extends FileType>> getRepresentFileInfos(@NotNull MsilFile msilFile, @NotNull VirtualFile virtualFile);

	@Nullable
	@RequiredReadAction
	public abstract PsiFile getRepresentationFile(@NotNull FileType fileType, @NotNull MsilFile msilFile);

	@Nullable
	@RequiredReadAction
	public PsiFile getRepresentationFile(@NotNull FileType fileType, @NotNull VirtualFile virtualFile)
	{
		if(virtualFile.getFileType() != MsilFileType.INSTANCE)
		{
			return null;
		}
		PsiFile file = PsiManager.getInstance(myProject).findFile(virtualFile);
		return file instanceof MsilFile ? getRepresentationFile(fileType, (MsilFile) file) : null;
	}
}
