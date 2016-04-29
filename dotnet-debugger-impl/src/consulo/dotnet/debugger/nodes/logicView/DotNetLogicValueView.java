package consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.xdebugger.frame.XCompositeNode;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
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
			new EnumerableDotNetLogicValueView(),
			new DefaultDotNetLogicValueView()
	};

	boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull DotNetTypeProxy typeMirror);

	void computeChildren(@NotNull UserDataHolderBase dataHolder,
			@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableMirrorNode parentNode,
			@NotNull DotNetThreadProxy threadMirror,
			@Nullable DotNetValueProxy value,
			@NotNull XCompositeNode node);
}
