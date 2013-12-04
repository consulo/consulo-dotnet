package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.dotnet.psi.DotNetFieldDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class CSharpFieldDeclarationImpl extends CSharpMemberImpl implements DotNetFieldDeclaration
{
	public CSharpFieldDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitFieldDeclaration(this);
	}

	@NotNull
	@Override
	public DotNetType getType()
	{
		return findNotNullChildByClass(DotNetType.class);
	}

	@Override
	public boolean isConstant()
	{
		return findChildByType(CSharpTokens.CONST_KEYWORD) != null;
	}
}
