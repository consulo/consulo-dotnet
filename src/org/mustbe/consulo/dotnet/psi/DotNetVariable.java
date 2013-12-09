package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public interface DotNetVariable extends PsiNameIdentifierOwner, DotNetNamedElement, DotNetModifierListOwner
{
	@NotNull
	DotNetType getType();
}
