package consulo.msil.lang.stubbing.values;

import consulo.internal.dotnet.asm.signature.TypeSignature;

/**
 * @author VISTALL
 * @since 10.07.2015
 */
public class MsiCustomAttributeValue
{
	private TypeSignature myTypeSignature;
	private Object myValue;

	public MsiCustomAttributeValue(TypeSignature typeSignature, Object value)
	{
		myTypeSignature = typeSignature;
		myValue = value;
	}

	public Object getValue()
	{
		return myValue;
	}

	public TypeSignature getTypeSignature()
	{
		return myTypeSignature;
	}
}
