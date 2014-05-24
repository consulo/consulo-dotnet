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

import java.util.List;

import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.util.XStubUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PairFunction;
import edu.arizona.cs.mbel.mbel.Event;
import edu.arizona.cs.mbel.mbel.Field;
import edu.arizona.cs.mbel.mbel.InterfaceImplementation;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.Property;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.signature.TypeAttributes;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilTypeBuilder extends MsilSharedBuilder implements TypeAttributes
{
	public static StubBlock processTypeDef(final TypeDef typeDef)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(".class ");

		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.VisibilityMask, TypeAttributes.Public))
		{
			builder.append("public ");
		}

		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.ClassSemanticsMask, TypeAttributes.Interface))
		{
			builder.append("interface ");
		}

		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.Abstract))
		{
			builder.append("abstract ");
		}

		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.Sealed))
		{
			builder.append("sealed ");
		}

		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.SpecialName))
		{
			builder.append("specialname ");
		}

		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.Serializable))
		{
			builder.append("serializable ");
		}

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
		processGeneric(builder, typeDef);

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

		for(int k = 0; k < typeDef.getNestedClasses().size(); k++)
		{
			TypeDef def = typeDef.getNestedClasses().get(k);
			if(XStubUtil.isInvisibleMember(def.getName()))
			{
				continue;
			}
			e.getBlocks().add(processTypeDef(def));
		}

		for(int j = 0; j < typeDef.getFields().size(); j++)
		{
			Field field = typeDef.getFields().get(j);

			//processAttributes(e, field);
			MsilFieldBuilder.processField(field, typeDef, e);
		}

		for(int k = 0; k < typeDef.getEvents().size(); k++)
		{
			Event event = typeDef.getEvents().get(k);
			MsilEventBuilder.processEvent(event, typeDef, e);
		}

		for(int k = 0; k < typeDef.getProperties().size(); k++)
		{
			Property property = typeDef.getProperties().get(k);
			MsilPropertyBuilder.processProperty(property, typeDef, e);
		}

		for(int k = 0; k < typeDef.getMethods().size(); k++)
		{
			MethodDef methodDef = typeDef.getMethods().get(k);

			MsilMethodBuilder.processMethod(methodDef, typeDef, e);
		}

		return e;
	}
}
