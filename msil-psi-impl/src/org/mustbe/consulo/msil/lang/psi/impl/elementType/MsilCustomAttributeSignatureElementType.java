package org.mustbe.consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttributeSignature;
import org.mustbe.consulo.msil.lang.psi.impl.MsilCustomAttributeSignatureImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilCustomAttributeSignatureStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

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

	@NotNull
	@Override
	public MsilCustomAttributeSignature createPsi(@NotNull MsilCustomAttributeSignatureStub msilCustomAttributeSignatureStub)
	{
		return new MsilCustomAttributeSignatureImpl(msilCustomAttributeSignatureStub, this);
	}

	@NotNull
	@Override
	public PsiElement createElement(@NotNull ASTNode astNode)
	{
		return new MsilCustomAttributeSignatureImpl(astNode);
	}

	@Override
	public MsilCustomAttributeSignatureStub createStub(@NotNull MsilCustomAttributeSignature psi, StubElement parentStub)
	{
		byte[] bytes = psi.getBytes();
		return new MsilCustomAttributeSignatureStub(parentStub, this, bytes);
	}

	@NotNull
	@Override
	public MsilCustomAttributeSignatureStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException
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
	public void serialize(@NotNull MsilCustomAttributeSignatureStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		byte[] bytes = stub.getBytes();
		dataStream.writeVarInt(bytes.length);
		for(byte b : bytes)
		{
			dataStream.writeByte(b);
		}
	}
}
