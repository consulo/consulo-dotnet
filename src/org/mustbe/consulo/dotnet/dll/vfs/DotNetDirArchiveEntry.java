package org.mustbe.consulo.dotnet.dll.vfs;

import com.intellij.openapi.vfs.ArchiveEntry;

/**
 * @author VISTALL
 * @since 11.12.13.
 */
public class DotNetDirArchiveEntry implements ArchiveEntry
{
	private final String myName;
	private long myLastModified;

	public DotNetDirArchiveEntry(String name, long lastModified)
	{
		myName = name;
		myLastModified = lastModified;
	}

	@Override
	public String getName()
	{
		return myName;
	}

	@Override
	public long getSize()
	{
		return 0;
	}

	@Override
	public long getTime()
	{
		return myLastModified;
	}

	@Override
	public boolean isDirectory()
	{
		return true;
	}
}
