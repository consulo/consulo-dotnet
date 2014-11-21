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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.LineStubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.PairFunction;
import edu.arizona.cs.mbel.mbel.CustomAttribute;
import edu.arizona.cs.mbel.mbel.GenericParamDef;
import edu.arizona.cs.mbel.mbel.GenericParamOwner;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.MethodDefOrRef;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.mbel.TypeRef;
import edu.arizona.cs.mbel.mbel.TypeSpec;
import edu.arizona.cs.mbel.signature.*;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilSharedBuilder implements SignatureConstants
{
	private static final String[] KEYWORDS;

	static
	{
		Set<String> set = new HashSet<String>();
		for(IElementType keyword : MsilTokenSets.KEYWORDS.getTypes())
		{
			set.add(keyword.toString().replace("_KEYWORD", "").toLowerCase());
		}
		KEYWORDS = ArrayUtil.toStringArray(set);
	}

	private static final char[] INVALID_CHARS = {'<', '/', '>'};

	public static void appendValidName(StringBuilder builder, String name)
	{
		if(!isValidName(name))
		{
			builder.append('\'');
			builder.append(name);
			builder.append('\'');
		}
		else
		{
			builder.append(name);
		}
	}

	private static boolean isValidName(String name)
	{
		if(name.length() > 0 && Character.isDigit(name.charAt(0)))
		{
			return false;
		}

		for(char invalidChar : INVALID_CHARS)
		{
			if(name.indexOf(invalidChar) >= 0)
			{
				return false;
			}
		}

		for(String s : KEYWORDS)
		{
			if(name.equals(s))
			{
				return false;
			}
		}
		return true;
	}

	public static void processAttributes(StubBlock parent, CustomAttributeOwner owner)
	{
		for(CustomAttribute customAttribute : owner.getCustomAttributes())
		{
			StringBuilder builder = new StringBuilder();
			builder.append(".custom ");

			MethodDefOrRef constructor = customAttribute.getConstructor();
			toStringFromDefRefSpec(builder, constructor.getParent(), null);
			builder.append("::").append(constructor.getName()).append("() = ()\n");

			parent.getBlocks().add(new LineStubBlock(builder));
		}
	}

	protected static void appendAccessor(String name, final TypeDef typeDef, MethodDef methodDef, StubBlock block)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(" ");

		typeToString(builder, methodDef.getSignature().getReturnType().getInnerType(), typeDef);

		builder.append(" ");

		toStringFromDefRefSpec(builder, methodDef.getParent(), typeDef);

		builder.append("::");

		appendValidName(builder, methodDef.getName());

		builder.append("(");
		join(builder, methodDef.getSignature().getParameters(), new PairFunction<StringBuilder, ParameterSignature, Void>()
		{
			@Nullable
			@Override
			public Void fun(StringBuilder t, ParameterSignature v)
			{
				typeToString(t, v.getInnerType(), typeDef);
				return null;
			}
		}, ", ");
		builder.append(")\n");

		block.getBlocks().add(new LineStubBlock(builder));
	}

	protected static void processGeneric(StringBuilder builder, GenericParamOwner paramOwner)
	{
		List<GenericParamDef> genericParams = paramOwner.getGenericParams();
		if(genericParams.isEmpty())
		{
			return;
		}

		builder.append("<");
		join(builder, genericParams, new PairFunction<StringBuilder, GenericParamDef, Void>()
		{
			@Nullable
			@Override
			public Void fun(StringBuilder t, GenericParamDef v)
			{
				int flags = v.getFlags();
				int varianceMask = flags & GenericParamAttributes.VarianceMask;
				switch(varianceMask)
				{
					case GenericParamAttributes.Covariant:
						t.append("+");
						break;
					case GenericParamAttributes.Contravariant:
						t.append("-");
						break;
				}
				t.append(v.getName());
				return null;
			}
		}, ", ");
		builder.append(">");
	}

	public static void typeToString(StringBuilder builder, TypeSignature signature, TypeDef typeDef)
	{
		if(signature == null)
		{
			builder.append("void");
			return;
		}
		byte type = signature.getType();
		switch(type)
		{
			case ELEMENT_TYPE_BOOLEAN:
				builder.append("bool");
				break;
			case ELEMENT_TYPE_VOID:
				builder.append("void");
				break;
			case ELEMENT_TYPE_CHAR:
				builder.append("char");
				break;
			case ELEMENT_TYPE_I1:
				builder.append("int8");
				break;
			case ELEMENT_TYPE_U1:
				builder.append("uint8");
				break;
			case ELEMENT_TYPE_I2:
				builder.append("int16");
				break;
			case ELEMENT_TYPE_U2:
				builder.append("uint16");
				break;
			case ELEMENT_TYPE_I4:
				builder.append("int32");
				break;
			case ELEMENT_TYPE_U4:
				builder.append("uint32");
				break;
			case ELEMENT_TYPE_I8:
				builder.append("int64");
				break;
			case ELEMENT_TYPE_U8:
				builder.append("uint64");
				break;
			case ELEMENT_TYPE_R4:
				builder.append("float");
				break;
			case ELEMENT_TYPE_R8:
				builder.append("float64");
				break;
			case ELEMENT_TYPE_STRING:
				builder.append("string");
				break;
			case ELEMENT_TYPE_OBJECT:
				builder.append("object");
				break;
			case ELEMENT_TYPE_I:
				builder.append("int");
				break;
			case ELEMENT_TYPE_U:
				builder.append("uint");
				break;
			case ELEMENT_TYPE_BYREF:
				typeToString(builder, ((InnerTypeOwner) signature).getInnerType(), typeDef);
				builder.append("&");
				break;
			case ELEMENT_TYPE_TYPEDBYREF:
				builder.append("valuetype ").append(DotNetTypes.System.TypedReference);
				break;
			case ELEMENT_TYPE_PTR:
				PointerTypeSignature pointerTypeSignature = (PointerTypeSignature) signature;
				typeToString(builder, pointerTypeSignature.getPointerType(), typeDef);
				builder.append("*");
				break;
			case ELEMENT_TYPE_SZARRAY:
				SZArrayTypeSignature szArrayTypeSignature = (SZArrayTypeSignature) signature;
				typeToString(builder, szArrayTypeSignature.getElementType(), typeDef);
				builder.append("[]");
				break;
			case ELEMENT_TYPE_CLASS:
				ClassTypeSignature typeSignature = (ClassTypeSignature) signature;
				builder.append("class ");
				toStringFromDefRefSpec(builder, typeSignature.getClassType(), typeDef);
				break;
			case ELEMENT_TYPE_GENERIC_INST:
				TypeSignatureWithGenericParameters mainTypeSignature = (TypeSignatureWithGenericParameters) signature;
				typeToString(builder, mainTypeSignature.getSignature(), typeDef);
				if(!mainTypeSignature.getGenericArguments().isEmpty())
				{
					builder.append("<");
					for(int i = 0; i < mainTypeSignature.getGenericArguments().size(); i++)
					{
						if(i != 0)
						{
							builder.append(", ");
						}
						typeToString(builder, mainTypeSignature.getGenericArguments().get(i), typeDef);
					}
					builder.append(">");
				}
				break;
			case ELEMENT_TYPE_VAR:
				XGenericTypeSignature typeGenericTypeSignature = (XGenericTypeSignature) signature;
				assert typeDef != null;
				builder.append("!");
				builder.append(typeDef.getGenericParams().get(typeGenericTypeSignature.getIndex()).getName());
				break;
			case ELEMENT_TYPE_MVAR:
				XGenericTypeSignature methodGenericTypeSignature = (XGenericTypeSignature) signature;

				builder.append(methodGenericTypeSignature.getIndex());
				break;
			case ELEMENT_TYPE_VALUETYPE:
				builder.append("valuetype ");
				ValueTypeSignature valueTypeSignature = (ValueTypeSignature) signature;
				toStringFromDefRefSpec(builder, valueTypeSignature.getValueType(), typeDef);
				break;
			default:
				builder.append("UNK").append(Integer.toHexString(type).toUpperCase());
				break;
		}
	}

	public static void appendTypeRefFullName(@NotNull StringBuilder builder, String namespace, @NotNull String name)
	{
		if(!StringUtil.isEmpty(namespace))
		{
			appendValidName(builder, namespace + "." + name);
		}
		else
		{
			appendValidName(builder, name);
		}
	}

	public static <T> void join(StringBuilder builder, List<T> list, PairFunction<StringBuilder, T, Void> function, String dem)
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

	public static void toStringFromDefRefSpec(@NotNull StringBuilder builder, @NotNull Object o, @Nullable TypeDef typeDef)
	{
		if(o instanceof TypeDef)
		{
			TypeDef parent = ((TypeDef) o).getParent();
			if(parent != null)
			{
				appendTypeRefFullName(builder, parent.getNamespace(), parent.getName());
				builder.append("/");
				appendValidName(builder, ((TypeDef) o).getName());
			}
			else
			{
				appendTypeRefFullName(builder, ((TypeRef) o).getNamespace(), ((TypeRef) o).getName());
			}
		}
		else if(o instanceof TypeRef)
		{
			appendTypeRefFullName(builder, ((TypeRef) o).getNamespace(), ((TypeRef) o).getName());
		}
		else if(o instanceof TypeSpec)
		{
			typeToString(builder, ((TypeSpec) o).getSignature(), typeDef);
		}
		else
		{
			throw new IllegalArgumentException(o.toString());
		}
	}
}
