package consulo.dotnet.debugger.nodes.logicView;

import consulo.debugger.frame.XCompositeNode;
import consulo.debugger.frame.XNamedValue;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.util.dataholder.UserDataHolderBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public interface DotNetLogicValueView
{
	boolean canHandle(@Nonnull DotNetDebugContext debugContext, @Nonnull DotNetTypeProxy typeMirror);

	void computeChildren(@Nonnull UserDataHolderBase dataHolder,
			@Nonnull DotNetDebugContext debugContext,
			@Nonnull XNamedValue parentNode,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@Nonnull XCompositeNode node);
}
