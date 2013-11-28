package org.mustbe.consulo.dotnet.dll.vfs;

import org.consulo.vfs.ArchiveFileSystemBase;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.DotNetDllFileType;
import com.intellij.openapi.vfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.impl.archive.ArchiveHandler;
import com.intellij.util.messages.MessageBus;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class DotNetArchiveFileSystem extends ArchiveFileSystemBase
{
	public DotNetArchiveFileSystem(MessageBus bus)
	{
		super(bus);
	}

	@Override
	public ArchiveHandler createHandler(ArchiveFileSystem archiveFileSystem, String s)
	{
		return null;
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
