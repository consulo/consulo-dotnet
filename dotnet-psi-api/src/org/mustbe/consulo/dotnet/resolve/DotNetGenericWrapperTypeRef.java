package org.mustbe.consulo.dotnet.resolve;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 25.10.14
 */
public interface DotNetGenericWrapperTypeRef extends DotNetTypeRef, DotNetTypeRefWithInnerTypeRef
{
	@NotNull
	DotNetTypeRef[] getArgumentTypeRefs();
}
