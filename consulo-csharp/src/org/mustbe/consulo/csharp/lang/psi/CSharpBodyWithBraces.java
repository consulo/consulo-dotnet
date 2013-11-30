package org.mustbe.consulo.csharp.lang.psi;

import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public interface CSharpBodyWithBraces extends PsiElement
{
	PsiElement getLeftBrace();

	PsiElement getRightBrace();
}
