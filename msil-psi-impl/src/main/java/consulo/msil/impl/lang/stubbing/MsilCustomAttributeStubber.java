package consulo.msil.impl.lang.stubbing;

import consulo.annotation.UsedInPlugin;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.*;
import consulo.dotnet.psi.resolve.DotNetPsiSearcher;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.internal.dotnet.asm.STypeSignatureParser;
import consulo.internal.dotnet.asm.io.ByteBuffer;
import consulo.internal.dotnet.asm.mbel.AbstractTypeReference;
import consulo.internal.dotnet.asm.mbel.AssemblyTypeRef;
import consulo.internal.dotnet.asm.signature.ClassTypeSignature;
import consulo.internal.dotnet.asm.signature.TypeSignature;
import consulo.internal.dotnet.asm.signature.TypeSignatureParser;
import consulo.internal.dotnet.asm.signature.ValueTypeSignature;
import consulo.internal.dotnet.msil.decompiler.textBuilder.util.XStubUtil;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.logging.Logger;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.impl.lang.psi.impl.MsilNativeTypeImpl;
import consulo.msil.impl.lang.psi.impl.MsilUserTypeImpl;
import consulo.msil.impl.lang.stubbing.values.MsiCustomAttributeValue;
import consulo.msil.impl.lang.stubbing.values.MsilCustomAttributeEnumValue;
import consulo.msil.lang.psi.MsilConstantValue;
import consulo.msil.lang.psi.MsilCustomAttribute;
import consulo.msil.lang.psi.MsilCustomAttributeSignature;
import consulo.msil.lang.psi.MsilFieldEntry;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nullable;
import org.joou.UByte;
import org.joou.UInteger;
import org.joou.UShort;

import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author VISTALL
 * @since 10.07.2015
 */
@UsedInPlugin
public class MsilCustomAttributeStubber
{
	private static final Logger LOGGER = Logger.getInstance(MsilCustomAttributeStubber.class);

	@Nonnull
	@RequiredReadAction
	public static MsilCustomAttributeArgumentList build(MsilCustomAttribute attribute)
	{
		MsilCustomAttributeSignature signature = attribute.getSignature();
		byte[] bytes = signature.getBytes();

		ByteBuffer byteBuffer = new ByteBuffer(bytes);

		List<MsiCustomAttributeValue> constructorArguments = new ArrayList<>();
		Map<String, MsiCustomAttributeValue> namedArguments = new LinkedHashMap<>();
		if(byteBuffer.canRead() && byteBuffer.getShort() == 1)
		{
			boolean failed = false;
			DotNetParameter[] parameters = attribute.getParameterList().getParameters();
			Project project = attribute.getProject();
			for(DotNetParameter parameter : parameters)
			{
				try
				{
					DotNetType type = parameter.getType();
					if(type == null)
					{
						throw new NullPointerException();
					}

					TypeSignature typeSignature = toTypeSignature(type);
					MsiCustomAttributeValue attributeValue = buildArgument(project, typeSignature, byteBuffer);
					if(attributeValue != null)
					{
						constructorArguments.add(attributeValue);
					}
				}
				catch(Exception e)
				{
					failed = true;
					break;
				}
			}


			if(!failed && byteBuffer.canRead())
			{
				try
				{
					int count = byteBuffer.getShort();
					for(int i = 0; i < count; i++)
					{
						byteBuffer.get(); //type  0x53 field 0x54 property
						TypeSignature typeSignature = TypeSignatureParser.parse(byteBuffer, null);

						if(typeSignature == null)
						{
							continue;
						}
						CharSequence name = XStubUtil.getString(byteBuffer, StandardCharsets.UTF_8);
						if(name.length() == 0)
						{
							continue;
						}

						MsiCustomAttributeValue attributeValue = buildArgument(project, typeSignature, byteBuffer);
						if(attributeValue != null)
						{
							namedArguments.put(name.toString(), attributeValue);
						}
					}
				}
				catch(Exception e)
				{
					LOGGER.warn(e);
				}
			}
		}

		return new MsilCustomAttributeArgumentList(constructorArguments, namedArguments);
	}

	@RequiredReadAction
	private static TypeSignature toTypeSignature(@Nonnull DotNetType type)
	{
		if(type instanceof MsilNativeTypeImpl)
		{
			IElementType elementType = ((MsilNativeTypeImpl) type).getTypeElementType();
			if(elementType == MsilTokens.STRING_KEYWORD)
			{
				return TypeSignature.STRING;
			}
			else if(elementType == MsilTokens.BOOL_KEYWORD)
			{
				return TypeSignature.BOOLEAN;
			}
			/*else if(elementType == MsilTokens.INT_KEYWORD)
			{
				return TypeSignature.INT_PTR;
			}
			else if(elementType == MsilTokens.UINT_KEYWORD)
			{
				return TypeSignature.UINT_PTR;
			}   */
			else if(elementType == MsilTokens.INT8_KEYWORD)
			{
				return TypeSignature.I1;
			}
			else if(elementType == MsilTokens.UINT8_KEYWORD)
			{
				return TypeSignature.U1;
			}
			else if(elementType == MsilTokens.INT16_KEYWORD)
			{
				return TypeSignature.I2;
			}
			else if(elementType == MsilTokens.UINT16_KEYWORD)
			{
				return TypeSignature.U2;
			}
			else if(elementType == MsilTokens.INT32_KEYWORD)
			{
				return TypeSignature.I4;
			}
			else if(elementType == MsilTokens.UINT32_KEYWORD)
			{
				return TypeSignature.U4;
			}
			else if(elementType == MsilTokens.INT64_KEYWORD)
			{
				return TypeSignature.I8;
			}
			else if(elementType == MsilTokens.UINT64_KEYWORD)
			{
				return TypeSignature.U8;
			}
			else if(elementType == MsilTokens.FLOAT32_KEYWORD)
			{
				return TypeSignature.R4;
			}
			else if(elementType == MsilTokens.FLOAT64_KEYWORD)
			{
				return TypeSignature.R8;
			}
			else if(elementType == MsilTokens.OBJECT_KEYWORD)
			{
				return TypeSignature.OBJECT;
			}
		}
		else if(type instanceof MsilUserTypeImpl)
		{
			String referenceText = ((MsilUserTypeImpl) type).getReferenceText();

			switch(((MsilUserTypeImpl) type).getTarget())
			{
				case STRUCT:
					return new ValueTypeSignature(parse(referenceText));
				case CLASS:
					return new ClassTypeSignature(parse(referenceText));
			}
		}
		LOGGER.error("Unknown how convert: " + type);
		return null;
	}

	@Nonnull
	private static AbstractTypeReference parse(String referenceText)
	{
		return new AssemblyTypeRef(null, StringUtil.getPackageName(referenceText), StringUtil.getShortName(referenceText));
	}

	@RequiredReadAction
	private static MsiCustomAttributeValue buildArgument(@Nonnull Project project, TypeSignature typeSignature, ByteBuffer byteBuffer)
	{
		if(typeSignature == TypeSignature.I1)
		{
			return new MsiCustomAttributeValue(typeSignature, byteBuffer.get());
		}
		else if(typeSignature == TypeSignature.U1)
		{
			return new MsiCustomAttributeValue(typeSignature, UByte.valueOf(byteBuffer.get() & 0xFF));
		}
		else if(typeSignature == TypeSignature.I2)
		{
			return new MsiCustomAttributeValue(typeSignature, byteBuffer.getShort());
		}
		else if(typeSignature == TypeSignature.U2)
		{
			return new MsiCustomAttributeValue(typeSignature, UShort.valueOf(byteBuffer.getShort() & 0xFFFF));
		}
		else if(typeSignature == TypeSignature.I4)
		{
			return new MsiCustomAttributeValue(typeSignature, byteBuffer.getInt());
		}
		else if(typeSignature == TypeSignature.U4)
		{
			return new MsiCustomAttributeValue(typeSignature, UInteger.valueOf(byteBuffer.getInt() & 0xFFFFFFFFL));
		}
		/*else if(typeSignature == TypeSignature.I8)
		{
			return new MsiCustomAttributeValue(typeSignature, byteBuffer.getLong());
		}
		else if(typeSignature == TypeSignature.U8)
		{
			return new MsiCustomAttributeValue(typeSignature, ULong);
		} */
		else if(typeSignature == TypeSignature.R4)
		{
			return new MsiCustomAttributeValue(typeSignature, Float.intBitsToFloat(byteBuffer.getInt()));
		}
		else if(typeSignature == TypeSignature.R8)
		{
			byte[] bytes = byteBuffer.get(8);
			reverse(bytes);  // correct order
			return new MsiCustomAttributeValue(typeSignature, Double.longBitsToDouble(longFromByteArray(bytes)));
		}
		else if(typeSignature == TypeSignature.BOOLEAN)
		{
			return new MsiCustomAttributeValue(typeSignature, byteBuffer.get() == 1);
		}
		else if(typeSignature == TypeSignature.STRING)
		{
			return new MsiCustomAttributeValue(typeSignature, XStubUtil.getString(byteBuffer, StandardCharsets.UTF_8));
		}
		else if(typeSignature instanceof ClassTypeSignature)
		{
			String vmQName = ((ClassTypeSignature) typeSignature).getClassType().getFullName();
			if(vmQName.equals(DotNetTypes.System.Type))
			{
				CharSequence text = XStubUtil.getString(byteBuffer, StandardCharsets.UTF_8);
				TypeSignature stringTypeSignature = STypeSignatureParser.parse(text);
				return new MsiCustomAttributeValue(typeSignature, stringTypeSignature);
			}
		}
		else if(typeSignature == TypeSignature.OBJECT)
		{
			int b = byteBuffer.get() & 0xFF;
			switch(b)
			{
				case 0x55: // enum
					CharSequence text = XStubUtil.getString(byteBuffer, StandardCharsets.UTF_8);
					TypeSignature stringTypeSignature = STypeSignatureParser.parse(text);
					return new MsiCustomAttributeValue(typeSignature, stringTypeSignature);
			}
			return new MsiCustomAttributeValue(TypeSignature.OBJECT, null);
		}
		else if(typeSignature instanceof ValueTypeSignature)
		{
			String vmQName = ((ValueTypeSignature) typeSignature).getValueType().getFullName();

			DotNetTypeDeclaration resolvedElement = DotNetPsiSearcher.getInstance(project).findType(vmQName, GlobalSearchScope.allScope(project));
			if(resolvedElement != null && resolvedElement.isEnum())
			{
				Number value = getValue(byteBuffer, resolvedElement.getTypeRefForEnumConstants());
				if(value != null)
				{
					Map<Long, String> map = new HashMap<>();

					long l = value.longValue();
					DotNetNamedElement[] members = resolvedElement.getMembers();
					for(DotNetNamedElement member : members)
					{
						if(member instanceof MsilFieldEntry && ((MsilFieldEntry) member).hasModifier(MsilTokens.LITERAL_KEYWORD))
						{
							DotNetExpression initializer = ((MsilFieldEntry) member).getInitializer();
							if(!(initializer instanceof MsilConstantValue))
							{
								continue;
							}
							String valueText = ((MsilConstantValue) initializer).getValueText();
							map.put(Long.parseLong(valueText), member.getName());
						}
					}

					if(DotNetAttributeUtil.hasAttribute(resolvedElement, DotNetTypes.System.FlagsAttribute))
					{
						List<String> fields = new ArrayList<>();

						for(Map.Entry<Long, String> entry : map.entrySet())
						{
							if((l & entry.getKey()) == entry.getKey())
							{
								fields.add(entry.getValue());
							}
						}
						return new MsilCustomAttributeEnumValue(typeSignature, l, fields);
					}
					else
					{
						String stringValue = map.get(l);
						if(stringValue != null)
						{
							return new MsilCustomAttributeEnumValue(typeSignature, value, Collections.singletonList(stringValue));
						}
						else
						{
							return new MsiCustomAttributeValue(typeSignature, value);
						}
					}
				}
				else
				{
					throw new IllegalArgumentException("Cant get value from enum: " + vmQName);
				}
			}
			LOGGER.warn("Can't get value for ValueType: " + vmQName);
			return null;
		}
		LOGGER.warn("Cant get value for: " + typeSignature);
		return null;
	}

	public static long longFromByteArray(byte[] bytes)
	{
		return longFromBytes(
				bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]);
	}

	public static long longFromBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8)
	{
		return (b1 & 0xFFL) << 56
				| (b2 & 0xFFL) << 48
				| (b3 & 0xFFL) << 40
				| (b4 & 0xFFL) << 32
				| (b5 & 0xFFL) << 24
				| (b6 & 0xFFL) << 16
				| (b7 & 0xFFL) << 8
				| (b8 & 0xFFL);
	}

	@Nullable
	@RequiredReadAction
	private static Number getValue(ByteBuffer byteBuffer, DotNetTypeRef typeRef)
	{
		PsiElement resolvedElement = typeRef.resolve().getElement();
		String qName = null;
		if(resolvedElement instanceof DotNetTypeDeclaration)
		{
			qName = ((DotNetTypeDeclaration) resolvedElement).getVmQName();
		}

		if(qName == null)
		{
			return null;
		}

		if(qName.equals(DotNetTypes.System.Int32))
		{
			return byteBuffer.getInt();
		}
		else if(qName.equals(DotNetTypes.System.UInt32))
		{
			return byteBuffer.getInt() & 0xFFFFFFFFL;
		}
		else if(qName.equals(DotNetTypes.System.Int16))
		{
			return byteBuffer.getShort();
		}
		else if(qName.equals(DotNetTypes.System.UInt16))
		{
			return byteBuffer.getShort() & 0xFFFF;
		}
		else if(qName.equals(DotNetTypes.System.Byte))
		{
			return byteBuffer.get() & 0xFF;
		}
		else if(qName.equals(DotNetTypes.System.SByte))
		{
			return byteBuffer.getShort();
		}
		LOGGER.warn("Unknown type: " + qName);
		return null;
	}

	public static void reverse(byte[] array)
	{
		if(array == null)
		{
			return;
		}
		int i = 0;
		int j = array.length - 1;
		byte tmp;
		while(j > i)
		{
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}
}
