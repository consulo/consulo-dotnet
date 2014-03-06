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

import org.apache.commons.lang.ArrayUtils;
import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.StubToStringBuilder;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.util.text.CharArrayUtil;
import edu.arizona.cs.mbel.mbel.AssemblyInfo;

/**
 * @author VISTALL
 * @since 11.12.13.
 */
@Logger
public class DotNetAssemblyFileArchiveEntry implements DotNetFileArchiveEntry
{
	public static final String AssemblyInfo = "AssemblyInfo.cs";

	private final AssemblyInfo myAssemblyInfo;
	private long myLastModified;

	private NotNullLazyValue<byte[]> myArray = new NotNullLazyValue<byte[]>()
	{
		@NotNull
		@Override
		protected byte[] compute()
		{
			StubToStringBuilder builder = new StubToStringBuilder(myAssemblyInfo);
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

	public DotNetAssemblyFileArchiveEntry(edu.arizona.cs.mbel.mbel.AssemblyInfo assemblyInfo, long lastModified)
	{
		myAssemblyInfo = assemblyInfo;
		myLastModified = lastModified;
	}

	@Override
	public String getName()
	{
		return AssemblyInfo;
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
