package consulo.dotnet.resolve;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 25.10.14
 */
public interface DotNetGenericWrapperTypeRef extends DotNetTypeRef, DotNetTypeRefWithInnerTypeRef
{
	@Nonnull
	DotNetTypeRef[] getArgumentTypeRefs();
}
