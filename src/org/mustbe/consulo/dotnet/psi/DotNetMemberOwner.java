package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 09.12.13.
 */
public interface DotNetMemberOwner extends DotNetNamedElement
{
	@NotNull
	DotNetNamedElement[] getMembers();
}
