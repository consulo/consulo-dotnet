package consulo.msil.impl.lang.psi.impl.elementType;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilArrayDimension;
import consulo.msil.impl.lang.psi.impl.MsilArrayDimensionImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilArrayDimensionStub;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 10.12.14
 */
public class MsilArrayDimensionStubElementType extends AbstractMsilStubElementType<MsilArrayDimensionStub, MsilArrayDimension>
{
	public MsilArrayDimensionStubElementType()
	{
		super("ARRAY_DIMENSION");
	}

	@Nonnull
	@Override
	public MsilArrayDimension createPsi(@Nonnull MsilArrayDimensionStub msilArrayDimensionStub)
	{
		return new MsilArrayDimensionImpl(msilArrayDimensionStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilArrayDimensionStub createStub(@Nonnull MsilArrayDimension psi, StubElement parentStub)
	{
		return new MsilArrayDimensionStub(parentStub, this, psi.getLowerValue());
	}

	@Nonnull
	@Override
	public PsiElement createElement(@Nonnull ASTNode astNode)
	{
		return new MsilArrayDimensionImpl(astNode);
	}

	@Override
	public void serialize(@Nonnull MsilArrayDimensionStub stub, @Nonnull StubOutputStream dataStream) throws IOException
	{
		dataStream.writeVarInt(stub.getLowerValue());
	}

	@Nonnull
	@Override
	public MsilArrayDimensionStub deserialize(@Nonnull StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		int lowerValue = dataStream.readVarInt();
		return new MsilArrayDimensionStub(parentStub, this, lowerValue);
	}
}
