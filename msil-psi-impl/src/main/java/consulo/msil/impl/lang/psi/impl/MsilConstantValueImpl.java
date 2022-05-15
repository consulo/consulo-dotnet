package consulo.msil.impl.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiUtilCore;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.lang.psi.MsilConstantValue;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.impl.lang.psi.impl.elementType.MsilConstantValueStubElementType;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilConstantValueStub;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 19.05.2015
 */
public class MsilConstantValueImpl extends MsilStubElementImpl<MsilConstantValueStub> implements MsilConstantValue
{
	private static final TokenSet ourValueSet = TokenSet.create(/*MsilTokens.NULLREF_KEYWORD, */MsilTokens.STRING_LITERAL,
			MsilTokens.DOUBLE_LITERAL, MsilTokens.NUMBER_LITERAL, MsilTokens.BOOL_LITERAL);

	public MsilConstantValueImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilConstantValueImpl(@Nonnull MsilConstantValueStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitContantValue(this);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public IElementType getValueType()
	{
		MsilConstantValueStub stub = getGreenStub();
		if(stub != null)
		{
			int valueIndex = stub.getValueIndex();
			return valueIndex == -1 ? null : MsilConstantValueStubElementType.ourElements[valueIndex];
		}

		PsiElement childByType = findChildByType(MsilConstantValueStubElementType.ourSet);
		if(childByType != null)
		{
			return PsiUtilCore.getElementType(childByType);
		}
		childByType = findChildByType(MsilTokens.STRING_LITERAL);
		if(childByType != null)
		{
			return MsilTokens.STRING_KEYWORD;
		}
		childByType = findChildByType(MsilTokens.NULLREF_KEYWORD);
		if(childByType != null)
		{
			return MsilTokens.NULLREF_KEYWORD;
		}
		return null;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getValueText()
	{
		PsiElement childByType = findChildByType(ourValueSet);
		if(childByType != null)
		{
			return childByType.getText();
		}
		return null;
	}

	@Nonnull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromParent)
	{
		return DotNetTypeRef.ERROR_TYPE;
	}
}
