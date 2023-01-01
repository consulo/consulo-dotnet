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

package consulo.dotnet.impl.dll.vfs;

import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.internal.dotnet.asm.mbel.ModuleParser;
import consulo.internal.dotnet.asm.parse.MSILParseException;
import consulo.internal.dotnet.msil.decompiler.file.DotNetArchiveEntry;
import consulo.internal.dotnet.msil.decompiler.file.DotNetArchiveFile;
import consulo.virtualFileSystem.archive.ArchiveEntry;
import consulo.virtualFileSystem.archive.ArchiveFile;
import consulo.virtualFileSystem.archive.ArchiveFileSystemProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
@ExtensionImpl
public class DotNetArchiveFileSystemProvider implements ArchiveFileSystemProvider
{
	private static class DotNetArchiveFileWrapper implements ArchiveFile
	{
		private final DotNetArchiveFile myArchiveFile;

		private DotNetArchiveFileWrapper(DotNetArchiveFile archiveFile)
		{
			myArchiveFile = archiveFile;
		}

		@Nonnull
		@Override
		public String getName()
		{
			return myArchiveFile.getName();
		}

		@Nullable
		@Override
		public ArchiveEntry getEntry(String s)
		{
			DotNetArchiveEntry entry = myArchiveFile.getEntry(s);
			return entry == null ? null : new DotNetArchiveEntryWrapper(entry);
		}

		@Nullable
		@Override
		public InputStream getInputStream(@Nonnull ArchiveEntry archiveEntry) throws IOException
		{
			return myArchiveFile.getInputStream(((DotNetArchiveEntryWrapper) archiveEntry).myArchiveEntry);
		}

		@Nonnull
		@Override
		public Iterator<? extends ArchiveEntry> entries()
		{
			return new Iterator<>()
			{
				private Iterator<? extends DotNetArchiveEntry> myEntries = myArchiveFile.entries().iterator();

				@Override
				public boolean hasNext()
				{
					return myEntries.hasNext();
				}

				@Override
				public ArchiveEntry next()
				{
					return new DotNetArchiveEntryWrapper(myEntries.next());
				}
			};
		}

		@Override
		public int getSize()
		{
			return myArchiveFile.getSize();
		}

		@Override
		public void close() throws IOException
		{
		}
	}

	private static class DotNetArchiveEntryWrapper implements ArchiveEntry
	{
		private final DotNetArchiveEntry myArchiveEntry;

		private DotNetArchiveEntryWrapper(DotNetArchiveEntry archiveEntry)
		{
			myArchiveEntry = archiveEntry;
		}

		@Override
		public String getName()
		{
			return myArchiveEntry.getName();
		}

		@Override
		public long getSize()
		{
			return myArchiveEntry.getSize();
		}

		@Override
		public long getTime()
		{
			return myArchiveEntry.getTime();
		}

		@Override
		public boolean isDirectory()
		{
			return myArchiveEntry.isDirectory();
		}
	}

	@Nonnull
	@Override
	public String getProtocol()
	{
		return DotNetModuleFileType.PROTOCOL;
	}

	@Nonnull
	@Override
	public ArchiveFile createArchiveFile(@Nonnull String path) throws IOException
	{
		try
		{
			File file = new File(path);
			return new DotNetArchiveFileWrapper(new DotNetArchiveFile(file, new ModuleParser(new File(path)), file.lastModified()));
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
