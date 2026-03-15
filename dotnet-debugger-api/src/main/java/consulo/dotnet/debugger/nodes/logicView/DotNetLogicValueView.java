package consulo.dotnet.debugger.nodes.logicView;

import consulo.execution.debug.frame.XCompositeNode;
import consulo.execution.debug.frame.XNamedValue;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.util.dataholder.UserDataHolderBase;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public interface DotNetLogicValueView
{
	boolean canHandle(DotNetDebugContext debugContext, DotNetTypeProxy typeMirror);

	void computeChildren(UserDataHolderBase dataHolder,
			DotNetDebugContext debugContext,
			XNamedValue parentNode,
			DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			XCompositeNode node);
}
