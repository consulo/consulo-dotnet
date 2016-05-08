package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import mssdw.LocalVariableMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftLocalVariableProxy implements DotNetLocalVariableProxy
{
	private final LocalVariableMirror myLocalVariableMirror;
	private DotNetTypeProxy myTypeProxy;

	public MicrosoftLocalVariableProxy(LocalVariableMirror localVariableMirror)
	{
		myLocalVariableMirror = localVariableMirror;
	}

	public int getIndex()
	{
		return myLocalVariableMirror.id();
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		if(myTypeProxy != null)
		{
			return myTypeProxy;
		}
		return myTypeProxy = MicrosoftTypeProxy.of(myLocalVariableMirror.type());
	}

	@NotNull
	@Override
	public String getName()
	{
		return myLocalVariableMirror.name();
	}
}
