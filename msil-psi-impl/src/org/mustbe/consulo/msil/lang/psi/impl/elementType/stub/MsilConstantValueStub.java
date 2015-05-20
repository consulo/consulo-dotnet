package org.mustbe.consulo.msil.lang.psi.impl.elementType.stub;

import org.mustbe.consulo.msil.lang.psi.MsilConstantValue;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;

/**
 * @author VISTALL
 * @since 19.05.2015
 */
public class MsilConstantValueStub extends StubBase<MsilConstantValue>
{
	private int myValueIndex;
	private String myValueText;

	public MsilConstantValueStub(StubElement parent, IStubElementType elementType, int valueIndex, String value)
	{
		super(parent, elementType);
		myValueIndex = valueIndex;
		myValueText = value;
	}

	public int getValueIndex()
	{
		return myValueIndex;
	}

	public String getValue()
	{
		return myValueText;
	}
}
