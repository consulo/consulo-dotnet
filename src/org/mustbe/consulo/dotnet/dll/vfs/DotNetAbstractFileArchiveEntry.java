package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.ArrayUtils;
import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.XStubBuilder;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.util.text.CharArrayUtil;
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

	private NotNullLazyValue<byte[]> myArray = new NotNullLazyValue<byte[]>()
	{
		@NotNull
		@Override
		protected byte[] compute()
		{
			try
			{
				myModuleParser.parseNext();
			}
			catch(IOException ignored)
			{
				//
			}
			XStubBuilder builder = createBuilder();
			char[] chars = CharArrayUtil.fromSequence(builder.gen());
			try
			{
				return CharArrayUtil.toByteArray(chars);
			}
			catch(IOException e)
			{
				LOGGER.error(e);
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}
		}
	};

	public DotNetAbstractFileArchiveEntry(ModuleParser moduleParser, String name, long lastModified)
	{
		myModuleParser = moduleParser;
		myName = name;
		myLastModified = lastModified;
	}

	@NotNull
	public abstract XStubBuilder createBuilder();

	@Override
	public String getName()
	{
		return myName;
	}

	@Override
	public long getSize()
	{
		return myArray.getValue().length;
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
		return new ByteArrayInputStream(myArray.getValue());
	}
}
