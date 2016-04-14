package org.mustbe.consulo.msil.lang.stubbing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joou.UByte;
import org.joou.UInteger;
import org.joou.UShort;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetAttributeUtil;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilConstantValue;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttribute;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttributeSignature;
import org.mustbe.consulo.msil.lang.psi.MsilFieldEntry;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.MsilNativeTypeImpl;
import org.mustbe.consulo.msil.lang.psi.impl.MsilUserTypeImpl;
import org.mustbe.consulo.msil.lang.stubbing.values.MsiCustomAttributeValue;
import org.mustbe.consulo.msil.lang.stubbing.values.MsilCustomAttributeEnumValue;
import org.mustbe.dotnet.asm.STypeSignatureParser;
import org.mustbe.dotnet.msil.decompiler.textBuilder.util.XStubUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.containers.hash.LinkedHashMap;
import edu.arizona.cs.mbel.io.ByteBuffer;
import edu.arizona.cs.mbel.mbel.AbstractTypeReference;
import edu.arizona.cs.mbel.mbel.AssemblyTypeRef;
import edu.arizona.cs.mbel.signature.ClassTypeSignature;
import edu.arizona.cs.mbel.signature.TypeSignature;
import edu.arizona.cs.mbel.signature.TypeSignatureParser;
import edu.arizona.cs.mbel.signature.ValueTypeSignature;

/**
 * @author VISTALL
 * @since 10.07.2015
 */
@Logger
public class MsilCustomAttributeStubber
{
	@NotNull
	@RequiredReadAction
	public static MsilCustomAttributeArgumentList build(MsilCustomAttribute attribute)
	{
		MsilCustomAttributeSignature signature = attribute.getSignature();
		byte[] bytes = signature.getBytes();

		ByteBuffer byteBuffer = new ByteBuffer(bytes);

		List<MsiCustomAttributeValue> constructorArguments = new ArrayList<MsiCustomAttributeValue>();
		Map<String, MsiCustomAttributeValue> namedArguments = new LinkedHashMap<String, MsiCustomAttributeValue>();
		if(byteBuffer.canRead() && byteBuffer.getShort() == 1)
		{
			boolean failed = false;
			DotNetParameter[] parameters = attribute.getParameterList().getParameters();
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
					MsiCustomAttributeValue attributeValue = buildArgument(attribute, typeSignature, byteBuffer);
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
						CharSequence name = XStubUtil.getString(byteBuffer, CharsetToolkit.UTF8_CHARSET);
						if(name.length() == 0)
						{
							continue;
						}

						MsiCustomAttributeValue attributeValue = buildArgument(attribute, typeSignature, byteBuffer);
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
	private static TypeSignature toTypeSignature(@NotNull DotNetType type)
	{
		if(type instanceof MsilNativeTypeImpl)
		{
			IElementType elementType = PsiUtilCore.getElementType(((MsilNativeTypeImpl) type).getTypeElement());
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
		}
		else if(type instanceof MsilUserTypeImpl)
		{
			String referenceText = ((MsilUserTypeImpl) type).getReferenceText();

			switch(((MsilUserTypeImpl) type).getTypeResoleKind())
			{
				case STRUCT:
					return new ValueTypeSignature(parse(referenceText));
				case UNKNOWN:
				case CLASS:
					return new ClassTypeSignature(parse(referenceText));
			}
		}
		LOGGER.error("Unknown how convert: " + type);
		return null;
	}

	@NotNull
	private static AbstractTypeReference parse(String referenceText)
	{
		return new AssemblyTypeRef(null, StringUtil.getPackageName(referenceText), StringUtil.getShortName(referenceText));
	}

	@RequiredReadAction
	private static MsiCustomAttributeValue buildArgument(@NotNull PsiElement scope, TypeSignature typeSignature, ByteBuffer byteBuffer)
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
		/*else if(typeSignature == TypeSignature.R8)
		{
			return new MsiCustomAttributeValue(typeSignature, byteBuffer.getDouble());
		}  */
		else if(typeSignature == TypeSignature.BOOLEAN)
		{
			return new MsiCustomAttributeValue(typeSignature, byteBuffer.get() == 1);
		}
		else if(typeSignature == TypeSignature.STRING)
		{
			return new MsiCustomAttributeValue(typeSignature, XStubUtil.getString(byteBuffer, CharsetToolkit.UTF8_CHARSET));
		}
		else if(typeSignature instanceof ClassTypeSignature)
		{
			String vmQName = ((ClassTypeSignature) typeSignature).getClassType().getFullName();
			if(vmQName.equals(DotNetTypes.System.Type))
			{
				CharSequence text = XStubUtil.getString(byteBuffer, CharsetToolkit.UTF8_CHARSET);
				TypeSignature stringTypeSignature = STypeSignatureParser.parse(text);
				return new MsiCustomAttributeValue(typeSignature, stringTypeSignature);
			}
		}
		else if(typeSignature instanceof ValueTypeSignature)
		{
			String vmQName = ((ValueTypeSignature) typeSignature).getValueType().getFullName();

			DotNetTypeDeclaration resolvedElement = DotNetPsiSearcher.getInstance(scope.getProject()).findType(vmQName, scope.getResolveScope());
			if(resolvedElement != null && resolvedElement.isEnum())
			{
				Number value = getValue(scope, byteBuffer, resolvedElement.getTypeRefForEnumConstants());
				if(value != null)
				{
					Map<Long, String> map = new HashMap<Long, String>();

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
						List<String> fields = new ArrayList<String>();

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
			LOGGER.error("Can't get value for ValueType: " + vmQName);
			return null;
		}
		LOGGER.error("Cant get value for: " + typeSignature);
		return null;
	}

	@Nullable
	@RequiredReadAction
	private static Number getValue(PsiElement scope, ByteBuffer byteBuffer, DotNetTypeRef typeRef)
	{
		PsiElement resolvedElement = typeRef.resolve(scope).getElement();
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
}
