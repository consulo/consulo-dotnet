package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpNamespaceDeclarationImpl extends CSharpElementImpl implements DotNetNamespaceDeclaration
{
	public CSharpNamespaceDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Nullable
	@Override
	public DotNetModifierList getModifierList()
	{
		return findChildByClass(DotNetModifierList.class);
	}
}
