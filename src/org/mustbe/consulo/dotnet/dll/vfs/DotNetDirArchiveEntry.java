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
