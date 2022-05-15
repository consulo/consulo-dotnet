package consulo.msil.impl.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.lang.psi.MsilArrayDimension;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilArrayDimensionStub;
import consulo.util.collection.ArrayFactory;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 10.12.14
 */
public class MsilArrayDimensionImpl extends MsilStubElementImpl<MsilArrayDimensionStub> implements MsilArrayDimension
{
	public static final MsilArrayDimensionImpl[] EMPTY_ARRAY = new MsilArrayDimensionImpl[0];

	public static ArrayFactory<MsilArrayDimensionImpl> ARRAY_FACTORY = new ArrayFactory<MsilArrayDimensionImpl>()
	{
		@Nonnull
		@Override
		public MsilArrayDimensionImpl[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new MsilArrayDimensionImpl[count];
		}
	};

	public MsilArrayDimensionImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilArrayDimensionImpl(@Nonnull MsilArrayDimensionStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	@RequiredReadAction
	public int getLowerValue()
	{
		MsilArrayDimensionStub stub = getGreenStub();
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
