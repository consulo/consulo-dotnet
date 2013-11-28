package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public abstract class CSharpElementImpl extends ASTWrapperPsiElement
{
	public CSharpElementImpl(@NotNull ASTNode node)
	{
		super(node);
	}
}
