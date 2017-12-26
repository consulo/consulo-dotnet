package consulo.dotnet.debugger.proxy.value;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public interface DotNetStructValueProxy extends DotNetValueProxy
{
	@NotNull
	DotNetStructValueProxy createNewStructValue(@NotNull Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> map);

	@NotNull
	Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> getValues();
}
