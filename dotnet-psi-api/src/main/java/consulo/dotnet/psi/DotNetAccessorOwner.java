package consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;

/**
 * @author VISTALL
 * @since 30.04.2016
 */
public interface DotNetAccessorOwner extends DotNetElement
{
	@NotNull
	@RequiredReadAction
	DotNetXXXAccessor[] getAccessors();
}
