package consulo.msil.impl.lang.psi.impl.elementType.stub;

import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.msil.lang.psi.MsilCustomAttributeSignature;

/**
 * @author VISTALL
 * @since 18.05.2015
 */
public class MsilCustomAttributeSignatureStub extends StubBase<MsilCustomAttributeSignature>
{
	private final byte[] myBytes;

	public MsilCustomAttributeSignatureStub(StubElement parent, IStubElementType elementType, byte[] bytes)
	{
		super(parent, elementType);
		myBytes = bytes;
	}

	public byte[] getBytes()
	{
		return myBytes;
	}
}
