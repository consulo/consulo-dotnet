package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.dotnet.psi.DotNetPropertyAccessor;
import org.mustbe.consulo.dotnet.psi.DotNetPropertyDeclaration;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class CSharpPropertyDeclarationImpl extends CSharpMemberImpl implements DotNetPropertyDeclaration
{
	public CSharpPropertyDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitPropertyDeclaration(this);
	}

	@NotNull
	@Override
	public DotNetPropertyAccessor[] getAccessors()
	{
		return findChildrenByClass(DotNetPropertyAccessor.class);
	}
}
