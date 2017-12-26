package consulo.dotnet.debugger.proxy;

import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public interface DotNetPropertyProxy extends DotNetFieldOrPropertyProxy
{
	boolean isArrayProperty();

	@Nullable
	DotNetMethodProxy getGetMethod();
}
