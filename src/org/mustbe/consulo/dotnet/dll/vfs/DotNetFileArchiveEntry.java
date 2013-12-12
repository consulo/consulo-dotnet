package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.StubToStringBuilder;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.ArchiveEntry;
import edu.arizona.cs.mbel.mbel.TypeDef;

/**
 * @author VISTALL
 * @since 11.12.13.
 */
public class DotNetFileArchiveEntry implements ArchiveEntry
{
	private final TypeDef myTypeDef;
	private final String myName;
	private long myLastModified;

	private NotNullLazyValue<byte[]> myArray = new NotNullLazyValue<byte[]>()
	{
		@NotNull
		@Override
		protected byte[] compute()
		{
			StubToStringBuilder builder = new StubToStringBuilder(myTypeDef);
			String gen = builder.gen();
			return gen.getBytes();
		}
	};

	public DotNetFileArchiveEntry(TypeDef typeDef, String name, long lastModified)
	{
		myTypeDef = typeDef;
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

	@NotNull
	public TypeDef getTypeDef()
	{
		return myTypeDef;
	}

	@NotNull
	public InputStream createInputStream()
	{
		return new ByteArrayInputStream(myArray.getValue());
	}
}
