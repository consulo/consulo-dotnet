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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.msil.MsilFileType;
import org.mustbe.consulo.msil.lang.psi.MsilFile;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.MultiMap;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileRepresentationManagerImpl extends MsilFileRepresentationManager
{
	private MultiMap<VirtualFile, PsiFile> myFiles = new MultiMap<VirtualFile, PsiFile>();
	private final Project myProject;

	public MsilFileRepresentationManagerImpl(Project project)
	{
		myProject = project;
	}

	@NotNull
	@Override
	public List<Pair<String, ? extends FileType>> getRepresentFileInfos(@NotNull MsilFile msilFile, @NotNull VirtualFile virtualFile)
	{
		MsilFileRepresentationProvider[] extensions = MsilFileRepresentationProvider.EP_NAME.getExtensions();
		List<Pair<String, ? extends FileType>> list = new ArrayList<Pair<String, ? extends FileType>>(extensions.length);
		for(MsilFileRepresentationProvider extension : extensions)
		{
			String fileName = extension.getRepresentFileName(msilFile);
			if(fileName != null)
			{
				list.add(new Pair<String, FileType>(fileName, extension.getFileType()));
			}
		}
		return list;
	}

	@Override
	public PsiFile getRepresentationFile(@NotNull FileType fileType, @NotNull VirtualFile msilFile)
	{
		if(msilFile.getFileType() != MsilFileType.INSTANCE)
		{
			return null;
		}

		MsilFile file = (MsilFile) PsiManager.getInstance(myProject).findFile(msilFile);
		if(file == null)
		{
			return null;
		}

		Collection<PsiFile> values = myFiles.getModifiable(msilFile);

		for(PsiFile value : values)
		{
			if(value.getFileType() == fileType)
			{
				return value;
			}
		}

		String name = null;
		for(Pair<String, ? extends FileType> pair : getRepresentFileInfos(file, msilFile))
		{
			if(pair.getSecond() == fileType)
			{
				name = pair.getFirst();
				break;
			}
		}

		if(name == null)
		{
			return null;
		}

		for(MsilFileRepresentationProvider provider : MsilFileRepresentationProvider.EP_NAME.getExtensions())
		{
			if(provider.getFileType() == fileType)
			{
				PsiFile transform = provider.transform(name, file);

				values.add(transform);
				return transform;
			}
		}
		return null;
	}
}
