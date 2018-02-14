package consulo.dotnet.debugger.nodes.logicView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableValueNode;
import consulo.dotnet.debugger.nodes.DotNetSimpleValueNode;
import consulo.dotnet.debugger.nodes.logicView.enumerator.CantCreateException;
import consulo.dotnet.debugger.nodes.logicView.enumerator.IEnumeratorAsIterator;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class EnumerableDotNetLogicValueView extends BaseDotNetLogicView
{
	@Override
	public boolean canHandle(@Nonnull DotNetDebugContext debugContext, @Nonnull DotNetTypeProxy typeMirror)
	{
		return DotNetDebuggerSearchUtil.isInImplementList(typeMirror, "System.Collections.IEnumerable");
	}

	@Override
	public void computeChildrenImpl(@Nonnull DotNetDebugContext debugContext,
			@Nonnull DotNetAbstractVariableValueNode parentNode,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@Nonnull XValueChildrenList childrenList)
	{
		if(!(value instanceof DotNetObjectValueProxy) && !(value instanceof DotNetStringValueProxy))
		{
			return;
		}

		DotNetTypeProxy type = value.getType();

		assert type != null;

		DotNetMethodProxy getEnumerator = DotNetDebuggerSearchUtil.findMethod("System.Collections.IEnumerable.GetEnumerator", type);

		if(getEnumerator == null)
		{
			return;
		}

		DotNetValueProxy getEnumeratorValue = null;
		try
		{
			getEnumeratorValue = getEnumerator.invoke(frameProxy, value);
		}
		catch(Exception e)
		{
			DotNetDebuggerSearchUtil.rethrow(frameProxy, e);
		}

		// need test returned object
		try
		{
			if(getEnumeratorValue == null)
			{
				return;
			}
			getEnumeratorValue.getType();
		}
		catch(Exception e)
		{
			return;
		}

		try
		{
			IEnumeratorAsIterator iterator = new IEnumeratorAsIterator(frameProxy, getEnumeratorValue);

			int i = 0;
			while(iterator.hasNext())
			{
				DotNetValueProxy next = iterator.next();

				if(next == null)
				{
					continue;
				}
				childrenList.add(new DotNetSimpleValueNode(debugContext, String.valueOf(i++), frameProxy, next));
			}
		}
		catch(CantCreateException e)
		{
			e.printStackTrace();
		}
	}
}
