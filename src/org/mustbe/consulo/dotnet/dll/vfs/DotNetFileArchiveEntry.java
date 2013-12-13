/*
 * Copyright 2013 must-be.org
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
import java.io.InputStream;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.StubToStringBuilder;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.ArchiveEntry;
import com.intellij.util.SmartList;
import edu.arizona.cs.mbel.mbel.TypeDef;

/**
 * @author VISTALL
 * @since 11.12.13.
 */
public class DotNetFileArchiveEntry implements ArchiveEntry
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
			StubToStringBuilder builder = new StubToStringBuilder(DotNetFileArchiveEntry.this);
			String gen = builder.gen();
			return gen.getBytes();
		}
	};

	public DotNetFileArchiveEntry(TypeDef typeDef, String name, long lastModified)
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

	@NotNull
	public String getNamespace()
	{
		assert !myTypeDefs.isEmpty();
		return myTypeDefs.get(0).getNamespace();
	}

	@NotNull
	public InputStream createInputStream()
	{
		return new ByteArrayInputStream(myArray.getValue());
	}
}
