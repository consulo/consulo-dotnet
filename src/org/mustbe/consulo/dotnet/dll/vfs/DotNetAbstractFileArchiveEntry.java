package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
		private ModuleParser myModuleParser;

		public LazyValue(ModuleParser moduleParser, DotNetAbstractFileArchiveEntry entry)
		{
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
				catch(IOException ignored)
				{
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
			catch(IOException e)
			{
				LOGGER.error(e);
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}
		}
	}

	private final String myName;
	private final long myLastModified;

	private final NotNullLazyValue<byte[]> myByteArrayValue;

	public DotNetAbstractFileArchiveEntry(ModuleParser moduleParser, String name, long lastModified)
	{
		myName = name;
		myLastModified = lastModified;
		myByteArrayValue = new LazyValue(moduleParser, this);
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
