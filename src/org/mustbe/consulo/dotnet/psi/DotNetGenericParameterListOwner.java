package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public interface DotNetGenericParameterListOwner extends DotNetElement
{
	@Nullable
	DotNetGenericParameterList getGenericParameterList();

	@NotNull
	DotNetGenericParameter[] getGenericParameters();
}
