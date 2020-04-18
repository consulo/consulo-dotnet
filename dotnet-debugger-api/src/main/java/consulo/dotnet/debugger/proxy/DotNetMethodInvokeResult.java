package consulo.dotnet.debugger.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 2020-04-18
 */
public class DotNetMethodInvokeResult
{
	private DotNetValueProxy myResult;
	private DotNetValueProxy myOutThis;

	public DotNetMethodInvokeResult(@Nonnull DotNetValueProxy result, @Nullable DotNetValueProxy outThis)
	{
		myResult = result;
		myOutThis = outThis;
	}

	@Nonnull
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
