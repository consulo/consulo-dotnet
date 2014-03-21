package org.mustbe.consulo.dotnet.dll.vfs.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.LineStubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.util.StubToStringUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Function;
import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.mbel.AbstractTypeReference;
import edu.arizona.cs.mbel.mbel.CustomAttribute;
import edu.arizona.cs.mbel.mbel.Field;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.MethodDefOrRef;
import edu.arizona.cs.mbel.mbel.MethodRef;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.signature.CustomAttributeOwner;
import edu.arizona.cs.mbel.signature.FieldAttributes;
import edu.arizona.cs.mbel.signature.MethodImplAttributes;
import edu.arizona.cs.mbel.signature.ParameterSignature;
import edu.arizona.cs.mbel.signature.SignatureConstants;
import edu.arizona.cs.mbel.signature.TypeAttributes;
import edu.arizona.cs.mbel.signature.TypeSignature;
import edu.arizona.cs.mbel.signature.ValueTypeSignature;
import lombok.val;

/**
 * @author VISTALL
 * @since 21.03.14
 */
@Logger
public class AttributeStubBuilder
{
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

	public static class ProcessAttributesCallback
	{
		public boolean extension;
	}

	public static List<LineStubBlock> processAttributes(
			CustomAttributeOwner owner, TypeDef typeDef, MethodDef methodDef, String forceTarget, ProcessAttributesCallback callback)
	{
		CustomAttribute[] customAttributes = owner.getCustomAttributes();
		if(customAttributes.length == 0)
		{
			return Collections.emptyList();
		}

		val list = new ArrayList<LineStubBlock>();
		for(CustomAttribute customAttribute : customAttributes)
		{
			MethodDefOrRef constructor = customAttribute.getConstructor();
			String type = TypeSignatureStubBuilder.toStringFromDefRefSpec(constructor.getParent(), typeDef, methodDef);
			if(Comparing.equal(type, "System.Runtime.CompilerServices.ExtensionAttribute"))
			{
				if(callback != null)
				{
					callback.extension = true;
				}
				continue;
			}

			StringBuilder builder = new StringBuilder();
			builder.append("[");
			if(forceTarget != null)
			{
				builder.append(forceTarget);
				builder.append(": ");
			}
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
		ByteBuffer byteBuffer = new ByteBuffer(signature);
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

	private static String getValueOfAttributeFromBlob(
			TypeDef typeDef, MethodDef methodDef, ByteBuffer byteBuffer, TypeSignature innerType)
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
		else if(innerType.getType() == SignatureConstants.ELEMENT_TYPE_CLASS)
		{
			long dword = byteBuffer.getDWORD();
			return String.valueOf("typeOf_" + dword);
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
						if(StubToStringUtil.isSet(field.getFlags(), FieldAttributes.Static) &&
								field.getDefaultValue() != null && StubToStringUtil.wrap(field.getDefaultValue()).getInt() == valueIndex)
						{
							return TypeSignatureStubBuilder.toStringFromDefRefSpec(valueType, typeDef, methodDef) + "." + field.getName();
						}
					}
				}
			}

			return "errorELEMENT_TYPE_VALUETYPE";
		}
		else if(innerType.getType() == SignatureConstants.ELEMENT_TYPE_STRING)
		{
			return "@" + StringUtil.QUOTER.fun(StubToStringUtil.getUtf8(byteBuffer));
		}
		else
		{
			return "unknown_type_" + innerType.getType();
		}
	}
}
