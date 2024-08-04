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

package consulo.dotnet.impl.library;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.progress.ProgressIndicator;
import consulo.content.base.DocumentationOrderRootType;
import consulo.content.library.ui.RootDetector;
import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.archive.ArchiveFileSystem;
import consulo.virtualFileSystem.archive.ArchiveVfsUtil;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * @author VISTALL
 * @since 01.02.14
 */
@ExtensionImpl
public class DotNetXmlDocumentationRootDetector extends RootDetector
{
	public DotNetXmlDocumentationRootDetector()
	{
		super(DocumentationOrderRootType.getInstance(), false, ".NET xml documentation");
	}

	@Nonnull
	@Override
	public Collection<VirtualFile> detectRoots(@Nonnull VirtualFile rootCandidate, @Nonnull ProgressIndicator progressIndicator)
	{
		if(rootCandidate.getFileSystem() instanceof ArchiveFileSystem)
		{
			VirtualFile localFile = ArchiveVfsUtil.getVirtualFileForArchive(rootCandidate);
			if(localFile == null || localFile.getFileType() != DotNetModuleFileType.INSTANCE)
			{
				return Collections.emptyList();
			}
			String docFilePath = localFile.getParent().getPath() + "/" + localFile.getNameWithoutExtension() + ".xml";
			VirtualFile docFile = LocalFileSystem.getInstance().findFileByIoFile(new File(docFilePath));
			if(docFile != null)
			{
				return Collections.singletonList(docFile);
			}
		}
		else if("xml".equals(rootCandidate.getExtension()))
		{
			return Collections.singletonList(rootCandidate);
		}
		return Collections.emptyList();
	}
}
