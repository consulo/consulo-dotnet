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

import java.util.List;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.MsilStubBuilder;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import edu.arizona.cs.mbel.mbel.AssemblyInfo;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * @author VISTALL
 * @since 11.12.13.
 */
@Logger
public class DotNetAssemblyFileArchiveEntry extends DotNetAbstractFileArchiveEntry
{
	public static final String AssemblyInfo = "AssemblyInfo.msil";

	private AssemblyInfo myAssemblyInfo;

	public DotNetAssemblyFileArchiveEntry(ModuleParser moduleParser, AssemblyInfo assemblyInfo, long lastModified)
	{
		super(moduleParser, AssemblyInfo, lastModified);
		myAssemblyInfo = assemblyInfo;
	}

	@NotNull
	@Override
	public List<? extends StubBlock> build()
	{
		AssemblyInfo assemblyInfo = myAssemblyInfo;
		myAssemblyInfo = null;
		return MsilStubBuilder.parseAssemblyInfo(assemblyInfo);
	}
}
