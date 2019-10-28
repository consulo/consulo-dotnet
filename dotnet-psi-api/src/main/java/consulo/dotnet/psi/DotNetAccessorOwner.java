package consulo.dotnet.psi;

import javax.annotation.Nonnull;
import consulo.annotations.RequiredReadAction;

/**
 * @author VISTALL
 * @since 30.04.2016
 */
public interface DotNetAccessorOwner extends DotNetElement
{
	@Nonnull
	@RequiredReadAction
	DotNetXAccessor[] getAccessors();
}
