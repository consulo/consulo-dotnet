package consulo.dotnet.mono.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import mono.debugger.LocalVariableMirror;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MonoLocalVariableProxy extends MonoVariableProxyBase<LocalVariableMirror> implements DotNetLocalVariableProxy
{
	public MonoLocalVariableProxy(LocalVariableMirror localVariable)
	{
		super(localVariable);
	}

	@NotNull
	@Override
	public DotNetTypeProxy getType()
	{
		return MonoTypeProxy.of(myMirror.type());
	}
}
