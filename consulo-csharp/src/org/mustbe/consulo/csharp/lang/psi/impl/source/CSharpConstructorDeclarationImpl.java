package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetConstructorDeclaration;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpConstructorDeclarationImpl extends CSharpMethodDeclarationImpl implements DotNetConstructorDeclaration
{
	public CSharpConstructorDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}
}
