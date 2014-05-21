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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PairFunction;
import edu.arizona.cs.mbel.mbel.AssemblyInfo;
import edu.arizona.cs.mbel.mbel.Field;
import edu.arizona.cs.mbel.mbel.InterfaceImplementation;
import edu.arizona.cs.mbel.mbel.MethodDef;
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

			StubBlock stubBlock = processTypeDef(typeDef);
			list.add(stubBlock);
		}
		return list;
	}

	private static StubBlock processTypeDef(final TypeDef typeDef)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(".class ");

		if(StringUtil.isEmpty(typeDef.getNamespace()))
		{
			builder.append(typeDef.getName());
		}
		else
		{
			builder.append(typeDef.getNamespace());
			builder.append(".");
			builder.append(typeDef.getName());
		}

		Object superClass = typeDef.getSuperClass();
		if(superClass != null)
		{
			builder.append(" extends ");
			toStringFromDefRefSpec(builder, superClass, typeDef);
		}

		List<InterfaceImplementation> interfaceImplementations = typeDef.getInterfaceImplementations();
		if(!interfaceImplementations.isEmpty())
		{
			builder.append(" implements ");

			join(builder, interfaceImplementations, new PairFunction<StringBuilder, InterfaceImplementation, Void>()
			{
				@Override
				public Void fun(StringBuilder builder, InterfaceImplementation o)
				{
					toStringFromDefRefSpec(builder, o.getInterface(), typeDef);
					return null;
				}
			}, ", ");
		}

		StubBlock e = new StubBlock(builder, null, BRACES);
		processAttributes(e, typeDef);

		for(int k = 0; k < typeDef.getNestedClasses().length; k++)
		{
			TypeDef def = typeDef.getNestedClasses()[k];
			e.getBlocks().add(processTypeDef(def));
		}

		for(int j = 0; j < typeDef.getFields().size(); j++)
		{
			Field field = typeDef.getFields().get(j);

			//processAttributes(e, field);
			MsilFieldBuilder.processField(field, typeDef, e);
		}

		for(int k = 0; k < typeDef.getMethods().size(); k++)
		{
			MethodDef methodDef = typeDef.getMethods().get(k);

			MsilMethodBuilder.processMethod(methodDef, typeDef, e);
		}

		return e;
	}
}