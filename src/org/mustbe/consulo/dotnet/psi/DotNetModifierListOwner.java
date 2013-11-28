package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetModifierListOwner extends DotNetElement
{
	@Nullable
	DotNetModifierList getModifierList();
}
