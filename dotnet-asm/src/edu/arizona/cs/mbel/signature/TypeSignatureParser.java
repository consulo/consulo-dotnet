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

package edu.arizona.cs.mbel.signature;

import java.util.ArrayList;
import java.util.List;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.mbel.TypeGroup;

/**
 * @author VISTALL
 * @since 09.01.14
 */
public class TypeSignatureParser implements SignatureConstants
{
	public static TypeSignature parse(ByteBuffer buffer, TypeGroup group)
	{
		byte data = buffer.peek();

		switch(data)
		{
			case ELEMENT_TYPE_END:
				return null;
			case ELEMENT_TYPE_VALUETYPE:
				return ValueTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_CLASS:
				return ClassTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_BOOLEAN:
				buffer.get();
				return TypeSignature.BOOLEAN;
			case ELEMENT_TYPE_CHAR:
				buffer.get();
				return TypeSignature.CHAR;
			case ELEMENT_TYPE_I1:
				buffer.get();
				return TypeSignature.I1;
			case ELEMENT_TYPE_U1:
				buffer.get();
				return TypeSignature.U1;
			case ELEMENT_TYPE_I2:
				buffer.get();
				return TypeSignature.I2;
			case ELEMENT_TYPE_U2:
				buffer.get();
				return TypeSignature.U2;
			case ELEMENT_TYPE_I4:
				buffer.get();
				return TypeSignature.I4;
			case ELEMENT_TYPE_U4:
				buffer.get();
				return TypeSignature.U4;
			case ELEMENT_TYPE_I8:
				buffer.get();
				return TypeSignature.I8;
			case ELEMENT_TYPE_U8:
				buffer.get();
				return TypeSignature.U8;
			case ELEMENT_TYPE_R4:
				buffer.get();
				return TypeSignature.R4;
			case ELEMENT_TYPE_R8:
				buffer.get();
				return TypeSignature.R8;
			case ELEMENT_TYPE_I:
				buffer.get();
				return TypeSignature.I;
			case ELEMENT_TYPE_U:
				buffer.get();
				return TypeSignature.U;
			case ELEMENT_TYPE_STRING:
				buffer.get();
				return TypeSignature.STRING;
			case ELEMENT_TYPE_OBJECT:
				buffer.get();
				return TypeSignature.OBJECT;
			case ELEMENT_TYPE_MVAR:
			case ELEMENT_TYPE_VAR:
				buffer.get();
				return new XGenericTypeSignature(data, buffer.getCompressedUInt32());
			case ELEMENT_TYPE_TYPEDBYREF:
				return TypeSignature.OBJECT;
			case ELEMENT_TYPE_PTR:
				return PointerTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_FNPTR:
				return FunctionPointerTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_ARRAY:
				return ArrayTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_SZARRAY:
				return SZArrayTypeSignature.parse(buffer, group);
			case ELEMENT_TYPE_GENERIC_INST:
				buffer.get();
				TypeSignature mainType = parse(buffer, group);
				int size = buffer.getCompressedUInt32();
				List<TypeSignature> list = new ArrayList<TypeSignature>(size);
				for(int i = 0; i < size; i++)
				{
					TypeSignature parse = parse(buffer, group);
					list.add(parse);
				}
				return new TypeSignatureWithGenericParameters(mainType, list);
			default:
				throw new IllegalArgumentException("Unknown element type: " + Integer.toHexString(data).toUpperCase());
		}
	}
}
