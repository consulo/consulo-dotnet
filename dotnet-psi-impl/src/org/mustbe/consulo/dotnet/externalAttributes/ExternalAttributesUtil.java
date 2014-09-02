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

package org.mustbe.consulo.dotnet.externalAttributes;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.types.BinariesOrderRootType;
import com.intellij.openapi.vfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.LocalFileProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 02.09.14
 */
public class ExternalAttributesUtil
{
	@Nullable
	public static ExternalAttributeHolder findHolder(@NotNull PsiElement element)
	{
		PsiFile containingFile = element.getContainingFile();

		if(containingFile == null)
		{
			return null;
		}

		ProjectFileIndex projectFileIndex = ProjectFileIndex.SERVICE.getInstance(element.getProject());


		VirtualFile virtualFile = containingFile.getVirtualFile();
		if(virtualFile == null)
		{
			return null;
		}
		if(!projectFileIndex.isInLibraryClasses(virtualFile))
		{
			return null;
		}

		VirtualFileSystem fileSystem = virtualFile.getFileSystem();
		if(!(fileSystem instanceof ArchiveFileSystem))
		{
			return null;
		}

		VirtualFile localVirtualFileFor = ((LocalFileProvider) fileSystem).getLocalVirtualFileFor(virtualFile);
		if(localVirtualFileFor == null)
		{
			return null;
		}

		VirtualFile archiveRootForLocalFile = ArchiveVfsUtil.getArchiveRootForLocalFile(localVirtualFileFor);
		if(archiveRootForLocalFile == null)
		{
			return null;
		}

		List<OrderEntry> orderEntriesForFile = projectFileIndex.getOrderEntriesForFile(containingFile.getVirtualFile());

		List<VirtualFile> externalAttributeFiles = new SmartList<VirtualFile>();
		for(OrderEntry orderEntry : orderEntriesForFile)
		{
			if(ArrayUtil.contains(archiveRootForLocalFile, orderEntry.getFiles(BinariesOrderRootType.getInstance())))
			{
				VirtualFile[] files = orderEntry.getFiles(ExternalAttributesRootOrderType.getInstance());
				if(files.length != 0)
				{
					Collections.addAll(externalAttributeFiles, files);
				}
			}
		}

		if(externalAttributeFiles.isEmpty())
		{
			return null;
		}
		else if(externalAttributeFiles.size() == 1)
		{
			return SingleExternalAttributeHolder.load(externalAttributeFiles.get(0));
		}
		else
		{
			List<ExternalAttributeHolder> list = new SmartList<ExternalAttributeHolder>();
			for(VirtualFile externalAttributeFile : externalAttributeFiles)
			{
				list.add(SingleExternalAttributeHolder.load(externalAttributeFile));
			}
			return new CompositeExternalAttributeHolder(list);
		}
	}
}
