package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpUsingListImpl extends CSharpElementImpl
{
	public CSharpUsingListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
	public CSharpUsingStatementImpl[] getStatements()
	{
		return findChildrenByClass(CSharpUsingStatementImpl.class);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitUsingList(this);
	}
}
