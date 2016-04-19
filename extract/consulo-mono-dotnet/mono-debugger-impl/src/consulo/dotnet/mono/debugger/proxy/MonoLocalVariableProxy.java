package consulo.dotnet.mono.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import mono.debugger.LocalVariableMirror;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MonoLocalVariableProxy implements DotNetLocalVariableProxy
{
	private LocalVariableMirror myLocalVariable;

	public MonoLocalVariableProxy( LocalVariableMirror localVariable)
	{
		myLocalVariable = localVariable;
	}

	public LocalVariableMirror getLocalVariable()
	{
		return myLocalVariable;
	}

	@NotNull
	@Override
	public DotNetTypeProxy getType()
	{
		return new MonoTypeProxy(myLocalVariable.type());
	}

	@NotNull
	@Override
	public String getName()
	{
		return myLocalVariable.name();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MonoLocalVariableProxy && myLocalVariable.equals(((MonoLocalVariableProxy) obj).myLocalVariable);
	}

	@Override
	public int hashCode()
	{
		return myLocalVariable.hashCode();
	}
}
