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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.dll.vfs.DotNetBaseFileArchiveEntry;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiBundle;
import com.intellij.util.ArrayUtil;
import com.intellij.util.BitUtil;
import com.intellij.util.Function;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import edu.arizona.cs.mbel.mbel.*;
import edu.arizona.cs.mbel.signature.*;
import lombok.val;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
@Logger
public class StubToStringBuilder
{
	private static final String CONSTRUCTOR_NAME = ".ctor";
	private static final String STATIC_CONSTRUCTOR_NAME = ".cctor";
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

	private static String[] KEYWORDS = new String[]{
			"event",
			"params"
	};

	private static List<String> SKIPPED_SUPERTYPES = new ArrayList<String>()
	{
		{
			add(DotNetTypes.System_Object);
			add(DotNetTypes.System_Enum);
			add(DotNetTypes.System_ValueType);
		}
	};

	private List<StubBlock> myRoots = new SmartList<StubBlock>();

	public StubToStringBuilder(DotNetBaseFileArchiveEntry archiveEntry)
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
				namespaceBlock.getBlocks().addAll(processAttributes(typeDef, null, null, null));
				namespaceBlock.getBlocks().add(typeBlock);
			}
			else
			{
				myRoots.addAll(processAttributes(typeDef, null, null, null));
				myRoots.add(typeBlock);
			}
		}
	}

	public StubToStringBuilder(AssemblyInfo assemblyInfo)
	{
		myRoots.addAll(processAttributes(assemblyInfo, null, null, "assembly"));
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

		processGenericConstraintList(typeDef, builder, typeDef, null);

		StubBlock stubBlock = new StubBlock(builder, null, BRACES);
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
					parent.getBlocks().addAll(processAttributes(field, typeDef, null, null));

					StringBuilder builder = new StringBuilder();
					builder.append(field.getName());
					byte[] defaultValue = field.getDefaultValue();
					if(defaultValue != null)
					{
						builder.append(" = ");
						//TODO [VISTALL] find better way to handle type
						if(defaultValue.length == 1)
						{
							builder.append(toValue(TypeSignature.I1, typeDef, null, defaultValue));
						}
						else if(defaultValue.length == 2)
						{
							builder.append(toValue(TypeSignature.I2, typeDef, null, defaultValue));
						}
						else if(defaultValue.length == 4)
						{
							builder.append(toValue(TypeSignature.I4, typeDef, null, defaultValue));
						}
						else if(defaultValue.length == 8)
						{
							builder.append(toValue(TypeSignature.I8, typeDef, null, defaultValue));
						}
						else
						{
							LOGGER.error("Wrong byte count: " + defaultValue.length + ": " + typeDef.getFullName() + "." + field.getName());
							builder.append("0");
						}
					}
					builder.append(",\n");
					parent.getBlocks().add(new LineStubBlock(builder));
				}
			}
			else
			{
				if(StubToStringUtil.isInvisibleMember(field.getName()))
				{
					continue;
				}
				StubBlock stubBlock = processField(typeDef, field);
				parent.getBlocks().addAll(processAttributes(field, typeDef, null, null));
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

			parent.getBlocks().addAll(processAttributes(property, typeDef, null, null));
			parent.getBlocks().add(stubBlock);
		}

		for(Event event : typeDef.getEvents())
		{
			StubBlock stubBlock = processEvent(typeDef, event);

			parent.getBlocks().addAll(processAttributes(event, typeDef, null, null));
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

			parent.getBlocks().addAll(processAttributes(methodDef, typeDef, methodDef, null));

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

		byte[] defaultValue = field.getDefaultValue();
		if(defaultValue != null)
		{
			builder.append(" = ");
			builder.append(toValue(field.getSignature().getType(), typeDef, null, defaultValue));
		}
		builder.append(";\n");

		return new LineStubBlock(builder);
	}

	private static StubBlock processProperty(TypeDef typeDef, Property property)
	{
		StubBlock getterStub = null;
		StubBlock setterStub = null;

		ParameterSignature parameterSignature = null;
		MethodDef getter = property.getGetter();
		if(getter != null)
		{
			getterStub = processMethod(typeDef, getter, "get", false, true);
			ParameterSignature[] parameters = getter.getSignature().getParameters();
			if(parameters.length == 1)
			{
				parameterSignature = parameters[0];
			}
		}
		MethodDef setter = property.getSetter();
		if(setter != null)
		{
			setterStub = processMethod(typeDef, setter, "set", false, true);

			if(parameterSignature == null)
			{
				ParameterSignature[] parameters = setter.getSignature().getParameters();
				if(parameters.length == 2)
				{
					parameterSignature = parameters[1];
				}
			}
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

		if(parameterSignature != null)
		{
			builder.append("this [");
			builder.append(getParameterText(parameterSignature, typeDef, null, 0));
			builder.append("]");
		}
		else
		{
			builder.append(cutSuperName(property.getName()));
		}

		StubBlock stubBlock = new StubBlock(builder, null, BRACES);
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

		StubBlock stubBlock = new StubBlock(builder, null, BRACES);
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

		boolean canHaveBody = true;
		if(delegate)
		{
			builder.append("delegate ");
			canHaveBody = false;
		}
		else
		{
			if(isSet(methodDef.getFlags(), MethodAttributes.Abstract))
			{
				builder.append("abstract ");
				canHaveBody = false;
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
					parameterType = methodDef.getSignature().getReturnType().getInnerType();

					// name is first parameter type
					name = TypeToStringBuilder.typeToString(methodDef.getSignature().getParameters()[0].getInnerType(), typeDef, methodDef);
				}
				else
				{
					builder.append(TypeToStringBuilder.typeToString(methodDef.getSignature().getReturnType().getInnerType(), typeDef, methodDef));
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

				if(!methodDef.getGenericParams().isEmpty())
				{
					processGenericConstraintList(typeDef, builder, typeDef, null);
				}
			}
			else
			{
				processGenericParameterList(methodDef, builder);

				processParameterList(typeDef, methodDef, methodDef.getSignature().getParameters(), builder);

				if(!methodDef.getGenericParams().isEmpty())
				{
					processGenericConstraintList(typeDef, builder, typeDef, null);
				}
			}
		}

		builder.append(canHaveBody ? " { /* compiled code */ }" : ";").append("\n");

		return new LineStubBlock(builder);
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
			public String fun(ParameterSignature parameterSignature)
			{
				return getParameterText(parameterSignature, typeDef, methodDef, ArrayUtil.indexOf(owner, parameterSignature));
			}
		}, ", ");
		builder.append("(").append(text).append(")");
	}

	private static String getParameterText(ParameterSignature parameterSignature, final TypeDef typeDef, final MethodDef methodDef, int index)
	{
		TypeSignature signature = parameterSignature;
		if(signature.getType() == 0)
		{
			signature = parameterSignature.getInnerType();
		}

		StringBuilder p = new StringBuilder();
		ParameterInfo parameterInfo = parameterSignature.getParameterInfo();
		for(CustomAttribute customAttribute : parameterInfo.getCustomAttributes())
		{
			String fullName = customAttribute.getConstructor().getParent().getFullName();
			if(Comparing.equal(fullName, "System.ParamArrayAttribute"))
			{
				p.append("params ");
			}
		}

		if(BitUtil.isSet(parameterInfo.getFlags(), ParamAttributes.Out))
		{
			p.append("out ");

			signature = parameterSignature.getInnerType();
		}

		p.append(TypeToStringBuilder.typeToString(signature, typeDef, methodDef));
		p.append(" ");
		p.append(toValidName(parameterInfo.getName()));

		if(BitUtil.isSet(parameterInfo.getFlags(), ParamAttributes.HasDefault))
		{
			p.append(" = ");
			p.append(toValue(signature, typeDef, methodDef, parameterInfo.getDefaultValue()));
		}
		return p.toString();
	}

	private static Object toValue(TypeSignature signature, TypeDef typeDef, MethodDef methodDef, byte[] value)
	{
		if(signature == TypeSignature.STRING)
		{
			try
			{
				return StringUtil.QUOTER.fun(new String(value, "UTF-8"));
			}
			catch(UnsupportedEncodingException e)
			{
				return StringUtil.QUOTER.fun(Arrays.toString(value));
			}
		}
		else if(signature == TypeSignature.BOOLEAN)
		{
			return value[0] == 1;
		}
		else if(signature == TypeSignature.I1)
		{
			return value[0];
		}
		else if(signature == TypeSignature.I2)
		{
			return wrap(value).getShort();
		}
		else if(signature == TypeSignature.I4)
		{
			return wrap(value).getInt();
		}
		else if(signature == TypeSignature.I8)
		{
			return wrap(value).getLong();
		}
		else if(signature == TypeSignature.U1)
		{
			return value[0] & 0xFF;
		}
		else if(signature == TypeSignature.U2)
		{
			return wrap(value).getShort() & 0xFFFF;
		}
		else if(signature == TypeSignature.U4)
		{
			return wrap(value).getInt() & 0xFFFFFFFFL;
		}
		else if(signature == TypeSignature.U8)
		{
			return new BigInteger(value).toString();
		}
		else if(signature == TypeSignature.CHAR)
		{
			return StringUtil.SINGLE_QUOTER.fun(String.valueOf(wrap(value).getChar()));
		}
		else if(signature == TypeSignature.R4)
		{
			return wrap(value).getFloat();
		}
		else if(signature == TypeSignature.R8)
		{
			return wrap(value).getDouble();
		}
		else if(signature.getType() == SignatureConstants.ELEMENT_TYPE_VALUETYPE)
		{
			ValueTypeSignature valueTypeSignature = (ValueTypeSignature) signature;
			AbstractTypeReference valueType = valueTypeSignature.getValueType();
			if(valueType instanceof TypeDef)
			{
				if(((TypeDef) valueType).isEnum())
				{
					for(Field field : ((TypeDef) valueType).getFields())
					{
						if(isSet(field.getFlags(), FieldAttributes.Static) &&
								field.getDefaultValue() != null && Arrays.equals(field.getDefaultValue(), value))
						{
							return TypeToStringBuilder.toStringFromDefRefSpec(valueType, typeDef, methodDef) + "." + field.getName();
						}
					}
				}
			}
			return "valueType" + valueType.getClass();
		}
		else if(signature.getType() == SignatureConstants.ELEMENT_TYPE_GENERIC_INST ||
				signature.getType() == SignatureConstants.ELEMENT_TYPE_CLASS ||
				signature.getType() == SignatureConstants.ELEMENT_TYPE_SZARRAY)
		{
			if(wrap(value).getInt() == 0)
			{
				return "null";
			}
		}

		LOGGER.error(signature + " " + typeDef.getFullName() + "#" + (methodDef == null ? null : methodDef.getName()) + "(). Array: " + Arrays
				.toString
				(value));

		return StringUtil.QUOTER.fun("error");
	}

	private static ByteBuffer wrap(byte[] data)
	{
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		return byteBuffer;
	}

	/**
	 * Sometimes - parameters name is C# keyword. Bytecode is allow - but C# parser dont. what why need change name to valid
	 */
	private static String toValidName(String name)
	{
		return ArrayUtil.contains(name, KEYWORDS) ? "@" + name : name;
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

	private static void processGenericConstraintList(GenericParamOwner owner, StringBuilder builder, final TypeDef typeDef,
			final MethodDef methodDef)
	{
		List<GenericParamDef> genericParams = owner.getGenericParams();
		if(genericParams.isEmpty())
		{
			return;
		}

		String join = StringUtil.join(genericParams, new Function<GenericParamDef, String>()
		{
			@Override
			public String fun(GenericParamDef genericParamDef)
			{
				List<String> list = processGenericConstraint(genericParamDef, typeDef, methodDef);
				if(list == null)
				{
					return "";
				}
				StringBuilder b = new StringBuilder();
				for(int i = 0; i < list.size(); i++)
				{
					if(i != 0)
					{
						b.append(" ");
					}
					String s = list.get(i);
					b.append("where ").append(genericParamDef.getName()).append(" : ").append(s);
				}
				return b.toString();
			}
		}, " ");

		if(!join.isEmpty())
		{
			builder.append(" ").append(join);
		}
	}

	private static List<String> processGenericConstraint(GenericParamDef paramDef, TypeDef typeDef, MethodDef methodDef)
	{
		List<String> list = new SmartList<String>();
		if(BitUtil.isSet(paramDef.getFlags(), GenericParamAttributes.DefaultConstructorConstraint))
		{
			list.add("new()");
		}

		for(Object o : paramDef.getConstraints())
		{
			String e = TypeToStringBuilder.toStringFromDefRefSpec(o, typeDef, methodDef);
			if(e.equals("System.ValueType"))
			{
				e = "struct";
			}
			list.add(e);
		}
		return list;
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

	private static Object[][] ourMethodImplAttributes = {
			{
					MethodImplAttributes.ManagedMask,
					MethodImplAttributes.Unmanaged,
					"Unmanaged"
			},
			{
					MethodImplAttributes.ForwardRef,
					MethodImplAttributes.ForwardRef,
					"ForwardRef"
			},
			{
					MethodImplAttributes.PreserveSig,
					MethodImplAttributes.PreserveSig,
					"PreserveSig"
			},
			{
					MethodImplAttributes.InternalCall,
					MethodImplAttributes.InternalCall,
					"InternalCall"
			},
			{
					MethodImplAttributes.Synchronized,
					MethodImplAttributes.Synchronized,
					"Synchronized"
			},
			{
					MethodImplAttributes.NoInlining,
					MethodImplAttributes.NoInlining,
					"NoInlining"
			},
			{
					MethodImplAttributes.NoOptimization,
					MethodImplAttributes.NoOptimization,
					"NoOptimization"
			},
			{
					MethodImplAttributes.MaxMethodImplVal,
					MethodImplAttributes.MaxMethodImplVal,
					"NoOptimization"
			},
	};

	private static List<LineStubBlock> processAttributes(CustomAttributeOwner owner, TypeDef typeDef, MethodDef methodDef, String forceTarget)
	{
		CustomAttribute[] customAttributes = owner.getCustomAttributes();
		if(customAttributes.length == 0)
		{
			return Collections.emptyList();
		}

		val list = new ArrayList<LineStubBlock>();
		for(CustomAttribute customAttribute : customAttributes)
		{
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			if(forceTarget != null)
			{
				builder.append(forceTarget);
				builder.append(": ");
			}
			MethodDefOrRef constructor = customAttribute.getConstructor();
			String type = TypeToStringBuilder.toStringFromDefRefSpec(constructor.getParent(), null, null);
			if(type.endsWith("Attribute"))
			{
				type = type.substring(0, type.length() - 9);
			}
			builder.append(type);
			String value = processAttributeValue(customAttribute, typeDef, methodDef);
			if(!value.isEmpty())
			{
				builder.append("(");
				builder.append(value);
				builder.append(")");
			}
			builder.append("]");

			list.add(new LineStubBlock(builder));
		}

		if(owner instanceof TypeDef)
		{
			if((((TypeDef) owner).getFlags() & TypeAttributes.Serializable) == TypeAttributes.Serializable)
			{
				list.add(new LineStubBlock("[System.Serializable]"));
			}
		}
		else if(owner instanceof MethodDef)
		{
			List<String> attributeValues = new ArrayList<String>();
			int implFlags = ((MethodDef) owner).getImplFlags();
			for(Object[] methodImplAttribute : ourMethodImplAttributes)
			{
				int mask = (Integer) methodImplAttribute[0];
				int value = (Integer) methodImplAttribute[1];
				String field = (String) methodImplAttribute[2];

				if((implFlags & mask) == value)
				{
					attributeValues.add(field);
				}
			}

			if(!attributeValues.isEmpty())
			{
				String val = StringUtil.join(attributeValues, new Function<String, String>()
				{
					@Override
					public String fun(String s)
					{
						return "System.Reflection.MethodImplAttributes." + s;
					}
				}, " | ");
				list.add(new LineStubBlock("[System.Runtime.CompilerServices.MethodImpl(" + val + ")]"));
			}
		}

		return list;
	}

	private static String processAttributeValue(@NotNull CustomAttribute customAttribute, TypeDef typeDef, MethodDef methodDef)
	{
		byte[] signature = customAttribute.getSignature();
		if(signature.length == 0)
		{
			return "";
		}
		edu.arizona.cs.mbel.ByteBuffer byteBuffer = new edu.arizona.cs.mbel.ByteBuffer(signature);
		if(byteBuffer.getShort() != 1)
		{
			throw new IllegalArgumentException("Not one");
		}

		ParameterSignature[] parameterSignatures;
		MethodDefOrRef constructor = customAttribute.getConstructor();
		if(constructor instanceof MethodDef)
		{
			parameterSignatures = ((MethodDef) constructor).getSignature().getParameters();
		}
		else if(constructor instanceof MethodRef)
		{
			parameterSignatures = ((MethodRef) constructor).getCallsiteSignature().getParameters();
		}
		else
		{
			throw new IllegalArgumentException(constructor.getClass().getName());
		}

		List<String> appender = new ArrayList<String>();
		for(ParameterSignature parameterSignature : parameterSignatures)
		{
			TypeSignature innerType = parameterSignature.getInnerType();
			assert innerType != null;

			appender.add(getValueOfAttributeFromBlob(typeDef, methodDef, byteBuffer, innerType));
		}

		if(byteBuffer.canRead())
		{
			int named = byteBuffer.getShort();
			if(named != 0)
			{
				/*if(named < Byte.MAX_VALUE)
				{
					System.out.println("error");
					named = 0;
				}
				for(int i = 0; i < named; i++)
				{
					int kind = byteBuffer.get();
					TypeSignature typeSignature = TypeSignatureParser.parse(byteBuffer, null);
					String name = getUtf8(byteBuffer);
					System.out.println(name);
				}  */
				/*
				var kind = ReadByte ();
			var type = ReadCustomAttributeFieldOrPropType ();
			var name = ReadUTF8String ();

			Collection<CustomAttributeNamedArgument> container;
			switch (kind) {
			case 0x53:
				container = GetCustomAttributeNamedArgumentCollection (ref fields);
				break;
			case 0x54:
				container = GetCustomAttributeNamedArgumentCollection (ref properties);
				break;
			default:
				throw new NotSupportedException ();
			} */
			}
		}
		return StringUtil.join(appender, ", ");
	}

	private static String getValueOfAttributeFromBlob(TypeDef typeDef, MethodDef methodDef, edu.arizona.cs.mbel.ByteBuffer byteBuffer,
			TypeSignature innerType)
	{
		if(innerType.getType() == SignatureConstants.ELEMENT_TYPE_SZARRAY)
		{
			return "arrayError";
		}
		else if(innerType.getType() == SignatureConstants.ELEMENT_TYPE_BOOLEAN)
		{
			return String.valueOf(byteBuffer.get() == 1);
		}
		else if(innerType.getType() == SignatureConstants.ELEMENT_TYPE_I4)
		{
			return String.valueOf(byteBuffer.getInt());
		}
		else if(innerType.getType() == SignatureConstants.ELEMENT_TYPE_U4)
		{
			return String.valueOf(byteBuffer.getDWORD());
		}
		else if(innerType.getType() == SignatureConstants.ELEMENT_TYPE_VALUETYPE)
		{
			int valueIndex = byteBuffer.getInt();

			ValueTypeSignature valueTypeSignature = (ValueTypeSignature) innerType;
			AbstractTypeReference valueType = valueTypeSignature.getValueType();
			if(valueType instanceof TypeDef)
			{
				if(((TypeDef) valueType).isEnum())
				{
					for(Field field : ((TypeDef) valueType).getFields())
					{
						if(isSet(field.getFlags(), FieldAttributes.Static) &&
								field.getDefaultValue() != null && wrap(field.getDefaultValue()).getInt() == valueIndex)
						{
							return TypeToStringBuilder.toStringFromDefRefSpec(valueType, typeDef, methodDef) + "." + field.getName();
						}
					}
				}
			}

			return "errorELEMENT_TYPE_VALUETYPE";
		}
		else if(innerType.getType() == SignatureConstants.ELEMENT_TYPE_STRING)
		{
			return "@" + StringUtil.QUOTER.fun(getUtf8(byteBuffer));
		}
		else
		{
			return "unknown_type_" + innerType.getType();
		}
	}

	private static String getUtf8(edu.arizona.cs.mbel.ByteBuffer byteBuffer)
	{
		byte b = byteBuffer.get();
		if(b == 0xFF)
		{
			return "";
		}
		else
		{
			byteBuffer.back();
			int size = Signature.readCodedInteger(byteBuffer);
			if(size == 0)
			{
				return "";
			}
			else
			{
				try
				{
					return new String(byteBuffer.get(size), "UTF-8");
				}
				catch(UnsupportedEncodingException e)
				{
					return "UnsupportedEncodingException:string";
				}
			}
		}
	}

	@NotNull
	public CharSequence gen()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(PsiBundle.message("psi.decompiled.text.header")).append('\n').append('\n');

		for(int i = 0; i < myRoots.size(); i++)
		{
			if(i != 0)
			{
				builder.append('\n');
			}
			StubBlock stubBlock = myRoots.get(i);
			processBlock(builder, stubBlock, 0);
		}

		return builder;
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

			CharSequence innerText = root.getInnerText();
			if(innerText != null)
			{
				builder.append(StringUtil.repeatSymbol('\t', index + 1)).append(innerText);
			}

			builder.append(StringUtil.repeatSymbol('\t', index));
			builder.append(indents[1]);
			builder.append("\n");
		}
	}
}
