package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.dotnet.psi.DotNetCodeBlock;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetParameterList;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpMethodDeclarationImpl extends CSharpElementImpl implements DotNetMethodDeclaration
{
	public CSharpMethodDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Nullable
	@Override
	public DotNetModifierList getModifierList()
	{
		return findChildByClass(DotNetModifierList.class);
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(CSharpTokens.IDENTIFIER);
	}

	@Override
	public String getName()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? null : nameIdentifier.getText();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean isDelegate()
	{
		return findChildByType(CSharpTokens.DELEGATE_KEYWORD) != null;
	}

	@Nullable
	@Override
	public DotNetParameterList getParameterList()
	{
		return findChildByClass(DotNetParameterList.class);
	}

	@NotNull
	@Override
	public DotNetParameter[] getParameters()
	{
		DotNetParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetParameter.EMPTY_ARRAY : parameterList.getParameters();
	}

	@Nullable
	@Override
	public DotNetCodeBlock getCodeBlock()
	{
		return findChildByClass(DotNetCodeBlock.class);
	}
}
