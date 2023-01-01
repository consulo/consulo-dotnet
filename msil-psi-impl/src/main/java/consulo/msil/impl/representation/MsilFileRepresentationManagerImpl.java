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

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ServiceImpl;
import consulo.disposer.Disposable;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.msil.impl.representation.fileSystem.MsilFileRepresentationVirtualFileSystem;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationManager;
import consulo.msil.representation.MsilFileRepresentationProvider;
import consulo.project.Project;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 27.05.14
 */
@Singleton
@ServiceImpl
public class MsilFileRepresentationManagerImpl extends MsilFileRepresentationManager implements Disposable
{
	private final VirtualFileManager myVirtualFileManager;

	@Inject
	public MsilFileRepresentationManagerImpl(Project project, VirtualFileManager virtualFileManager)
	{
		super(project);
		myVirtualFileManager = virtualFileManager;
	}

	@Nonnull
	@Override
	public List<Pair<String, ? extends FileType>> getRepresentFileInfos(@Nonnull MsilFile msilFile, @Nonnull VirtualFile virtualFile)
	{
		List<MsilFileRepresentationProvider> extensions = MsilFileRepresentationProvider.EP_NAME.getExtensionList();
		List<Pair<String, ? extends FileType>> list = new ArrayList<>(extensions.size());
		for(MsilFileRepresentationProvider extension : extensions)
		{
			String fileName = extension.getRepresentFileName(msilFile);
			if(fileName != null)
			{
				list.add(Pair.create(fileName, extension.getFileType()));
			}
		}
		return list;
	}

	@Override
	@RequiredReadAction
	public PsiFile getRepresentationFile(@Nonnull FileType fileType, @Nonnull MsilFile msilFile)
	{
		VirtualFile virtualFile = msilFile.getVirtualFile();
		if(virtualFile == null)
		{
			return null;
		}

		String originalUrl = virtualFile.getUrl() + MsilFileRepresentationVirtualFileSystem.SEPARATOR + fileType.getId();

		String constructUrl = VirtualFileManager.constructUrl(MsilFileRepresentationVirtualFileSystem.PROTOCOL, originalUrl);

		VirtualFile file = myVirtualFileManager.findFileByUrl(constructUrl);
		if(file == null)
		{
			return null;
		}
		
		return PsiManager.getInstance(myProject).findFile(file);
	}

	@Override
	public void dispose()
	{

	}
}
