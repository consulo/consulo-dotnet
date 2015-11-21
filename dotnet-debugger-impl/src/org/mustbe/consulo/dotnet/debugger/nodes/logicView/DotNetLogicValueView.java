package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.xdebugger.frame.XCompositeNode;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public interface DotNetLogicValueView
{
	//FIXME [VISTALL] extension point?
	DotNetLogicValueView[] IMPL = new DotNetLogicValueView[]{
			new ArrayDotNetLogicValueView(),
			new EnumerableDotNetLogicValueView(),
			new DefaultDotNetLogicValueView()
	};

	boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull TypeMirror typeMirror);

	void computeChildren(@NotNull UserDataHolderBase dataHolder, @NotNull DotNetDebugContext debugContext, @NotNull ThreadMirror threadMirror, @Nullable Value<?> value, @NotNull XCompositeNode node);
}
