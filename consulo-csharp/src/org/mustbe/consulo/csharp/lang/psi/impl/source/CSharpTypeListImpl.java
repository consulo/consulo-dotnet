package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class CSharpTypeListImpl extends CSharpElementImpl
{
	public CSharpTypeListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
	public CSharpTypeImpl[] getTypes()
	{
		return findChildrenByClass(CSharpTypeImpl.class);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitTypeList(this);
	}
}
