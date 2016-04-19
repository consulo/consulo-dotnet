package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.nodes.DotNetArrayValueMirrorNode;
import com.intellij.xdebugger.frame.XNamedValue;
import consulo.dotnet.debugger.DotNetDebugContext;
import mono.debugger.ArrayValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
@Deprecated
public class ArrayDotNetLogicValueView extends LimitableDotNetLogicValueView<ArrayValueMirror>
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull TypeMirror typeMirror)
	{
		return typeMirror.isArray();
	}

	@Override
	public int getSize(@NotNull ArrayValueMirror value)
	{
		return value.length();
	}

	@Override
	public boolean isMyValue(@NotNull Value<?> value)
	{
		return value instanceof ArrayValueMirror;
	}

	@NotNull
	@Override
	public XNamedValue createChildValue(int index, @NotNull DotNetDebugContext context, @NotNull ThreadMirror threadMirror, @NotNull ArrayValueMirror value)
	{
		return new DotNetArrayValueMirrorNode(context, "[" + index + "]", threadMirror, value, index);
	}
}
