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

package org.mustbe.consulo.dotnet.dll.vfs.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import edu.arizona.cs.mbel.mbel.AssemblyInfo;
import edu.arizona.cs.mbel.mbel.TypeDef;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilStubBuilder extends MsilSharedBuilder
{
	@NotNull
	public static List<? extends StubBlock> parseAssemblyInfo(AssemblyInfo assemblyInfo)
	{
		StubBlock stubBlock = new StubBlock(".assembly", null, BRACES);
		processAttributes(stubBlock, assemblyInfo);
		return Collections.singletonList(stubBlock);
	}

	public static List<? extends StubBlock> parseTypeDef(String namespace, List<TypeDef> typeDefs)
	{
		List<StubBlock> list = new ArrayList<StubBlock>(typeDefs.size());
		for(int i = 0; i < typeDefs.size(); i++)
		{
			TypeDef typeDef = typeDefs.get(i);

			StubBlock stubBlock = MsilTypeBuilder.processTypeDef(typeDef);
			list.add(stubBlock);
		}
		return list;
	}

}
