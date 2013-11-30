package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpGenericConstraintValue;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public class CSharpNewGenericConstraintValueImpl extends CSharpElementImpl implements CSharpGenericConstraintValue
{
	public CSharpNewGenericConstraintValueImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitNewGenericConstraintValue(this);
	}
}
