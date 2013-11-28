package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpModifierListImpl extends CSharpElementImpl implements DotNetModifierList
{
	public CSharpModifierListImpl(@NotNull ASTNode node)
	{
		super(node);
	}
}
