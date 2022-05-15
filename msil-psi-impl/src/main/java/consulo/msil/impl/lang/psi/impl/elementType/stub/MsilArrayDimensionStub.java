package consulo.msil.impl.lang.psi.impl.elementType.stub;

import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.msil.impl.lang.psi.impl.MsilArrayDimensionImpl;

/**
 * @author VISTALL
 * @since 10.12.14
 */
public class MsilArrayDimensionStub extends StubBase<MsilArrayDimensionImpl>
{
	private final int myLowerValue;

	public MsilArrayDimensionStub(StubElement parent, IStubElementType elementType, int lowerValue)
	{
		super(parent, elementType);
		myLowerValue = lowerValue;
	}

	public int getLowerValue()
	{
		return myLowerValue;
	}
}
