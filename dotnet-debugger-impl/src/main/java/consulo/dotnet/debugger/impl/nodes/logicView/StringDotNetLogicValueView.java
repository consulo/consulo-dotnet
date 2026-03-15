package consulo.dotnet.debugger.impl.nodes.logicView;

import consulo.execution.debug.frame.XNamedValue;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.impl.nodes.DotNetSimpleValueNode;
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
	public boolean canHandle(DotNetDebugContext debugContext, DotNetTypeProxy typeMirror)
	{
		return typeMirror.getFullName().equals(DotNetTypes.System.String);
	}

	@Override
	public int getSize(DotNetStringValueProxy value)
	{
		return value.getValue().length();
	}

	@Override
	public boolean isMyValue(DotNetValueProxy value)
	{
		return value instanceof DotNetStringValueProxy;
	}

	@Override
	public XNamedValue createChildValue(int index, DotNetDebugContext context, DotNetStackFrameProxy frameProxy, DotNetStringValueProxy value)
	{
		char c = value.getValue().charAt(index);
		return new DotNetSimpleValueNode(context, "[" + index + "]", frameProxy, context.getVirtualMachine().createCharValue(c));
	}
}
