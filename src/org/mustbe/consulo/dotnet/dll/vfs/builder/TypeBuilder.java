package org.mustbe.consulo.dotnet.dll.vfs.builder;

import edu.arizona.cs.mbel.signature.ClassTypeSignature;
import edu.arizona.cs.mbel.signature.PointerTypeSignature;
import edu.arizona.cs.mbel.signature.SignatureConstants;
import edu.arizona.cs.mbel.signature.TypeSignature;
import edu.arizona.cs.mbel.signature.ValueTypeSignature;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
public class TypeBuilder implements SignatureConstants
{
	public static String typeToString(TypeSignature signature)
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
				builder.append(typeToString(pointerTypeSignature.getPointerType()));
				builder.append("*");
				break;
			case ELEMENT_TYPE_CLASS:
				ClassTypeSignature typeSignature = (ClassTypeSignature) signature;
				builder.append(typeSignature.getClassType().getFullName());
				break;
			case ELEMENT_TYPE_VALUETYPE:
				ValueTypeSignature valueTypeSignature = (ValueTypeSignature) signature;
				builder.append(valueTypeSignature.getValueType().getFullName());
				break;
			default:
				builder.append("UNK").append(Integer.toHexString(type).toUpperCase());
				break;
		}
		return builder.toString();
	}
}
