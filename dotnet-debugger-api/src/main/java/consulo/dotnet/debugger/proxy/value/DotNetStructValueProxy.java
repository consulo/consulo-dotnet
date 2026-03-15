package consulo.dotnet.debugger.proxy.value;

import java.util.Map;


import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public interface DotNetStructValueProxy extends DotNetValueProxy
{
	DotNetStructValueProxy createNewStructValue(Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> map);

	Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> getValues();
}
