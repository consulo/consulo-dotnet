package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public class CSharpGenericConstraintImpl extends CSharpElementImpl
{
	public CSharpGenericConstraintImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Nullable
	public CSharpReferenceExpressionImpl getGenericParameterReference()
	{
		return findChildByClass(CSharpReferenceExpressionImpl.class);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitGenericConstraint(this);
	}
}
