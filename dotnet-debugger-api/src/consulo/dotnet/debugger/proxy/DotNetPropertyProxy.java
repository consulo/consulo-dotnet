package consulo.dotnet.debugger.proxy;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public interface DotNetPropertyProxy extends DotNetFieldOrPropertyProxy
{
	boolean isArrayProperty();
}
