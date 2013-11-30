package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public interface DotNetGenericParameterList extends DotNetElement
{
	@NotNull
	DotNetGenericParameter[] getParameters();
}
