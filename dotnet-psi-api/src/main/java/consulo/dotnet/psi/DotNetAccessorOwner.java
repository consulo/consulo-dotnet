package consulo.dotnet.psi;

import consulo.annotation.access.RequiredReadAction;

/**
 * @author VISTALL
 * @since 30.04.2016
 */
public interface DotNetAccessorOwner extends DotNetElement
{
	@RequiredReadAction
	DotNetXAccessor[] getAccessors();
}
