package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlockUtil;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.util.ArrayUtil;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * @author VISTALL
 * @since 21.03.14
 */
@Logger
public abstract class DotNetAbstractFileArchiveEntry implements DotNetFileArchiveEntry
{
	private static class LazyValue extends NotNullLazyValue<byte[]>
	{
		private final DotNetAbstractFileArchiveEntry myEntry;
		private final File myOriginalFile;
		private ModuleParser myModuleParser;

		public LazyValue(File originalFile, ModuleParser moduleParser, DotNetAbstractFileArchiveEntry entry)
		{
			myOriginalFile = originalFile;
			myModuleParser = moduleParser;
			myEntry = entry;
		}

		@NotNull
		@Override
		protected byte[] compute()
		{
			try
			{
				try
				{
					myModuleParser.parseNext();
				}
				catch(Throwable e)
				{
					LOGGER.error("File '" + myOriginalFile.getPath() + "' cant decompiled correctly please create issue with this file", e);
					return ArrayUtil.EMPTY_BYTE_ARRAY;
				}
				finally
				{
					myModuleParser = null;
				}

				List<? extends StubBlock> builder = myEntry.build();

				CharSequence charSequence = StubBlockUtil.buildText(builder);

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
			catch(Throwable e)
			{
				LOGGER.error("File '" + myOriginalFile.getPath() + "' cant decompiled correctly please create issue with this file", e);
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}
		}
	}

	private final String myName;
	private final long myLastModified;

	private final NotNullLazyValue<byte[]> myByteArrayValue;

	public DotNetAbstractFileArchiveEntry(File originalFile, ModuleParser moduleParser, String name, long lastModified)
	{
		myName = name;
		myLastModified = lastModified;
		myByteArrayValue = new LazyValue(originalFile, moduleParser, this);
	}

	@NotNull
	public abstract List<? extends StubBlock> build();

	@Override
	public String getName()
	{
		return myName;
	}

	@Override
	public long getSize()
	{
		return myByteArrayValue.getValue().length;
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
		return new ByteArrayInputStream(myByteArrayValue.getValue());
	}
}
