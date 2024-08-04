package consulo.dotnet.debugger.proxy.value;

import java.util.Map;

import jakarta.annotation.Nonnull;

import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public interface DotNetStructValueProxy extends DotNetValueProxy
{
	@Nonnull
	DotNetStructValueProxy createNewStructValue(@Nonnull Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> map);

	@Nonnull
	Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> getValues();
}
