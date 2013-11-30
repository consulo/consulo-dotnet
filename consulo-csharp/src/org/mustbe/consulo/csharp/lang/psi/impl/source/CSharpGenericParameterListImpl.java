package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public class CSharpGenericParameterListImpl extends CSharpElementImpl implements DotNetGenericParameterList
{
	public CSharpGenericParameterListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitGenericParameterList(this);
	}

	@NotNull
	@Override
	public DotNetGenericParameter[] getParameters()
	{
		return findChildrenByClass(DotNetGenericParameter.class);
	}
}
