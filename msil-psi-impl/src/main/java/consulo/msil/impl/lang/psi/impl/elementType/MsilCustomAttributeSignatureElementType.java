package consulo.msil.impl.lang.psi.impl.elementType;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilCustomAttributeSignature;
import consulo.msil.impl.lang.psi.impl.MsilCustomAttributeSignatureImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilCustomAttributeSignatureStub;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 18.05.2015
 */
public class MsilCustomAttributeSignatureElementType extends AbstractMsilStubElementType<MsilCustomAttributeSignatureStub, MsilCustomAttributeSignature>
{
	public MsilCustomAttributeSignatureElementType()
	{
		super("CUSTOM_ATTRIBUTE_SIGNATURE");
	}

	@Nonnull
	@Override
	public MsilCustomAttributeSignature createPsi(@Nonnull MsilCustomAttributeSignatureStub msilCustomAttributeSignatureStub)
	{
		return new MsilCustomAttributeSignatureImpl(msilCustomAttributeSignatureStub, this);
	}

	@Nonnull
	@Override
	public PsiElement createElement(@Nonnull ASTNode astNode)
	{
		return new MsilCustomAttributeSignatureImpl(astNode);
	}

	@RequiredReadAction
	@Override
	public MsilCustomAttributeSignatureStub createStub(@Nonnull MsilCustomAttributeSignature psi, StubElement parentStub)
	{
		byte[] bytes = psi.getBytes();
		return new MsilCustomAttributeSignatureStub(parentStub, this, bytes);
	}

	@Nonnull
	@Override
	public MsilCustomAttributeSignatureStub deserialize(@Nonnull StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		int count = dataStream.readVarInt();
		byte[] data = new byte[count];
		for(int j = 0; j < count; j++)
		{
			data[j] = dataStream.readByte();
		}
		return new MsilCustomAttributeSignatureStub(parentStub, this, data);
	}

	@Override
	public void serialize(@Nonnull MsilCustomAttributeSignatureStub stub, @Nonnull StubOutputStream dataStream) throws IOException
	{
		byte[] bytes = stub.getBytes();
		dataStream.writeVarInt(bytes.length);
		for(byte b : bytes)
		{
			dataStream.writeByte(b);
		}
	}
}
