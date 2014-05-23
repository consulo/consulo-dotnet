package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.consulo.lombok.annotations.LazyInstance;
import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.util.XStubUtil;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * @author VISTALL
 * @since 21.03.14
 */
@Logger
public abstract class DotNetAbstractFileArchiveEntry implements DotNetFileArchiveEntry
{
	private final ModuleParser myModuleParser;
	private final String myName;
	private long myLastModified;

	public DotNetAbstractFileArchiveEntry(ModuleParser moduleParser, String name, long lastModified)
	{
		myModuleParser = moduleParser;
		myName = name;
		myLastModified = lastModified;
	}

	@NotNull
	public abstract List<? extends StubBlock> build();

	@NotNull
	@LazyInstance
	private byte[] getByteArray()
	{
		try
		{
			myModuleParser.parseNext();
		}
		catch(IOException ignored)
		{
			//
		}
		List<? extends StubBlock> builder = build();

		CharSequence charSequence = XStubUtil.buildText(builder);

		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(out);
			try
			{
				for(int i = 0; i < charSequence.length(); i++)
				{
					writer.write(charSequence.charAt(i));
				}
			}
			finally
			{
				writer.close();
			}
			return out.toByteArray();
		}
		catch(IOException e)
		{
			LOGGER.error(e);
			return ArrayUtils.EMPTY_BYTE_ARRAY;
		}
	}

	@Override
	public String getName()
	{
		return myName;
	}

	@Override
	public long getSize()
	{
		return getByteArray().length;
	}

	@Override
	public long getTime()
	{
		return myLastModified;
	}

	@Override
	public boolean isDirectory()
	{
		return false;
	}

	@Override
	@NotNull
	public String getNamespace()
	{
		return "";
	}

	@Override
	@NotNull
	public InputStream createInputStream()
	{
		return new ByteArrayInputStream(getByteArray());
	}
}
