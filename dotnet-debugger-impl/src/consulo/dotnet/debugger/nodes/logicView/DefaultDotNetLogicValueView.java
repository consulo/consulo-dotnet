package consulo.dotnet.debugger.nodes.logicView;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import consulo.dotnet.debugger.nodes.DotNetDebuggerCompilerGenerateUtil;
import consulo.dotnet.debugger.nodes.DotNetFieldOrPropertyMirrorNode;
import consulo.dotnet.debugger.nodes.DotNetStructValueInfo;
import consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueMirrorNode;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DefaultDotNetLogicValueView extends BaseDotNetLogicView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull DotNetTypeProxy typeMirror)
	{
		return true;
	}

	@Override
	public void computeChildrenImpl(@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableMirrorNode parentNode,
			@NotNull DotNetThreadProxy threadMirror,
			@Nullable DotNetValueProxy value,
			@NotNull XValueChildrenList childrenList)
	{
		if(value instanceof DotNetObjectValueProxy)
		{
			DotNetTypeProxy type = value.getType();

			assert type != null;

			DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, threadMirror, type);

			DotNetFieldOrPropertyProxy[] mirrors = DotNetDebuggerSearchUtil.getFieldAndProperties(type, true);
			for(DotNetFieldOrPropertyProxy fieldOrPropertyProxy : mirrors)
			{
				if(needSkip(fieldOrPropertyProxy))
				{
					continue;
				}
				childrenList.add(new DotNetFieldOrPropertyMirrorNode(debugContext, fieldOrPropertyProxy, threadMirror, (DotNetObjectValueProxy) value));
			}
		}
		else if(value instanceof DotNetStructValueProxy)
		{
			Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> fields = ((DotNetStructValueProxy) value).getValues();

			for(Map.Entry<DotNetFieldOrPropertyProxy, DotNetValueProxy> entry : fields.entrySet())
			{
				DotNetFieldOrPropertyProxy fieldMirror = entry.getKey();
				DotNetValueProxy fieldValue = entry.getValue();

				DotNetStructValueInfo valueInfo = new DotNetStructValueInfo((DotNetStructValueProxy) value, parentNode, fieldMirror, fieldValue);

				childrenList.add(new DotNetFieldOrPropertyMirrorNode(debugContext, fieldMirror, threadMirror, null, valueInfo));
			}
		}
	}

	private static boolean needSkip(DotNetFieldOrPropertyProxy fieldOrPropertyProxy)
	{
		if(fieldOrPropertyProxy.isStatic())
		{
			return true;
		}
		if(DotNetDebuggerCompilerGenerateUtil.needSkipVariableByName(fieldOrPropertyProxy.getName()))
		{
			return true;
		}
		if(fieldOrPropertyProxy instanceof DotNetPropertyProxy)
		{
			if(((DotNetPropertyProxy) fieldOrPropertyProxy).isArrayProperty())
			{
				return true;
			}
		}
		return false;
	}
}
