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

package consulo.dotnet.library;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.dll.DotNetModuleFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.roots.libraries.ui.RootDetector;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.roots.types.DocumentationOrderRootType;
import consulo.vfs.ArchiveFileSystem;
import consulo.vfs.util.ArchiveVfsUtil;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class DotNetXmlDocumentationRootDetector extends RootDetector
{
	public DotNetXmlDocumentationRootDetector()
	{
		super(DocumentationOrderRootType.getInstance(), false, ".NET xml documentation");
	}

	@NotNull
	@Override
	public Collection<VirtualFile> detectRoots(@NotNull VirtualFile rootCandidate, @NotNull ProgressIndicator progressIndicator)
	{
		if(rootCandidate.getFileSystem() instanceof ArchiveFileSystem)
		{
			VirtualFile localFile = ArchiveVfsUtil.getVirtualFileForArchive(rootCandidate);
			if(localFile == null || localFile.getFileType() != DotNetModuleFileType.INSTANCE)
			{
				return Collections.emptyList();
			}
			String docFilePath = localFile.getParent().getPath() + "/" + localFile.getNameWithoutExtension() + XmlFileType.DOT_DEFAULT_EXTENSION;
			VirtualFile docFile = LocalFileSystem.getInstance().findFileByIoFile(new File(docFilePath));
			if(docFile != null)
			{
				return Collections.singletonList(docFile);
			}
		}
		else if(rootCandidate.getFileType() == XmlFileType.INSTANCE)
		{
			return Collections.singletonList(rootCandidate);
		}
		return Collections.emptyList();
	}
}
