package consulo.msil.impl.lang.psi.impl.elementType.stub;

import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.msil.lang.psi.MsilConstantValue;

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
