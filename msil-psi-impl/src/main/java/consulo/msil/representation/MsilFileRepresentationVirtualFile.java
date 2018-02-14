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

import java.io.IOException;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.DeprecatedVirtualFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileRepresentationVirtualFile extends LightVirtualFile
{
	private static class MyVirtualFileSystem extends DeprecatedVirtualFileSystem
	{
		@NonNls
		private static final String PROTOCOL = "msil";

		private MyVirtualFileSystem()
		{
			startEventPropagation();
		}

		@Override
		public void deleteFile(Object requestor, @Nonnull VirtualFile vFile) throws IOException
		{

		}

		@Override
		public void moveFile(Object requestor, @Nonnull VirtualFile vFile, @Nonnull VirtualFile newParent) throws IOException
		{

		}

		@Override
		public void renameFile(Object requestor, @Nonnull VirtualFile vFile, @Nonnull String newName) throws IOException
		{

		}

		@Override
		public VirtualFile createChildFile(Object requestor, @Nonnull VirtualFile vDir, @Nonnull String fileName) throws IOException
		{
			return null;
		}

		@Nonnull
		@Override
		public VirtualFile createChildDirectory(Object requestor, @Nonnull VirtualFile vDir, @Nonnull String dirName) throws IOException
		{
			return null;
		}

		@Override
		public VirtualFile copyFile(Object requestor, @Nonnull VirtualFile virtualFile, @Nonnull VirtualFile newParent, @Nonnull String copyName) throws IOException
		{
			return null;
		}

		@Override
		@Nonnull
		public String getProtocol()
		{
			return PROTOCOL;
		}

		@Override
		@Nullable
		public VirtualFile findFileByPath(@Nonnull String path)
		{
			return null;
		}

		@Override
		public void refresh(boolean asynchronous)
		{
		}

		@Override
		@Nullable
		public VirtualFile refreshAndFindFileByPath(@Nonnull String path)
		{
			return null;
		}
	}

	private static final MyVirtualFileSystem ourFileSystem = new MyVirtualFileSystem();

	public MsilFileRepresentationVirtualFile(String name, FileType fileType, CharSequence text)
	{
		super(name, fileType, text);
		setWritable(false);
	}

	@Nonnull
	@Override
	public MyVirtualFileSystem getFileSystem()
	{
		return ourFileSystem;
	}
}
