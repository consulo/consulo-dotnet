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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.LineStubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.util.XByteUtil;
import org.mustbe.consulo.dotnet.dll.vfs.builder.util.XStubModifier;
import org.mustbe.consulo.dotnet.dll.vfs.builder.util.XStubUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiBundle;
import com.intellij.util.ArrayUtil;
import com.intellij.util.BitUtil;
import com.intellij.util.PairFunction;
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
public class XStubBuilder
{
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
			"params",
			"break",
			"continue",
			"lock",
			"explicit",
			"this",
			"abstract",
			"sealed",
			"object",
			"case",
			"finally",
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

	public XStubBuilder(String namespace, List<TypeDef> typeDefs)
	{
		StubBlock namespaceBlock = processNamespace(namespace);
		if(namespaceBlock != null)
		{
			myRoots.add(namespaceBlock);
		}

		for(TypeDef typeDef : typeDefs)
		{
			StubBlock typeBlock = processType(typeDef);
			if(namespaceBlock != null)
			{
				namespaceBlock.getBlocks().addAll(AttributeStubBuilder.processAttributes(typeDef, null, null, null, null));
				namespaceBlock.getBlocks().add(typeBlock);
			}
			else
			{
				myRoots.addAll(AttributeStubBuilder.processAttributes(typeDef, null, null, null, null));
				myRoots.add(typeBlock);
			}
		}
	}

	public XStubBuilder(AssemblyInfo assemblyInfo)
	{
		myRoots.addAll(AttributeStubBuilder.processAttributes(assemblyInfo, null, null, "assembly", null));
	}

	// System.MulticastDelegate
	// Invoke
	@NotNull
	private static StubBlock processType(final TypeDef typeDef)
	{
		String superSuperClassFullName = TypeSignatureStubBuilder.toStringFromDefRefSpec(typeDef.getSuperClass(), typeDef, null);
		if(Comparing.equal(DotNetTypes.System_MulticastDelegate, superSuperClassFullName))
		{
			for(MethodDef methodDef : typeDef.getMethods())
			{
				if("Invoke".equals(methodDef.getName()))
				{
					MethodDef newMethodDef = new MethodDef(XStubUtil.getUserTypeDefName(typeDef), methodDef.getImplFlags(), methodDef.getFlags(),
							methodDef.getSignature());
					for(GenericParamDef paramDef : typeDef.getGenericParams())
					{
						newMethodDef.addGenericParam(paramDef);
					}
					return processMethod(typeDef, newMethodDef, newMethodDef.getName(), DELEGATE);
				}

			}
			assert true;
		}

		StringBuilder builder = new StringBuilder();
		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.VisibilityMask, TypeAttributes.Public))
		{
			builder.append("public ");
		}
		else
		{
			builder.append("internal ");
		}

		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.Sealed))
		{
			builder.append("sealed ");
		}

		if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.Abstract))
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
		else if(XStubUtil.isSet(typeDef.getFlags(), TypeAttributes.Interface))
		{
			builder.append("interface ");
		}
		else
		{
			builder.append("class ");
		}
		builder.append(XStubUtil.getUserTypeDefName(typeDef));

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

			join(builder, supers, new PairFunction<StringBuilder, Object, Void>()
			{
				@Override
				public Void fun(StringBuilder builder, Object o)
				{
					if(o instanceof AbstractTypeReference)
					{
						TypeSignatureStubBuilder.toStringFromDefRefSpec(builder, o, typeDef, null);
					}
					else if(o instanceof InterfaceImplementation)
					{
						TypeSignatureStubBuilder.toStringFromDefRefSpec(builder, ((InterfaceImplementation) o).getInterface(), typeDef, null);
					}
					return null;
				}
			}, ", ");
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
				if(XStubUtil.isSet(field.getFlags(), FieldAttributes.Static))
				{
					parent.getBlocks().addAll(AttributeStubBuilder.processAttributes(field, typeDef, null, null, null));

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
							XStubBuilder.LOGGER.error("Wrong byte count: " + defaultValue.length + ": " + typeDef.getFullName() + "." + field
									.getName());
							builder.append("0");
						}
					}
					builder.append(",\n");
					parent.getBlocks().add(new LineStubBlock(builder));
				}
			}
			else
			{
				if(XStubUtil.isInvisibleMember(field.getName()))
				{
					continue;
				}
				StubBlock stubBlock = processField(typeDef, field);
				parent.getBlocks().addAll(AttributeStubBuilder.processAttributes(field, typeDef, null, null, null));
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

			parent.getBlocks().addAll(AttributeStubBuilder.processAttributes(property, typeDef, null, null, null));
			parent.getBlocks().add(stubBlock);
		}

		for(Event event : typeDef.getEvents())
		{
			StubBlock stubBlock = processEvent(typeDef, event);

			parent.getBlocks().addAll(AttributeStubBuilder.processAttributes(event, typeDef, null, null, null));
			parent.getBlocks().add(stubBlock);
		}

		for(MethodDef methodDef : typeDef.getMethods())
		{
			Ref<Boolean> overrideCallback = Ref.create(Boolean.FALSE);
			String name = XStubUtil.cutSuperTypeName(methodDef.getName(), overrideCallback);

			if(XStubUtil.isSet(methodDef.getFlags(), MethodAttributes.SpecialName))
			{
				// dont show properties methods
				if(name.startsWith("get_") ||
						name.startsWith("set_") ||
						name.startsWith("add_") ||
						name.startsWith("remove_") ||
						name.equals(XStubUtil.STATIC_CONSTRUCTOR_NAME))
				{
					continue;
				}
			}

			if(XStubUtil.isInvisibleMember(name))
			{
				continue;
			}

			AttributeStubBuilder.ProcessAttributesCallback callback = new AttributeStubBuilder.ProcessAttributesCallback();

			parent.getBlocks().addAll(AttributeStubBuilder.processAttributes(methodDef, typeDef, methodDef, null, callback));

			int flags = 0;
			if(callback.extension)
			{
				flags = BitUtil.set(flags, EXTENSION, true);
			}
			if(Boolean.TRUE.equals(overrideCallback.get()))
			{
				flags = BitUtil.set(flags, OVERRIDE, true);
			}
			StubBlock stubBlock = processMethod(typeDef, methodDef, name, flags);

			parent.getBlocks().add(stubBlock);
		}
	}

	private static StubBlock processField(TypeDef typeDef, Field field)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getFieldAccess(field).name().toLowerCase());
		builder.append(' ');

		if(XStubUtil.isSet(field.getFlags(), FieldAttributes.Static))
		{
			builder.append("static ");
		}

		TypeSignatureStubBuilder.typeToString(builder, field.getSignature().getType(), typeDef, typeDef);
		builder.append(' ');
		if(ArrayUtil.contains(field.getName(), KEYWORDS))
		{
			builder.append('@');
		}
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
			getterStub = processMethod(typeDef, getter, "get", ACCESSOR);
			List<ParameterSignature> parameters = getter.getSignature().getParameters();
			if(parameters.size() == 1)
			{
				parameterSignature = parameters.get(0);
			}
		}
		MethodDef setter = property.getSetter();
		if(setter != null)
		{
			setterStub = processMethod(typeDef, setter, "set", ACCESSOR);

			if(parameterSignature == null)
			{
				List<ParameterSignature> parameters = setter.getSignature().getParameters();
				if(parameters.size() == 2)
				{
					parameterSignature = parameters.get(1);
				}
			}
		}

		XStubModifier propertyModifier = XStubModifier.INTERNAL;
		boolean isStatic = false;
		if(getter == null && setter != null)
		{
			propertyModifier = getMethodAccess(setter);
			isStatic = BitUtil.isSet(setter.getFlags(), MethodAttributes.Static);
		}
		else if(getter != null && setter == null)
		{
			propertyModifier = getMethodAccess(getter);
			isStatic = BitUtil.isSet(getter.getFlags(), MethodAttributes.Static);
		}
		else
		{
			if(getMethodAccess(setter) == getMethodAccess(getter))
			{
				propertyModifier = getMethodAccess(getter);
			}
		}

		StringBuilder builder = new StringBuilder();

		builder.append(propertyModifier.name().toLowerCase()).append(' ');
		if(isStatic)
		{
			builder.append("static ");
		}
		TypeSignatureStubBuilder.typeToString(builder, property.getSignature().getType(), typeDef, null);
		builder.append(' ');

		if(parameterSignature != null)
		{
			builder.append("this [");
			processParameterText(builder, parameterSignature, typeDef, null, 0, 0);
			builder.append("]");
		}
		else
		{
			builder.append(XStubUtil.cutSuperTypeName(property.getName(), null));
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
			addOnMethodStub = processMethod(typeDef, addOnMethod, "add", ACCESSOR);
		}
		MethodDef removeOnMethod = event.getRemoveOnMethod();
		if(removeOnMethod != null)
		{
			removeOnStub = processMethod(typeDef, removeOnMethod, "remove", ACCESSOR);
		}

		XStubModifier propertyModifier = XStubModifier.INTERNAL;

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

		builder.append(propertyModifier.name().toLowerCase()).append(' ');
		builder.append("event ");
		builder.append(TypeSignatureStubBuilder.toStringFromDefRefSpec(event.getEventType(), typeDef, null));
		builder.append(' ');
		builder.append(XStubUtil.cutSuperTypeName(event.getName(), null));

		StubBlock stubBlock = new StubBlock(builder, null, BRACES);
		ContainerUtil.addIfNotNull(stubBlock.getBlocks(), addOnMethodStub);
		ContainerUtil.addIfNotNull(stubBlock.getBlocks(), removeOnStub);

		return stubBlock;
	}

	private static XStubModifier getFieldAccess(Field methodDef)
	{
		if(XStubUtil.isSet(methodDef.getFlags(), FieldAttributes.FieldAccessMask, FieldAttributes.Public))
		{
			return XStubModifier.PUBLIC;
		}
		else if(XStubUtil.isSet(methodDef.getFlags(), FieldAttributes.FieldAccessMask, FieldAttributes.Private))
		{
			return XStubModifier.PRIVATE;
		}
		else
		{
			return XStubModifier.INTERNAL;
		}
	}

	private static XStubModifier getMethodAccess(MethodDef methodDef)
	{
		if(XStubUtil.isSet(methodDef.getFlags(), MethodAttributes.MemberAccessMask, MethodAttributes.Public))
		{
			return XStubModifier.PUBLIC;
		}
		else if(XStubUtil.isSet(methodDef.getFlags(), MethodAttributes.MemberAccessMask, MethodAttributes.Private))
		{
			return XStubModifier.PRIVATE;
		}
		else
		{
			return XStubModifier.INTERNAL;
		}
	}

	private static int DELEGATE = 1 << 0;
	private static int ACCESSOR = 1 << 1;
	private static int EXTENSION = 1 << 2;
	private static int OVERRIDE = 1 << 3;

	@NotNull
	private static StubBlock processMethod(TypeDef typeDef, MethodDef methodDef, String name, int flags)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(getMethodAccess(methodDef).name().toLowerCase()).append(' ');

		boolean canHaveBody = true;
		if(BitUtil.isSet(flags, DELEGATE))
		{
			builder.append("delegate ");
			canHaveBody = false;
		}
		else
		{
			if(XStubUtil.isSet(methodDef.getFlags(), MethodAttributes.Abstract))
			{
				builder.append("abstract ");
				canHaveBody = false;
			}

			if(XStubUtil.isSet(methodDef.getFlags(), MethodAttributes.Static))
			{
				builder.append("static ");
			}

			if(BitUtil.isSet(flags, OVERRIDE))
			{
				builder.append("override ");
			}

			if(XStubUtil.isSet(methodDef.getFlags(), MethodAttributes.Virtual))
			{
				builder.append("virtual ");
			}
		}

		if(XStubUtil.isSet(methodDef.getFlags(), MethodAttributes.Final))
		{
			//builder.append("final "); //TODO [VISTALL] final  ? maybe sealed ?
		}

		TypeSignature parameterType = null;
		if(name.equals(XStubUtil.CONSTRUCTOR_NAME))
		{
			builder.append(XStubUtil.getUserTypeDefName(typeDef));
		}
		else
		{
			if(!BitUtil.isSet(flags, ACCESSOR))
			{
				boolean operator = false;
				if(XStubUtil.isSet(methodDef.getFlags(), MethodAttributes.SpecialName))
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
					StringBuilder temp = new StringBuilder();
					TypeSignatureStubBuilder.typeToString(temp, methodDef.getSignature().getParameters().get(0).getInnerType(), typeDef, methodDef);
					name = temp.toString();
				}
				else
				{
					TypeSignatureStubBuilder.typeToString(builder, methodDef.getSignature().getReturnType().getInnerType(), typeDef, methodDef);
				}

				builder.append(' ');

				if(operator)
				{
					builder.append("operator ");
				}
			}

			builder.append(name);
		}

		if(!BitUtil.isSet(flags, ACCESSOR))
		{
			// if conversion method
			if(parameterType != null)
			{
				builder.append("(");
				TypeSignatureStubBuilder.typeToString(builder, parameterType, typeDef, methodDef);
				builder.append(" p)");

				if(!methodDef.getGenericParams().isEmpty())
				{
					processGenericConstraintList(typeDef, builder, typeDef, null);
				}
			}
			else
			{
				processGenericParameterList(methodDef, builder);

				processParameterList(typeDef, methodDef, methodDef.getSignature().getParameters(), flags, builder);

				if(!methodDef.getGenericParams().isEmpty())
				{
					processGenericConstraintList(typeDef, builder, typeDef, null);
				}
			}
		}

		builder.append(canHaveBody ? " { /* compiled code */ }" : ";").append("\n");

		return new LineStubBlock(builder);
	}

	private static void processParameterList(
			final TypeDef typeDef,
			final MethodDef methodDef,
			final List<ParameterSignature> owner,
			final int flags,
			StringBuilder builder)
	{
		builder.append("(");
		join(builder, owner, new PairFunction<StringBuilder, ParameterSignature, Void>()
		{
			@Override
			public Void fun(StringBuilder builder, ParameterSignature parameterSignature)
			{
				processParameterText(builder, parameterSignature, typeDef, methodDef, flags, owner.indexOf(parameterSignature));
				return null;
			}
		}, ", ");
		builder.append(")");
	}

	private static void processParameterText(
			StringBuilder p, ParameterSignature parameterSignature, final TypeDef typeDef, final MethodDef methodDef, int flags, int index)
	{
		TypeSignature signature = parameterSignature;
		if(signature.getType() == 0)
		{
			signature = parameterSignature.getInnerType();
		}

		ParameterInfo parameterInfo = parameterSignature.getParameterInfo();
		for(CustomAttribute customAttribute : parameterInfo.getCustomAttributes())
		{
			String fullName = customAttribute.getConstructor().getParent().getFullName();
			if(Comparing.equal(fullName, "System.ParamArrayAttribute"))
			{
				p.append("params ");
			}
		}

		if(index == 0 && BitUtil.isSet(flags, EXTENSION))
		{
			p.append("this ");
		}

		if(BitUtil.isSet(parameterInfo.getFlags(), ParamAttributes.Out))
		{
			p.append("out ");

			signature = parameterSignature.getInnerType();
		}

		TypeSignatureStubBuilder.typeToString(p, signature, typeDef, methodDef);
		p.append(' ');
		if(ArrayUtil.contains(parameterInfo.getName(), KEYWORDS))
		{
			p.append('@');
		}
		p.append(parameterInfo.getName());

		if(BitUtil.isSet(parameterInfo.getFlags(), ParamAttributes.HasDefault))
		{
			p.append(" = ");
			p.append(toValue(signature, typeDef, methodDef, parameterInfo.getDefaultValue()));
		}
	}

	private static Object toValue(TypeSignature signature, TypeDef typeDef, MethodDef methodDef, byte[] value)
	{
		if(signature == TypeSignature.STRING)
		{
			try
			{
				return StringUtil.QUOTER.fun(XStubUtil.convertTo(new String(value, "UTF-8")));
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
			return XByteUtil.getShort(value);
		}
		else if(signature == TypeSignature.I4)
		{
			return XByteUtil.getInt(value);
		}
		else if(signature == TypeSignature.I8)
		{
			return XByteUtil.getLong(value);
		}
		else if(signature == TypeSignature.U1)
		{
			return value[0] & 0xFF;
		}
		else if(signature == TypeSignature.U2)
		{
			return XByteUtil.getShort(value) & 0xFFFF;
		}
		else if(signature == TypeSignature.U4)
		{
			return XByteUtil.getInt(value) & 0xFFFFFFFFL;
		}
		else if(signature == TypeSignature.U8)
		{
			return new BigInteger(value).toString();
		}
		else if(signature == TypeSignature.CHAR)
		{
			return StringUtil.SINGLE_QUOTER.fun(String.valueOf(XStubUtil.toValidStringSymbol(XByteUtil.getChar(value))));
		}
		else if(signature == TypeSignature.R4)
		{
			return Float.intBitsToFloat(XByteUtil.getInt(value));
		}
		else if(signature == TypeSignature.R8)
		{
			return Double.longBitsToDouble(XByteUtil.getLong(value));
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
						if(XStubUtil.isSet(field.getFlags(), FieldAttributes.Static) &&
								field.getDefaultValue() != null && Arrays.equals(field.getDefaultValue(), value))
						{
							return TypeSignatureStubBuilder.toStringFromDefRefSpec(valueType, typeDef, methodDef) + "." + field.getName();
						}
					}
				}
			}
			return "unsupported";
		}
		else if(signature.getType() == SignatureConstants.ELEMENT_TYPE_GENERIC_INST ||
				signature.getType() == SignatureConstants.ELEMENT_TYPE_CLASS ||
				signature.getType() == SignatureConstants.ELEMENT_TYPE_SZARRAY)
		{
			if(XByteUtil.getInt(value) == 0)
			{
				return "null";
			}
		}

		XStubBuilder.LOGGER.error(signature + " " + typeDef.getFullName() + "#" + (methodDef == null ? null : methodDef.getName()) + "(). Array: " +
				Arrays.toString(value));

		return StringUtil.QUOTER.fun("error");
	}

	private static void processGenericParameterList(GenericParamOwner owner, StringBuilder builder)
	{
		List<GenericParamDef> genericParams = owner.getGenericParams();
		if(genericParams.isEmpty())
		{
			return;
		}

		builder.append('<');
		join(builder, genericParams, new PairFunction<StringBuilder, GenericParamDef, Void>()
		{
			@Override
			public Void fun(StringBuilder builder, GenericParamDef genericParamDef)
			{
				builder.append(genericParamDef.getName());
				return null;
			}
		}, ", ");
		builder.append('>');
	}

	private static void processGenericConstraintList(
			GenericParamOwner owner, StringBuilder builder, final TypeDef typeDef, final MethodDef methodDef)
	{
		List<GenericParamDef> genericParams = owner.getGenericParams();
		if(genericParams.isEmpty())
		{
			return;
		}

		int cap = builder.capacity();

		if(!genericParams.isEmpty())
		{
			builder.append(' ');

			join(builder, genericParams, new PairFunction<StringBuilder, GenericParamDef, Void>()
			{
				@Override
				public Void fun(StringBuilder builder, GenericParamDef genericParamDef)
				{
					List<String> list = processGenericConstraint(genericParamDef, typeDef, methodDef);
					if(list == null)
					{
						return null;
					}

					for(int i = 0; i < list.size(); i++)
					{
						if(i != 0)
						{
							builder.append(' ');
						}
						String s = list.get(i);
						builder.append("where ").append(genericParamDef.getName()).append(" : ").append(s);
					}
					return null;
				}
			}, " ");
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
			String e = TypeSignatureStubBuilder.toStringFromDefRefSpec(o, typeDef, methodDef);
			if(e.equals("System.ValueType"))
			{
				e = "struct";
			}
			list.add(e);
		}
		return list;
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
		repeatSymbol(builder, '\t', index);
		builder.append(root.getStartText());

		if(!(root instanceof LineStubBlock))
		{
			char[] indents = root.getIndents();
			builder.append('\n');
			repeatSymbol(builder, '\t', index);
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
				repeatSymbol(builder, '\t', index + 1);
				builder.append(innerText);
			}

			repeatSymbol(builder, '\t', index);
			builder.append(indents[1]);
			builder.append('\n');
		}
	}

	private static <T> void join(StringBuilder builder, List<T> list, PairFunction<StringBuilder, T, Void> function, String dem)
	{
		for(int i = 0; i < list.size(); i++)
		{
			if(i != 0)
			{
				builder.append(dem);
			}

			T t = list.get(i);
			function.fun(builder, t);
		}
	}

	private static void repeatSymbol(StringBuilder builder, char ch, int count)
	{
		for(int i = 0; i < count; i++)
		{
			builder.append(ch);
		}
	}
}
