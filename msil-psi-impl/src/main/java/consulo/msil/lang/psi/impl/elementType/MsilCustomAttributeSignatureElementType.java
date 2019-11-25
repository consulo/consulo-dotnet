package consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;

import javax.annotation.Nonnull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import consulo.annotation.access.RequiredReadAction;
import consulo.msil.lang.psi.MsilCustomAttributeSignature;
import consulo.msil.lang.psi.impl.MsilCustomAttributeSignatureImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilCustomAttributeSignatureStub;

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
