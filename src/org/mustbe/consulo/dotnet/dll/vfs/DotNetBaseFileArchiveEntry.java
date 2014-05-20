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
import org.mustbe.consulo.dotnet.dll.vfs.builder.XStubBuilder;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import com.intellij.openapi.util.Comparing;
import com.intellij.util.SmartList;
import edu.arizona.cs.mbel.mbel.ModuleParser;
import edu.arizona.cs.mbel.mbel.TypeDef;

/**
 * @author VISTALL
 * @since 11.12.13.
 */
@Logger
public class DotNetBaseFileArchiveEntry extends DotNetAbstractFileArchiveEntry
{
	private final List<TypeDef> myTypeDefs;

	public DotNetBaseFileArchiveEntry(ModuleParser moduleParser, TypeDef typeDef, String name, long lastModified)
	{
		super(moduleParser, name, lastModified);
		myTypeDefs = new SmartList<TypeDef>(typeDef);
	}

	public void addTypeDef(@NotNull TypeDef typeDef)
	{
		if(isAlreadyDefined(typeDef))
		{
			return;
		}
		myTypeDefs.add(typeDef);
	}

	@NotNull
	public List<TypeDef> getTypeDefs()
	{
		return myTypeDefs;
	}

	/**
	 * Return true is type already defined. It for case when one type is struct, and second is class. C# cant handle this problem
	 */
	private boolean isAlreadyDefined(@NotNull TypeDef typeDef)
	{
		//noinspection ForLoopReplaceableByForEach
		for(int i = 0; i < myTypeDefs.size(); i++)
		{
			TypeDef def = myTypeDefs.get(i);
			if(Comparing.equal(typeDef.getName(), def.getName()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	@NotNull
	public String getNamespace()
	{
		assert !myTypeDefs.isEmpty();
		return myTypeDefs.get(0).getNamespace();
	}

	@NotNull
	@Override
	public List<? extends StubBlock> build()
	{
		return XStubBuilder.parseTypeDef(getNamespace(), myTypeDefs);
	}
}
