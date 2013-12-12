package org.mustbe.consulo.dotnet.psi;

import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetTypeDeclaration extends DotNetModifierListOwner, DotNetGenericParameterListOwner, PsiNameIdentifierOwner, DotNetMemberOwner
{
	boolean isInterface();

	boolean isStruct();

	boolean isEnum();
}
