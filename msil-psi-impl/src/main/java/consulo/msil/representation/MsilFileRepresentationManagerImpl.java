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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ConcurrentMultiMap;
import com.intellij.util.containers.MultiMap;
import consulo.annotation.access.RequiredReadAction;
import consulo.disposer.Disposable;
import consulo.msil.lang.psi.MsilFile;

/**
 * @author VISTALL
 * @since 27.05.14
 */
@Singleton
public class MsilFileRepresentationManagerImpl extends MsilFileRepresentationManager implements Disposable
{
	private MultiMap<VirtualFile, PsiFile> myFiles = new ConcurrentMultiMap<>();

	@Inject
	public MsilFileRepresentationManagerImpl(Project project)
	{
		super(project);
		project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener()
		{
			@Override
			public void before(@Nonnull List<? extends VFileEvent> events)
			{
				for(VFileEvent event : events)
				{
					VirtualFile file = event.getFile();
					if(file != null)
					{
						myFiles.remove(file);
					}
				}
			}
		});
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

		Collection<PsiFile> values = myFiles.getModifiable(virtualFile);

		for(PsiFile value : values)
		{
			if(value.getFileType() == fileType)
			{
				return value;
			}
		}

		String fileName = null;
		MsilFileRepresentationProvider provider = null;
		for(MsilFileRepresentationProvider extension : MsilFileRepresentationProvider.EP_NAME.getExtensionList())
		{
			String temp = extension.getRepresentFileName(msilFile);
			if(temp != null)
			{
				fileName = temp;
				provider = extension;
				break;
			}
		}

		if(fileName == null)
		{
			return null;
		}

		PsiFile transform = provider.transform(fileName, msilFile);

		values.add(transform);
		return transform;
	}

	@Override
	public void dispose()
	{
		myFiles.clear();
	}
}
