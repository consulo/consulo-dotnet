package consulo.dotnet.debugger.nodes.logicView;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableValueNode;
import consulo.dotnet.debugger.nodes.DotNetFieldOrPropertyValueNode;
import consulo.dotnet.debugger.nodes.DotNetStructValueInfo;
import consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueNode;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
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
	public boolean canHandle(@Nonnull DotNetDebugContext debugContext, @Nonnull DotNetTypeProxy typeMirror)
	{
		return true;
	}

	@Override
	public void computeChildrenImpl(@Nonnull DotNetDebugContext debugContext,
			@Nonnull DotNetAbstractVariableValueNode parentNode,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@Nonnull XValueChildrenList childrenList)
	{
		if(value instanceof DotNetObjectValueProxy)
		{
			DotNetTypeProxy type = value.getType();

			assert type != null;

			DotNetThisAsObjectValueNode.addStaticNode(childrenList, debugContext, frameProxy, type);

			final Set<String> visited = new HashSet<String>();
			DotNetFieldOrPropertyProxy[] mirrors = DotNetDebuggerSearchUtil.getFieldAndProperties(type, true);
			for(DotNetFieldOrPropertyProxy fieldOrPropertyProxy : mirrors)
			{
				if(fieldOrPropertyProxy.isStatic() || DotNetThisAsObjectValueNode.isHiddenPropertyOrField(fieldOrPropertyProxy))
				{
					continue;
				}
				if(!visited.add(fieldOrPropertyProxy.getName()))
				{
					continue;
				}
				childrenList.add(new DotNetFieldOrPropertyValueNode(debugContext, fieldOrPropertyProxy, frameProxy, (DotNetObjectValueProxy) value));
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

				childrenList.add(new DotNetFieldOrPropertyValueNode(debugContext, fieldMirror, frameProxy, null, valueInfo));
			}
		}
	}

}
