package org.mustbe.consulo.msil.lang.psi.impl;

import consulo.lombok.annotations.ArrayFactoryFields;
import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.msil.lang.psi.MsilArrayDimension;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilArrayDimensionStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 10.12.14
 */
@ArrayFactoryFields
public class MsilArrayDimensionImpl extends MsilStubElementImpl<MsilArrayDimensionStub> implements MsilArrayDimension
{
	public MsilArrayDimensionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilArrayDimensionImpl(@NotNull MsilArrayDimensionStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	@RequiredReadAction
	public int getLowerValue()
	{
		MsilArrayDimensionStub stub = getStub();
		if(stub != null)
		{
			return stub.getLowerValue();
		}

		PsiElement numberElement = findChildByType(MsilTokens.NUMBER_LITERAL);
		if(numberElement == null)
		{
			return -1;
		}
		return Integer.parseInt(numberElement.getText());
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitArrayDimension(this);
	}
}
