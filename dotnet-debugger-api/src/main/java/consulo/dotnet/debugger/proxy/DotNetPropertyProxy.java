package consulo.dotnet.debugger.proxy;

import jakarta.annotation.Nullable;

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
