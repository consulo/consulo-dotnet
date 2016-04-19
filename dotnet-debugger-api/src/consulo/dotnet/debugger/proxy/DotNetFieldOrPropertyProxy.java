package consulo.dotnet.debugger.proxy;

import org.consulo.lombok.annotations.ArrayFactoryFields;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
@ArrayFactoryFields
public interface DotNetFieldOrPropertyProxy extends DotNetVariableProxy
{
	boolean isStatic();
}
