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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.dll.vfs.DotNetFileArchiveEntry;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import edu.arizona.cs.mbel.mbel.AbstractTypeReference;
import edu.arizona.cs.mbel.mbel.Event;
import edu.arizona.cs.mbel.mbel.Field;
import edu.arizona.cs.mbel.mbel.GenericParamDef;
import edu.arizona.cs.mbel.mbel.GenericParamOwner;
import edu.arizona.cs.mbel.mbel.InterfaceImplementation;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.Property;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.signature.FieldAttributes;
import edu.arizona.cs.mbel.signature.MethodAttributes;
import edu.arizona.cs.mbel.signature.ParameterSignature;
import edu.arizona.cs.mbel.signature.TypeAttributes;
import edu.arizona.cs.mbel.signature.TypeSignature;
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
	private static final char[] BRACES = {
			'{',
			'}'
	};

	private static Map<String, String> SPECIAL_METHOD_NAMES = new HashMap<String, String>()
	{
		{
			put("op_Addition", "+");
			put("op_UnaryPlus", "+");
			put("op_Subtraction", "-");
			put("op_UnaryNegation", "-");
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
			put("op_Increment", "++");
			put("op_Decrement", "--");
			put("op_Explicit", "implicit");
			put("op_Implicit", "explicit");
		}
	};

	private static String[] KEYWORDS = new String[] {"event", "params"};

	private static List<String> SKIPPED_SUPERTYPES = new ArrayList<String>()
	{
		{
			add(DotNetTypes.System_Object);
			add(DotNetTypes.System_Enum);
			add(DotNetTypes.System_ValueType);
		}
	};

	private List<StubBlock> myRoots = new SmartList<StubBlock>();

	public StubToStringBuilder(DotNetFileArchiveEntry archiveEntry)
	{
		StubBlock namespaceBlock = processNamespace(archiveEntry.getNamespace());
		if(namespaceBlock != null)
		{
			myRoots.add(namespaceBlock);
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
				myRoots.add(typeBlock);
			}
		}
	}

	// System.MulticastDelegate
	// Invoke
	@NotNull
	private static StubBlock processType(final TypeDef typeDef)
	{
		String superSuperClassFullName = TypeToStringBuilder.toStringFromDefRefSpec(typeDef.getSuperClass(), typeDef, null);
		if(Comparing.equal(DotNetTypes.System_MulticastDelegate, superSuperClassFullName))
		{
			for(MethodDef methodDef : typeDef.getMethods())
			{
				if("Invoke".equals(methodDef.getName()))
				{
					MethodDef newMethodDef = new MethodDef(StubToStringUtil.getUserTypeDefName(typeDef), methodDef.getImplFlags(),
							methodDef.getFlags(), methodDef.getSignature());
					for(GenericParamDef paramDef : typeDef.getGenericParams())
					{
						newMethodDef.addGenericParam(paramDef);
					}
					return processMethod(typeDef, newMethodDef, newMethodDef.getName(), true, false);
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

		val interfaceImplementations = typeDef.getInterfaceImplementations();
		List<Object> supers = new ArrayList<Object>(interfaceImplementations.size() + 1);
		if(superSuperClassFullName != null)
		{
			if(!SKIPPED_SUPERTYPES.contains(superSuperClassFullName))
			{
				supers.add(typeDef.getSuperClass());
			}
		}

		supers.addAll(interfaceImplementations);

		if(!supers.isEmpty())
		{
			builder.append(" : ");

			builder.append(StringUtil.join(supers, new Function<Object, String>()
			{
				@Override
				public String fun(Object o)
				{
					if(o instanceof AbstractTypeReference)
					{
						return TypeToStringBuilder.toStringFromDefRefSpec(o, typeDef, null);
					}
					else if(o instanceof InterfaceImplementation)
					{
						return TypeToStringBuilder.toStringFromDefRefSpec(((InterfaceImplementation) o).getInterface(), typeDef, null);
					}
					return null;
				}
			}, ", "));
		}

		StubBlock stubBlock = new StubBlock(builder.toString(), null, BRACES);
		processMembers(typeDef, stubBlock);
		return stubBlock;
	}

	private static void processMembers(TypeDef typeDef, StubBlock parent)
	{
		boolean isEnum = typeDef.isEnum();
		for(Field field : typeDef.getFields())
		{
			if(isEnum)
			{
				if(isSet(field.getFlags(), FieldAttributes.Static))
				{
					parent.getBlocks().add(new LineStubBlock(field.getName() + ","));
				}
			}
			else
			{
				if(StubToStringUtil.isInvisibleMember(field.getName()))
				{
					continue;
				}
				StubBlock stubBlock = processField(typeDef, field);

				parent.getBlocks().add(stubBlock);
			}
		}

		if(isEnum)
		{
			return;
		}

		for(Property property : typeDef.getProperties())
		{
			StubBlock stubBlock = processProperty(typeDef, property);

			parent.getBlocks().add(stubBlock);
		}

		for(Event event : typeDef.getEvents())
		{
			StubBlock stubBlock = processEvent(typeDef, event);

			parent.getBlocks().add(stubBlock);
		}

		for(MethodDef methodDef : typeDef.getMethods())
		{
			String name = cutSuperName(methodDef.getName());

			if(isSet(methodDef.getFlags(), MethodAttributes.SpecialName))
			{
				// dont show properties methods
				if(name.startsWith("get_") ||
						name.startsWith("set_") ||
						name.startsWith("add_") ||
						name.startsWith("remove_") ||
						name.equals(STATIC_CONSTRUCTOR_NAME))
				{
					continue;
				}
			}

			if(StubToStringUtil.isInvisibleMember(name))
			{
				continue;
			}

			StubBlock stubBlock = processMethod(typeDef, methodDef, name, false, false);

			parent.getBlocks().add(stubBlock);
		}
	}

	private static StubBlock processField(TypeDef typeDef, Field field)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getFieldAccess(field).name().toLowerCase());
		builder.append(" ");

		if(isSet(field.getFlags(), FieldAttributes.Static))
		{
			builder.append("static ");
		}

		builder.append(TypeToStringBuilder.typeToString(field.getSignature().getType(), typeDef, typeDef));
		builder.append(" ");
		builder.append(field.getName());

		return new LineStubBlock(builder.toString() + ";");
	}

	private static StubBlock processProperty(TypeDef typeDef, Property property)
	{
		StubBlock getterStub = null;
		StubBlock setterStub = null;

		MethodDef getter = property.getGetter();
		if(getter != null)
		{
			getterStub = processMethod(typeDef, getter, "get", false, true);
		}
		MethodDef setter = property.getSetter();
		if(setter != null)
		{
			setterStub = processMethod(typeDef, setter, "set", false, true);
		}

		AccessModifier propertyModifier = AccessModifier.INTERNAL;

		if(getter == null && setter != null)
		{
			propertyModifier = getMethodAccess(setter);
		}
		else if(getter != null && setter == null)
		{
			propertyModifier = getMethodAccess(getter);
		}
		else
		{
			if(getMethodAccess(setter) == getMethodAccess(getter))
			{
				propertyModifier = getMethodAccess(getter);
			}
		}

		StringBuilder builder = new StringBuilder();

		builder.append(propertyModifier.name().toLowerCase()).append(" ");
		builder.append(TypeToStringBuilder.typeToString(property.getSignature().getType(), typeDef, null));
		builder.append(" ");
		builder.append(cutSuperName(property.getName()));

		StubBlock stubBlock = new StubBlock(builder.toString(), null, BRACES);
		ContainerUtil.addIfNotNull(stubBlock.getBlocks(), getterStub);
		ContainerUtil.addIfNotNull(stubBlock.getBlocks(), setterStub);

		return stubBlock;
	}

	private static StubBlock processEvent(TypeDef typeDef, Event event)
	{
		StubBlock addOnMethodStub = null;
		StubBlock removeOnStub = null;

		MethodDef addOnMethod = event.getAddOnMethod();
		if(addOnMethod != null)
		{
			addOnMethodStub = processMethod(typeDef, addOnMethod, "add", false, true);
		}
		MethodDef removeOnMethod = event.getRemoveOnMethod();
		if(removeOnMethod != null)
		{
			removeOnStub = processMethod(typeDef, removeOnMethod, "remove", false, true);
		}

		AccessModifier propertyModifier = AccessModifier.INTERNAL;

		if(addOnMethod == null && removeOnMethod != null)
		{
			propertyModifier = getMethodAccess(removeOnMethod);
		}
		else if(addOnMethod != null && removeOnMethod == null)
		{
			propertyModifier = getMethodAccess(addOnMethod);
		}
		else
		{
			if(getMethodAccess(removeOnMethod) == getMethodAccess(addOnMethod))
			{
				propertyModifier = getMethodAccess(addOnMethod);
			}
		}

		StringBuilder builder = new StringBuilder();

		builder.append(propertyModifier.name().toLowerCase()).append(" ");
		builder.append("event ");
		builder.append(TypeToStringBuilder.toStringFromDefRefSpec(event.getEventType(), typeDef, null));
		builder.append(" ");
		builder.append(cutSuperName(event.getName()));

		StubBlock stubBlock = new StubBlock(builder.toString(), null, BRACES);
		ContainerUtil.addIfNotNull(stubBlock.getBlocks(), addOnMethodStub);
		ContainerUtil.addIfNotNull(stubBlock.getBlocks(), removeOnStub);

		return stubBlock;
	}

	private static AccessModifier getFieldAccess(Field methodDef)
	{
		if(isSet(methodDef.getFlags(), FieldAttributes.FieldAccessMask, FieldAttributes.Public))
		{
			return AccessModifier.PUBLIC;
		}
		else if(isSet(methodDef.getFlags(), FieldAttributes.FieldAccessMask, FieldAttributes.Private))
		{
			return AccessModifier.PRIVATE;
		}
		else
		{
			return AccessModifier.INTERNAL;
		}
	}

	private static AccessModifier getMethodAccess(MethodDef methodDef)
	{
		if(isSet(methodDef.getFlags(), MethodAttributes.MemberAccessMask, MethodAttributes.Public))
		{
			return AccessModifier.PUBLIC;
		}
		else if(isSet(methodDef.getFlags(), MethodAttributes.MemberAccessMask, MethodAttributes.Private))
		{
			return AccessModifier.PRIVATE;
		}
		else
		{
			return AccessModifier.INTERNAL;
		}
	}

	@NotNull
	private static StubBlock processMethod(TypeDef typeDef, MethodDef methodDef, String name, boolean delegate, boolean accessor)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(getMethodAccess(methodDef).name().toLowerCase()).append(" ");

		if(delegate)
		{
			builder.append("delegate ");
		}
		else
		{
			if(isSet(methodDef.getFlags(), MethodAttributes.Abstract))
			{
				builder.append("abstract ");
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

		TypeSignature parameterType = null;
		if(name.equals(CONSTRUCTOR_NAME))
		{
			builder.append(StubToStringUtil.getUserTypeDefName(typeDef));
		}
		else
		{
			if(!accessor)
			{
				boolean operator = false;
				if(isSet(methodDef.getFlags(), MethodAttributes.SpecialName))
				{
					if(SPECIAL_METHOD_NAMES.containsKey(name))
					{
						operator = true;
						name = SPECIAL_METHOD_NAMES.get(name);
					}
				}

				if(operator && (name.equals("explicit") || name.equals("implicit")))
				{
					builder.append(name);
					parameterType = methodDef.getSignature().getReturnType().getType();

					// name is first parameter type
					name = TypeToStringBuilder.typeToString(methodDef.getSignature().getParameters()[0].getInnerType(), typeDef, methodDef);
				}
				else
				{
					builder.append(TypeToStringBuilder.typeToString(methodDef.getSignature().getReturnType().getType(), typeDef, methodDef));
				}

				builder.append(" ");

				if(operator)
				{
					builder.append("operator ");
				}
			}

			builder.append(name);
		}

		if(!accessor)
		{
			// if conversion method
			if(parameterType != null)
			{
				builder.append("(");
				builder.append(TypeToStringBuilder.typeToString(parameterType, typeDef, methodDef));
				builder.append(" p)");
			}
			else
			{
				processGenericParameterList(methodDef, builder);

				processParameterList(typeDef, methodDef, methodDef.getSignature().getParameters(), builder);
			}
		}

		return new LineStubBlock(builder.toString() + ";");
	}

	@NotNull
	private static String cutSuperName(@NotNull String name)
	{
		if(name.equals(CONSTRUCTOR_NAME))
		{
			return CONSTRUCTOR_NAME;
		}
		else if(StringUtil.containsChar(name, '.'))
		{
			// method override(implement) from superclass, cut owner of super method
			val dotIndex = name.lastIndexOf('.');
			name = name.substring(dotIndex + 1, name.length());
			return name;
		}
		else
		{
			return name;
		}
	}

	private static void processParameterList(final TypeDef typeDef, final MethodDef methodDef, final ParameterSignature[] owner,
			StringBuilder builder)
	{
		String text = StringUtil.join(owner, new Function<ParameterSignature, String>()
		{
			@Override
			public String fun(ParameterSignature paramDef)
			{
				StringBuilder p = new StringBuilder();
				p.append(TypeToStringBuilder.typeToString(paramDef.getInnerType(), typeDef, methodDef));
				p.append(" ");
				p.append(toValidName(paramDef.getParameterInfo().getName(), ArrayUtil.indexOf(owner, paramDef)));
				return p.toString();
			}
		}, ", ");
		builder.append("(").append(text).append(")");
	}

	/**
	 * Sometimes - parameters name is C# keyword. Bytecode is allow - but C# parser dont. hat why need change name to valid
	 * @param name
	 * @param index
	 * @return
	 */
	private static String toValidName(String name, int index)
	{
		if(ArrayUtil.contains(name, KEYWORDS))
		{
			return name + index;
		}
		else
		{
			return name;
		}
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

		return new StubBlock("namespace " + namespace, null, BRACES);
	}

	@NotNull
	public String gen()
	{
		assert !myRoots.isEmpty();

		StringBuilder builder = new StringBuilder();
		builder.append("// Generated by decompiler from Consulo .NET plugin\n");
		builder.append("// Bodies decompilation is not supported\n\n");

		for(int i = 0; i < myRoots.size(); i++)
		{
			if(i != 0)
			{
				builder.append('\n');
			}
			StubBlock stubBlock = myRoots.get(i);
			processBlock(builder, stubBlock, 0);
		}

		return builder.toString();
	}

	private static void processBlock(StringBuilder builder, StubBlock root, int index)
	{
		builder.append(StringUtil.repeatSymbol('\t', index));
		builder.append(root.getStartText());

		if(!(root instanceof LineStubBlock))
		{
			char[] indents = root.getIndents();
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
		builder.append('\n');
	}
}
