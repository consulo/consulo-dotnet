package org.mustbe.consulo.csharp.lang.psi;

import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public class CSharpRecursiveElementVisitor extends CSharpElementVisitor
{
	@Override
	public void visitElement(PsiElement element)
	{
		element.acceptChildren(this);
	}
}
