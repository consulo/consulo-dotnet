package consulo.dotnet.mono.debugger.proxy;

import javax.annotation.Nullable;

import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import mono.debugger.LocalVariableMirror;
import mono.debugger.TypeMirror;

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
	protected TypeMirror fetchType()
	{
		return myMirror.type();
	}
}
