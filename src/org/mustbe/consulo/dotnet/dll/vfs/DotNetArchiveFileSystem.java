package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.consulo.lombok.annotations.Logger;
import org.consulo.vfs.ArchiveFileSystemBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.dll.DotNetDllFileType;
import com.intellij.openapi.vfs.ArchiveFile;
import com.intellij.openapi.vfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.impl.archive.ArchiveHandler;
import com.intellij.openapi.vfs.impl.archive.ArchiveHandlerBase;
import com.intellij.util.messages.MessageBus;
import edu.arizona.cs.mbel.mbel.ModuleParser;
import edu.arizona.cs.mbel.parse.MSILParseException;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
@Logger
public class DotNetArchiveFileSystem extends ArchiveFileSystemBase
{
	@NotNull
	public static DotNetArchiveFileSystem getInstance()
	{
		return (DotNetArchiveFileSystem) VirtualFileManager.getInstance().getFileSystem(DotNetDllFileType.PROTOCOL);
	}

	public DotNetArchiveFileSystem(MessageBus bus)
	{
		super(bus);
	}

	@Override
	public ArchiveHandler createHandler(ArchiveFileSystem archiveFileSystem, String s)
	{
		return new ArchiveHandlerBase(archiveFileSystem, s)
		{
			@Nullable
			@Override
			protected ArchiveFile createArchiveFile()
			{
				val originalFile = getOriginalFile();
				try
				{
					File mirrorFile = getMirrorFile(originalFile);
					ModuleParser parser = new ModuleParser(new FileInputStream(mirrorFile));

					return new DotNetArchiveFile(parser.parseModule(), mirrorFile.lastModified());
				}
				catch(MSILParseException e)
				{
					LOGGER.warn(e.getMessage() + ": " + originalFile.getPath(), e);
					return null;
				}
				catch(IOException e)
				{
					LOGGER.warn(e.getMessage() + ": " + originalFile.getPath(), e);
					return null;
				}
			}
		};
	}

	@Override
	public void setNoCopyJarForPath(String s)
	{

	}

	@NotNull
	@Override
	public String getProtocol()
	{
		return DotNetDllFileType.PROTOCOL;
	}
}
