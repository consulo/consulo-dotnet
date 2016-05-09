package consulo.dotnet.debugger.proxy;

import org.consulo.lombok.annotations.ArrayFactoryFields;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
@ArrayFactoryFields
public interface DotNetFieldOrPropertyProxy extends DotNetVariableProxy
{
	boolean isStatic();

	@NotNull
	DotNetTypeProxy getParentType();

	@Nullable
	DotNetValueProxy getValue(@NotNull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy);

	void setValue(@NotNull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy);
}
