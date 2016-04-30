package consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetElement;
import org.mustbe.consulo.dotnet.psi.DotNetXXXAccessor;

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
