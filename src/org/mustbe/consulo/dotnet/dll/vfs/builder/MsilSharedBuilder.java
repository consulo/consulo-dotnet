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

import org.mustbe.consulo.dotnet.dll.vfs.builder.block.LineStubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.util.XStubUtil;
import com.intellij.util.PairFunction;
import edu.arizona.cs.mbel.mbel.CustomAttribute;
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
	public static final char[] BRACES = {
			'{',
			'}'
	};

	public static void processAttributes(StubBlock parent, CustomAttributeOwner owner)
	{
		for(CustomAttribute customAttribute : owner.getCustomAttributes())
		{
			StringBuilder builder = new StringBuilder();
			builder.append(".custom ");

			MethodDefOrRef constructor = customAttribute.getConstructor();
			builder.append(constructor.getParent().getFullName());
			builder.append(":").append(constructor.getName()).append("\n");

			parent.getBlocks().add(new LineStubBlock(builder));
		}
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
				builder.append("in64");
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
				builder.append("ref ");
				typeToString(builder, ((InnerTypeOwner) signature).getInnerType(), typeDef);
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
				XStubUtil.appendDottedValidName(builder, typeSignature.getClassType().getFullName());
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
				builder.append(typeDef.getGenericParams().get(typeGenericTypeSignature.getIndex()).getName());
				break;
			case ELEMENT_TYPE_MVAR:
				XGenericTypeSignature methodGenericTypeSignature = (XGenericTypeSignature) signature;

				builder.append(methodGenericTypeSignature.getIndex());
				break;
			case ELEMENT_TYPE_VALUETYPE:
				builder.append("valuetype ");
				ValueTypeSignature valueTypeSignature = (ValueTypeSignature) signature;
				XStubUtil.appendDottedValidName(builder, valueTypeSignature.getValueType().getFullName());
				break;
			default:
				builder.append("UNK").append(Integer.toHexString(type).toUpperCase());
				break;
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

	public static void toStringFromDefRefSpec(StringBuilder builder, Object o, TypeDef typeDef)
	{
		if(o instanceof TypeRef)
		{
			XStubUtil.appendTypeRefFullName(builder, ((TypeRef) o));
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
