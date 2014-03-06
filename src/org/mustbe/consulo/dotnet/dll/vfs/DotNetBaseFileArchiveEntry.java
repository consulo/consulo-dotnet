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

package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.StubToStringBuilder;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.util.SmartList;
import com.intellij.util.text.CharArrayUtil;
import edu.arizona.cs.mbel.mbel.TypeDef;

/**
 * @author VISTALL
 * @since 11.12.13.
 */
@Logger
public class DotNetBaseFileArchiveEntry implements DotNetFileArchiveEntry
{
	private final List<TypeDef> myTypeDefs;
	private final String myName;
	private long myLastModified;

	private NotNullLazyValue<byte[]> myArray = new NotNullLazyValue<byte[]>()
	{
		@NotNull
		@Override
		protected byte[] compute()
		{
			StubToStringBuilder builder = new StubToStringBuilder(DotNetBaseFileArchiveEntry.this);
			char[] chars = CharArrayUtil.fromSequence(builder.gen());
			try
			{
				return CharArrayUtil.toByteArray(chars);
			}
			catch(IOException e)
			{
				DotNetBaseFileArchiveEntry.LOGGER.error(e);
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}
		}
	};

	public DotNetBaseFileArchiveEntry(TypeDef typeDef, String name, long lastModified)
	{
		myTypeDefs = new SmartList<TypeDef>(typeDef);
		myName = name;
		myLastModified = lastModified;
	}

	public void addTypeDef(@NotNull TypeDef typeDef)
	{
		myTypeDefs.add(typeDef);
	}

	@NotNull
	public List<TypeDef> getTypeDefs()
	{
		return myTypeDefs;
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

	@Override
	@NotNull
	public String getNamespace()
	{
		assert !myTypeDefs.isEmpty();
		return myTypeDefs.get(0).getNamespace();
	}

	@Override
	@NotNull
	public InputStream createInputStream()
	{
		return new ByteArrayInputStream(myArray.getValue());
	}
}
