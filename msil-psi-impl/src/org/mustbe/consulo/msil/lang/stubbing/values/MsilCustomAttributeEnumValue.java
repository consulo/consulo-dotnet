package org.mustbe.consulo.msil.lang.stubbing.values;

import java.util.List;

import consulo.internal.dotnet.asm.signature.TypeSignature;

/**
 * @author VISTALL
 * @since 10.07.2015
 */
public class MsilCustomAttributeEnumValue extends MsiCustomAttributeValue
{
	private List<String> myValues;

	public MsilCustomAttributeEnumValue(TypeSignature typeSignature, Number value, List<String> values)
	{
		super(typeSignature, value);
		myValues = values;
	}

	public List<String> getValues()
	{
		return myValues;
	}
}
