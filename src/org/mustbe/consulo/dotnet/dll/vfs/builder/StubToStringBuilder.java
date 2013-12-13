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

package org.mustbe.consulo.dotnet.dll.vfs.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetClasses;
import org.mustbe.consulo.dotnet.dll.vfs.DotNetFileArchiveEntry;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Function;
import edu.arizona.cs.mbel.mbel.GenericParamDef;
import edu.arizona.cs.mbel.mbel.GenericParamOwner;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.Property;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.mbel.TypeRef;
import edu.arizona.cs.mbel.signature.MethodAttributes;
import edu.arizona.cs.mbel.signature.ParameterSignature;
import edu.arizona.cs.mbel.signature.TypeAttributes;
import lombok.val;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
public class StubToStringBuilder
{
	private static final String CONSTRUCTOR_NAME = ".ctor";
	private static final String STATIC_CONSTRUCTOR_NAME = ".cctor";
	private static final String ARRAY_PROPERTY_NAME = "Item";

	private static Map<String, String> SPECIAL_METHOD_NAMES = new HashMap<String, String>()
	{
		{
			put("op_Addition", "+");
			put("op_Subtraction", "-");
			put("op_Multiply", "*");
			put("op_Division", "/");
			put("op_Modulus", "%");
			put("op_BitwiseAnd", "&");
			put("op_BitwiseOr", "|");
			put("op_ExclusiveOr", "^");
			put("op_LeftShift", "<<");
			put("op_RightShift", ">>");
			put("op_Equality", "==");
			put("op_Inequality", "!=");
			put("op_LessThan", "<");
			put("op_LessThanOrEqual", "<=");
			put("op_GreaterThan", ">");
			put("op_GreaterThanOrEqual", ">=");
			put("op_OnesComplement", "~");
			put("op_LogicalNot", "!");
		}
	};

	private static List<String> SKIPPED = new ArrayList<String>()
	{
		{
			add("System.Runtime.Serialization.ISerializable.GetObjectData");
		}
	};

	private StubBlock myRoot;

	public StubToStringBuilder(DotNetFileArchiveEntry archiveEntry)
	{
		StubBlock namespaceBlock = processNamespace(archiveEntry.getNamespace());
		if(namespaceBlock != null)
		{
			myRoot = namespaceBlock;
		}

		for(TypeDef typeDef : archiveEntry.getTypeDefs())
		{
			StubBlock typeBlock = processType(typeDef);
			if(namespaceBlock != null)
			{
				namespaceBlock.getBlocks().add(typeBlock);
			}
			else
			{
				// if more that one type in future file
				if(myRoot != null)
				{
					val oldRoot = myRoot;

					myRoot = new StubBlock("", null, ' ', ' ');
					myRoot.getBlocks().add(oldRoot);
					myRoot.getBlocks().add(typeBlock);
				}
				else
				{
					myRoot = typeBlock;
				}
			}
		}
	}

	// System.MulticastDelegate
	// Invoke
	@NotNull
	private static StubBlock processType(TypeDef typeDef)
	{
		TypeRef superClass = typeDef.getSuperClass();
		if(superClass != null && Comparing.equal(DotNetClasses.System_MulticastDelegate, superClass.getFullName()))
		{
			for(MethodDef methodDef : typeDef.getMethods())
			{
				if("Invoke".equals(methodDef.getName()))
				{
					return processMethod(typeDef, methodDef, StubToStringUtil.getUserTypeDefName(typeDef), true);
				}

			}
			assert true;
		}

		StringBuilder builder = new StringBuilder();
		if(isSet(typeDef.getFlags(), TypeAttributes.VisibilityMask, TypeAttributes.Public))
		{
			builder.append("public ");
		}
		else
		{
			builder.append("internal ");
		}

		if(isSet(typeDef.getFlags(), TypeAttributes.Sealed))
		{
			builder.append("sealed ");
		}

		if(isSet(typeDef.getFlags(), TypeAttributes.Abstract))
		{
			builder.append("abstract ");
		}

		if(typeDef.isEnum())
		{
			builder.append("enum ");
		}
		else if(typeDef.isValueType())
		{
			builder.append("struct ");
		}
		else if(isSet(typeDef.getFlags(), TypeAttributes.Interface))
		{
			builder.append("interface ");
		}
		else
		{
			builder.append("class ");
		}
		builder.append(StubToStringUtil.getUserTypeDefName(typeDef));

		processGenericParameterList(typeDef, builder);

		StubBlock stubBlock = new StubBlock(builder.toString(), null, '{', '}');
		processMembers(typeDef, stubBlock);
		return stubBlock;
	}

	private static void processMembers(TypeDef typeDef, StubBlock parent)
	{
		for(Property property : typeDef.getProperties())
		{
			StubBlock stubBlock = processProperty(property);

			parent.getBlocks().add(stubBlock);
		}

		for(MethodDef methodDef : typeDef.getMethods())
		{
			StubBlock stubBlock = processMethod(typeDef, methodDef, methodDef.getName(), false);

			if(stubBlock == null)
			{
				continue;
			}
			parent.getBlocks().add(stubBlock);
		}
	}

	private static StubBlock processProperty(Property property)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(TypeToStringBuilder.typeToString(property.getSignature().getType()));
		builder.append(" ");
		builder.append(property.getName());

		StubBlock stubBlock = new StubBlock(builder.toString(), null, '{', '}');
		return stubBlock;
	}

	private static StubBlock processMethod(TypeDef typeDef, MethodDef methodDef, String name, boolean delegate)
	{
		for(String prefix : SKIPPED)
		{
			if(StringUtil.equals(name, prefix))
			{
				return null;
			}
		}

		if(isSet(methodDef.getFlags(), MethodAttributes.SpecialName))
		{
			// dont show properties methods
			if(name.startsWith("get_") || name.startsWith("set_") || name.equals(STATIC_CONSTRUCTOR_NAME))
			{
				return null;
			}

			String operator = SPECIAL_METHOD_NAMES.get(name);
			if(operator != null)
			{
				name = operator;
			}
		}

		StringBuilder builder = new StringBuilder();

		if(isSet(methodDef.getFlags(), MethodAttributes.MemberAccessMask, MethodAttributes.Public))
		{
			builder.append("public ");
		}
		else if(isSet(methodDef.getFlags(), MethodAttributes.MemberAccessMask, MethodAttributes.Private))
		{
			builder.append("private ");

		}
		else
		{
			builder.append("internal ");
		}

		boolean noBody = false;
		if(delegate)
		{
			builder.append("delegate ");
			noBody = true;
		}
		else
		{
			if(isSet(methodDef.getFlags(), MethodAttributes.Abstract))
			{
				builder.append("abstract ");
				noBody = true;
			}

			if(isSet(methodDef.getFlags(), MethodAttributes.Static))
			{
				builder.append("static ");
			}

			if(isSet(methodDef.getFlags(), MethodAttributes.Virtual))
			{
				builder.append("virtual ");
			}
		}

		if(isSet(methodDef.getFlags(), MethodAttributes.Final))
		{
			//builder.append("final "); //TODO [VISTALL] final  ? maybe sealed ?
		}

		if(name.equals(CONSTRUCTOR_NAME))
		{
			builder.append(StubToStringUtil.getUserTypeDefName(typeDef));
		}
		else
		{
			builder.append(TypeToStringBuilder.typeToString(methodDef.getSignature().getReturnType().getType())).append(" ");

			if(SPECIAL_METHOD_NAMES.containsValue(name))
			{
				builder.append("operator ");
			}

			builder.append(name);
		}

		processGenericParameterList(methodDef, builder);

		processParameterList(methodDef.getSignature().getParameters(), builder);

		if(noBody)
		{
			return new StubBlock(builder.toString(), null);
		}
		else
		{
			return new StubBlock(builder.toString(), "// Bodies decompilation is not supported\n", '{', '}');
		}
	}

	private static void processParameterList(ParameterSignature[] owner, StringBuilder builder)
	{
		String text = StringUtil.join(owner, new Function<ParameterSignature, String>()
		{
			@Override
			public String fun(ParameterSignature paramDef)
			{
				StringBuilder p = new StringBuilder();
				p.append(TypeToStringBuilder.typeToString(paramDef.getType()));
				p.append(" ");
				p.append(paramDef.getParameterInfo().getName());
				return p.toString();
			}
		}, ", ");
		builder.append("(").append(text).append(")");
	}

	private static void processGenericParameterList(GenericParamOwner owner, StringBuilder builder)
	{
		List<GenericParamDef> genericParams = owner.getGenericParams();
		if(genericParams.isEmpty())
		{
			return;
		}

		String text = StringUtil.join(genericParams, new Function<GenericParamDef, String>()
		{
			@Override
			public String fun(GenericParamDef genericParamDef)
			{
				return genericParamDef.getName();
			}
		}, ", ");
		builder.append("<").append(text).append(">");
	}

	private static boolean isSet(long value, int mod)
	{
		return (value & mod) == mod;
	}

	private static boolean isSet(long value, int mod, int v)
	{
		return (value & mod) == v;
	}

	@Nullable
	private static StubBlock processNamespace(String namespace)
	{
		if(StringUtil.isEmpty(namespace))
		{
			return null;
		}

		return new StubBlock("namespace " + namespace, null, '{', '}');
	}

	@NotNull
	public String gen()
	{
		assert myRoot != null;

		StringBuilder builder = new StringBuilder();
		builder.append("// Generated by decompiler from Consulo .NET plugin\n\n");

		processBlock(builder, myRoot, 0);

		return builder.toString();
	}

	private static void processBlock(StringBuilder builder, StubBlock root, int index)
	{
		builder.append(StringUtil.repeatSymbol('\t', index));
		builder.append(root.getStartText());
		char[] indents = root.getIndents();

		if(indents.length > 0)
		{
			builder.append('\n');
			builder.append(StringUtil.repeatSymbol('\t', index));
			builder.append(indents[0]);
			builder.append('\n');

			List<StubBlock> blocks = root.getBlocks();
			for(int i = 0; i < blocks.size(); i++)
			{
				if(i != 0)
				{
					builder.append('\n');
				}
				StubBlock stubBlock = blocks.get(i);
				processBlock(builder, stubBlock, index + 1);
			}

			String innerText = root.getInnerText();
			if(innerText != null)
			{
				builder.append(StringUtil.repeatSymbol('\t', index + 1)).append(innerText);
			}

			builder.append(StringUtil.repeatSymbol('\t', index));
			builder.append(indents[1]);
		}
		else
		{
			builder.append(";");
		}
		builder.append('\n');
	}
}
