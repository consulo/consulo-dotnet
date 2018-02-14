package consulo.dotnet.debugger.nodes.logicView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.xdebugger.frame.XCompositeNode;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableValueNode;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public interface DotNetLogicValueView
{
	//FIXME [VISTALL] extension point?
	DotNetLogicValueView[] IMPL = new DotNetLogicValueView[]{
			new ArrayDotNetLogicValueView(),
			new StringDotNetLogicValueView(),
			new EnumerableDotNetLogicValueView(),
			new DefaultDotNetLogicValueView()
	};

	boolean canHandle(@Nonnull DotNetDebugContext debugContext, @Nonnull DotNetTypeProxy typeMirror);

	void computeChildren(@Nonnull UserDataHolderBase dataHolder,
			@Nonnull DotNetDebugContext debugContext,
			@Nonnull DotNetAbstractVariableValueNode parentNode,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@Nonnull XCompositeNode node);
}
