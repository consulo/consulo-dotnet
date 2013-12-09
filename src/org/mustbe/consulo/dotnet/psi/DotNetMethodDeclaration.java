package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetMethodDeclaration extends DotNetModifierListOwner, DotNetNamedElement, PsiNameIdentifierOwner, DotNetGenericParameterListOwner
{
	boolean isDelegate();

	@Nullable
	DotNetParameterList getParameterList();

	@NotNull
	DotNetParameter[] getParameters();

	@Nullable
	DotNetCodeBlock getCodeBlock();
}
