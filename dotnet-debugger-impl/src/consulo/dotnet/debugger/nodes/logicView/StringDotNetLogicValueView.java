package consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.DotNetTypes;
import com.intellij.xdebugger.frame.XNamedValue;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.nodes.DotNetSimpleValueNode;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 01.05.2016
 */
public class StringDotNetLogicValueView extends LimitableDotNetLogicValueView<DotNetStringValueProxy>
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull DotNetTypeProxy typeMirror)
	{
		return typeMirror.getFullName().equals(DotNetTypes.System.String);
	}

	@Override
	public int getSize(@NotNull DotNetStringValueProxy value)
	{
		return value.getValue().length();
	}

	@Override
	public boolean isMyValue(@NotNull DotNetValueProxy value)
	{
		return value instanceof DotNetStringValueProxy;
	}

	@NotNull
	@Override
	public XNamedValue createChildValue(int index, @NotNull DotNetDebugContext context, @NotNull DotNetStackFrameProxy frameProxy, @NotNull DotNetStringValueProxy value)
	{
		char c = value.getValue().charAt(index);
		return new DotNetSimpleValueNode(context, "[" + index + "]", frameProxy, context.getVirtualMachine().createCharValue(c));
	}
}
