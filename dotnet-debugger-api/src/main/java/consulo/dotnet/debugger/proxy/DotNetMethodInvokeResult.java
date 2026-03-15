package consulo.dotnet.debugger.proxy;

import org.jspecify.annotations.Nullable;

import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 2020-04-18
 */
public class DotNetMethodInvokeResult
{
	private DotNetValueProxy myResult;
	private DotNetValueProxy myOutThis;

	public DotNetMethodInvokeResult(DotNetValueProxy result, @Nullable DotNetValueProxy outThis)
	{
		myResult = result;
		myOutThis = outThis;
	}

	public DotNetValueProxy getResult()
	{
		return myResult;
	}

	@Nullable
	public DotNetValueProxy getOutThis()
	{
		return myOutThis;
	}
}
