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

package consulo.dotnet.dll.vfs;

import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.internal.dotnet.asm.mbel.ModuleParser;
import consulo.internal.dotnet.asm.parse.MSILParseException;
import consulo.internal.dotnet.msil.decompiler.file.DotNetArchiveFile;
import consulo.virtualFileSystem.VirtualFileManager;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class DotNetArchiveFileSystem extends ArchiveFileSystemBase
{
	@Nonnull
	public static DotNetArchiveFileSystem getInstance()
	{
		return (DotNetArchiveFileSystem) VirtualFileManager.getInstance().getFileSystem(DotNetModuleFileType.PROTOCOL);
	}

	public DotNetArchiveFileSystem()
	{
		super(DotNetModuleFileType.PROTOCOL);
	}

	@Nonnull
	@Override
	public ArchiveFile createArchiveFile(@Nonnull String path) throws IOException
	{
		try
		{
			File file = new File(path);
			return new DotNetArchiveFile(file, new ModuleParser(new File(path)), file.lastModified());
		}
		catch(MSILParseException e)
		{
			// ignore initial parse exception
			return ArchiveFile.EMPTY;
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}
}
