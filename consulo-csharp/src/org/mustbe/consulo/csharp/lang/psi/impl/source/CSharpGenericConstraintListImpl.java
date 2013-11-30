package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public class CSharpGenericConstraintListImpl extends CSharpElementImpl
{
	public CSharpGenericConstraintListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitGenericConstraintList(this);
	}
}
