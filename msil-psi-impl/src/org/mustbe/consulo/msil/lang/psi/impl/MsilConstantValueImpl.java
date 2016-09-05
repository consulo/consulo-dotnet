package org.mustbe.consulo.msil.lang.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilConstantValue;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.MsilConstantValueStubElementType;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilConstantValueStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiUtilCore;

/**
 * @author VISTALL
 * @since 19.05.2015
 */
public class MsilConstantValueImpl extends MsilStubElementImpl<MsilConstantValueStub> implements MsilConstantValue
{
	private static final TokenSet ourValueSet = TokenSet.create(/*MsilTokens.NULLREF_KEYWORD, */MsilTokens.STRING_LITERAL,
			MsilTokens.DOUBLE_LITERAL, MsilTokens.NUMBER_LITERAL, MsilTokens.BOOL_LITERAL);

	public MsilConstantValueImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilConstantValueImpl(@NotNull MsilConstantValueStub stub, @NotNull IStubElementType nodeType)
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
		MsilConstantValueStub stub = getStub();
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

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromParent)
	{
		return DotNetTypeRef.ERROR_TYPE;
	}
}
