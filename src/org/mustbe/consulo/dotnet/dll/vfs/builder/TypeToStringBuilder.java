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

import edu.arizona.cs.mbel.mbel.GenericParamOwner;
import edu.arizona.cs.mbel.signature.ClassTypeSignature;
import edu.arizona.cs.mbel.signature.PointerTypeSignature;
import edu.arizona.cs.mbel.signature.SZArrayTypeSignature;
import edu.arizona.cs.mbel.signature.SignatureConstants;
import edu.arizona.cs.mbel.signature.TypeSignature;
import edu.arizona.cs.mbel.signature.TypeSignatureWithGenericParameters;
import edu.arizona.cs.mbel.signature.ValueTypeSignature;
import edu.arizona.cs.mbel.signature.XGenericTypeSignature;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
public class TypeToStringBuilder implements SignatureConstants
{
	public static String typeToString(TypeSignature signature, GenericParamOwner typeDef, GenericParamOwner memberDef)
	{
		if(signature == null)
		{
			return "void";
		}
		StringBuilder builder = new StringBuilder();
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
				builder.append("sbyte");
				break;
			case ELEMENT_TYPE_U1:
				builder.append("byte");
				break;
			case ELEMENT_TYPE_I2:
				builder.append("short");
				break;
			case ELEMENT_TYPE_U2:
				builder.append("ushort");
				break;
			case ELEMENT_TYPE_I4:
				builder.append("int");
				break;
			case ELEMENT_TYPE_U4:
				builder.append("uint");
				break;
			case ELEMENT_TYPE_I8:
				builder.append("long");
				break;
			case ELEMENT_TYPE_U8:
				builder.append("ulong");
				break;
			case ELEMENT_TYPE_R4:
				builder.append("float");
				break;
			case ELEMENT_TYPE_R8:
				builder.append("double");
				break;
			case ELEMENT_TYPE_STRING:
				builder.append("string");
				break;
			case ELEMENT_TYPE_OBJECT:
				builder.append("object");
				break;
			case ELEMENT_TYPE_PTR:
				PointerTypeSignature pointerTypeSignature = (PointerTypeSignature) signature;
				builder.append(typeToString(pointerTypeSignature.getPointerType(), typeDef, memberDef));
				builder.append("*");
				break;
			case ELEMENT_TYPE_SZARRAY:
				SZArrayTypeSignature szArrayTypeSignature = (SZArrayTypeSignature)signature;
				builder.append(typeToString(szArrayTypeSignature.getElementType(), typeDef, memberDef));
				builder.append("[]");
				break;
			case ELEMENT_TYPE_CLASS:
				ClassTypeSignature typeSignature = (ClassTypeSignature) signature;
				builder.append(StubToStringUtil.getUserTypeDefName(typeSignature.getClassType().getFullName()));
				break;
			case ELEMENT_TYPE_GENERIC_INST:
				TypeSignatureWithGenericParameters mainTypeSignature = (TypeSignatureWithGenericParameters) signature;
				builder.append(typeToString(mainTypeSignature.getSignature(), typeDef, memberDef));
				if(!mainTypeSignature.getGenericArguments().isEmpty())
				{
					builder.append("<");
					for(int i = 0; i < mainTypeSignature.getGenericArguments().size(); i++)
					{
						if(i != 0)
						{
							builder.append(", ");
						}
						builder.append(typeToString(mainTypeSignature.getGenericArguments().get(i), typeDef, memberDef));
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
				assert memberDef != null;
				builder.append(typeDef.getGenericParams().get(methodGenericTypeSignature.getIndex()).getName());
				break;
			case ELEMENT_TYPE_VALUETYPE:
				ValueTypeSignature valueTypeSignature = (ValueTypeSignature) signature;
				builder.append(StubToStringUtil.getUserTypeDefName(valueTypeSignature.getValueType().getFullName()));
				break;
			default:
				builder.append("UNK").append(Integer.toHexString(type).toUpperCase());
				break;
		}
		return builder.toString();
	}
}
