package consulo.dotnet.microsoft.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.util.BitUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import edu.arizona.cs.mbel.signature.FieldAttributes;
import mssdw.FieldMirror;
import mssdw.ObjectValueMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftFieldProxy extends MicrosoftVariableProxyBase<FieldMirror> implements DotNetFieldProxy
{
	public MicrosoftFieldProxy(@NotNull FieldMirror mirror)
	{
		super(mirror);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MicrosoftTypeProxy.of(myMirror.type());
	}

	@Override
	public boolean isStatic()
	{
		return myMirror.isStatic();
	}

	@NotNull
	@Override
	public DotNetTypeProxy getParentType()
	{
		return MicrosoftTypeProxy.of(myMirror.parent());
	}

	@Nullable
	@Override
	public DotNetValueProxy getValue(@NotNull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy)
	{
		MicrosoftStackFrameProxy microsoftThreadProxy = (MicrosoftStackFrameProxy) frameProxy;
		MicrosoftValueProxyBase<?> microsoftValueProxyBase = (MicrosoftValueProxyBase<?>) proxy;
		return MicrosoftValueProxyUtil.wrap(myMirror.value(microsoftThreadProxy.getFrameMirror(), microsoftValueProxyBase == null ? null : (ObjectValueMirror) microsoftValueProxyBase.getMirror()));
	}

	@Override
	public void setValue(@NotNull DotNetStackFrameProxy threadProxy, @Nullable DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy)
	{
		//MonoThreadProxy monoThreadProxy = (MonoThreadProxy) threadProxy;
		//MonoObjectValueProxy monoValueProxyBase = (MonoObjectValueProxy) proxy;
		//MonoValueProxyBase<?> monoNewValueProxyBase = (MonoValueProxyBase<?>) newValueProxy;

		//myMirror.setValue(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : monoValueProxyBase.getMirror(), monoNewValueProxyBase.getMirror());
	}

	@Override
	public boolean isLiteral()
	{
		return BitUtil.isSet(myMirror.attributes(), FieldAttributes.Literal);
	}
}
