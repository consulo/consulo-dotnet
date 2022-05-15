package consulo.msil.impl.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.lang.psi.MsilCustomAttributeSignature;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilCustomAttributeSignatureStub;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 18.05.2015
 */
public class MsilCustomAttributeSignatureImpl extends MsilStubElementImpl<MsilCustomAttributeSignatureStub> implements MsilCustomAttributeSignature
{
	public MsilCustomAttributeSignatureImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilCustomAttributeSignatureImpl(@Nonnull MsilCustomAttributeSignatureStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitCustomAttributeSignature(this);
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public byte[] getBytes()
	{
		MsilCustomAttributeSignatureStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getBytes();
		}

		PsiElement[] child = findChildrenByType(MsilTokens.HEX_NUMBER_LITERAL, PsiElement.class);
		byte[] bytes = new byte[child.length];
		for(int i = 0; i < child.length; i++)
		{
			PsiElement psiElement = child[i];
			byte b = 0;
			try
			{
				b = (byte) Integer.parseInt(psiElement.getText(), 16);
			}
			catch(NumberFormatException ignored)
			{
			}
			bytes[i] = b;
		}
		return bytes;
	}
}
