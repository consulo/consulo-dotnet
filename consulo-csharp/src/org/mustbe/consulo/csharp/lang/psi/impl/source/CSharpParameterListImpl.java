package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetParameterList;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpParameterListImpl extends CSharpElementImpl implements DotNetParameterList
{
	public CSharpParameterListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
	@Override
	public DotNetParameter[] getParameters()
	{
		return findChildrenByClass(DotNetParameter.class);
	}
}
