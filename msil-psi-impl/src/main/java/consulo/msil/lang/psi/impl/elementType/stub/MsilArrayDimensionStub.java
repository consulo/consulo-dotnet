package consulo.msil.lang.psi.impl.elementType.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import consulo.msil.lang.psi.impl.MsilArrayDimensionImpl;

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
