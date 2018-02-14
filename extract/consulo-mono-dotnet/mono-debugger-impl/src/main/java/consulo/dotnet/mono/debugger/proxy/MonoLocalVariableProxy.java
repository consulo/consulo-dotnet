package consulo.dotnet.mono.debugger.proxy;

import javax.annotation.Nullable;

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

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MonoTypeProxy.of(myMirror::type);
	}
}
