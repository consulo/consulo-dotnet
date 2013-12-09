package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetPropertyDeclaration extends DotNetVariable, DotNetMemberOwner
{
	@NotNull
	DotNetPropertyAccessor[] getAccessors();
}
