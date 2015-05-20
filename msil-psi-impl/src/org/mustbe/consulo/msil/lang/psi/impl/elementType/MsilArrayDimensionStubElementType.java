package org.mustbe.consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.msil.lang.psi.MsilArrayDimension;
import org.mustbe.consulo.msil.lang.psi.impl.MsilArrayDimensionImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilArrayDimensionStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

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

	@NotNull
	@Override
	public MsilArrayDimension createPsi(@NotNull MsilArrayDimensionStub msilArrayDimensionStub)
	{
		return new MsilArrayDimensionImpl(msilArrayDimensionStub, this);
	}

	@Override
	public MsilArrayDimensionStub createStub(@NotNull MsilArrayDimension psi, StubElement parentStub)
	{
		return new MsilArrayDimensionStub(parentStub, this, psi.getLowerValue());
	}

	@NotNull
	@Override
	public PsiElement createElement(@NotNull ASTNode astNode)
	{
		return new MsilArrayDimensionImpl(astNode);
	}

	@Override
	public void serialize(@NotNull MsilArrayDimensionStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		dataStream.writeVarInt(stub.getLowerValue());
	}

	@NotNull
	@Override
	public MsilArrayDimensionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		int lowerValue = dataStream.readVarInt();
		return new MsilArrayDimensionStub(parentStub, this, lowerValue);
	}
}
