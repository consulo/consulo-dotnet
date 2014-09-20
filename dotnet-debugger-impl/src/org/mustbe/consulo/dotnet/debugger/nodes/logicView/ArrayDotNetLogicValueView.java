package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.nodes.DotNetArrayValueMirrorNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.ArrayValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class ArrayDotNetLogicValueView implements DotNetLogicValueView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull TypeMirror typeMirror)
	{
		return typeMirror.isArray();
	}

	@Override
	public void computeChildren(@NotNull DotNetDebugContext debugContext, @NotNull ThreadMirror threadMirror, @Nullable Value<?> value, @NotNull XValueChildrenList childrenList)
	{
		ArrayValueMirror arrayValueMirror = (ArrayValueMirror) value;
		if(arrayValueMirror == null)
		{
			return;
		}
		int length = arrayValueMirror.length();
		int min = Math.min(length, 100);
		for(int i = 0; i < min; i++)
		{
			childrenList.add(new DotNetArrayValueMirrorNode(debugContext, "[" + i + "]", threadMirror, arrayValueMirror, i));
		}
	}
}
