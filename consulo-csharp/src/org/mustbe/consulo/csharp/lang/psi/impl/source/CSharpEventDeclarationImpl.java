package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.dotnet.psi.DotNetEventDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetEventAccessor;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class CSharpEventDeclarationImpl extends CSharpMemberImpl implements DotNetEventDeclaration
{
	public CSharpEventDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitEventDeclaration(this);
	}

	@NotNull
	@Override
	public DotNetEventAccessor[] getAccessors()
	{
		return findChildrenByClass(DotNetEventAccessor.class);
	}

	@Override
	@NotNull
	public DotNetType getType()
	{
		return findNotNullChildByClass(DotNetType.class);
	}
}
