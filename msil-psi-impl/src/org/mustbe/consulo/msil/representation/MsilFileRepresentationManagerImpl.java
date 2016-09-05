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
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.msil.lang.psi.MsilFile;
import org.picocontainer.Disposable;
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

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileRepresentationManagerImpl extends MsilFileRepresentationManager implements Disposable
{
	private MultiMap<VirtualFile, PsiFile> myFiles = new ConcurrentMultiMap<VirtualFile, PsiFile>();

	public MsilFileRepresentationManagerImpl(Project project)
	{
		super(project);
		project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener.Adapter()
		{
			@Override
			public void before(@NotNull List<? extends VFileEvent> events)
			{
				for(VFileEvent event : events)
				{
					myFiles.remove(event.getFile());
				}
			}
		});
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
	@RequiredReadAction
	public PsiFile getRepresentationFile(@NotNull FileType fileType, @NotNull MsilFile msilFile)
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
		for(MsilFileRepresentationProvider extension : MsilFileRepresentationProvider.EP_NAME.getExtensions())
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
