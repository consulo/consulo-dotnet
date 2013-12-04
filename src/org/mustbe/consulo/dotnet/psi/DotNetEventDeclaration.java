package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetEventDeclaration extends DotNetModifierListOwner, PsiNameIdentifierOwner
{
	@NotNull
	DotNetEventAccessor[] getAccessors();
}
