package consulo.dotnet.debugger.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.internal.dotnet.asm.signature.SignatureConstants;

/**
 * @author VISTALL
 * @since 25.04.14
 *
 * copy from mono library
 * @see mono.debugger.TypeTag
 */
public enum TypeTag
{
	Void("System.Void", SignatureConstants.ELEMENT_TYPE_VOID),
	Char("System.Char", SignatureConstants.ELEMENT_TYPE_CHAR),
	Boolean("System.Boolean", SignatureConstants.ELEMENT_TYPE_BOOLEAN),
	String("System.String", SignatureConstants.ELEMENT_TYPE_STRING),
	SByte("System.SByte", SignatureConstants.ELEMENT_TYPE_I1),
	Byte("System.Byte", SignatureConstants.ELEMENT_TYPE_U1),
	Int16("System.Int16", SignatureConstants.ELEMENT_TYPE_I2),
	UInt16("System.UInt16", SignatureConstants.ELEMENT_TYPE_U2),
	Int32("System.Int32", SignatureConstants.ELEMENT_TYPE_I4),
	UInt32("System.UInt32", SignatureConstants.ELEMENT_TYPE_U4),
	Int64("System.Int64", SignatureConstants.ELEMENT_TYPE_I8),
	UInt64("System.UInt64", SignatureConstants.ELEMENT_TYPE_U8),
	Single("System.Single", SignatureConstants.ELEMENT_TYPE_R4),
	Double("System.Double", SignatureConstants.ELEMENT_TYPE_R8);

	public static final TypeTag[] VALUES = values();

	private final String myType;
	private final int myTag;

	TypeTag(String type, int tag)
	{
		myType = type;
		myTag = tag;
	}

	public int getTag()
	{
		return myTag;
	}

	public String getType()
	{
		return myType;
	}

	@Nullable
	public static TypeTag byType(@NotNull String type)
	{
		for(TypeTag value : VALUES)
		{
			if(type.equals(value.getType()))
			{
				return value;
			}
		}
		return null;
	}

	public static int tagByType(@NotNull String type)
	{
		for(TypeTag value : VALUES)
		{
			if(type.equals(value.getType()))
			{
				return value.myTag;
			}
		}
		return -1;
	}

	@NotNull
	public static String typeByTag(int tag)
	{
		for(TypeTag value : VALUES)
		{
			if(value.myTag == tag)
			{
				return value.myType;
			}
		}
		throw new IllegalArgumentException(java.lang.String.valueOf(tag));
	}
}
