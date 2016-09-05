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

package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.DotNetModuleFileType;
import org.mustbe.consulo.dotnet.module.extension.DotNetLibraryOpenCache;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.vfs.VirtualFileManager;
import consulo.internal.dotnet.msil.decompiler.file.DotNetArchiveFile;
import consulo.lombok.annotations.Logger;
import consulo.vfs.impl.archive.ArchiveFile;
import consulo.vfs.impl.archive.ArchiveFileSystemBase;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
@Logger
public class DotNetArchiveFileSystem extends ArchiveFileSystemBase implements ApplicationComponent
{
	@NotNull
	public static DotNetArchiveFileSystem getInstance()
	{
		return (DotNetArchiveFileSystem) VirtualFileManager.getInstance().getFileSystem(DotNetModuleFileType.PROTOCOL);
	}

	public DotNetArchiveFileSystem()
	{
		super(DotNetModuleFileType.PROTOCOL);
	}

	@NotNull
	@Override
	public ArchiveFile createArchiveFile(@NotNull String path) throws IOException
	{
		DotNetLibraryOpenCache.Record record = null;
		try
		{
			File file = new File(path);
			record = DotNetLibraryOpenCache.acquire(path);
			return new DotNetArchiveFile(file, record.get(), file.lastModified());
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
		finally
		{
			if(record != null)
			{
				record.finish();
			}
		}
	}
}
