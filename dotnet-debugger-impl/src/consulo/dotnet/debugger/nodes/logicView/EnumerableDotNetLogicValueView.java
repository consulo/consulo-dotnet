package consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import consulo.dotnet.debugger.nodes.DotNetSimpleValueMirrorNode;
import consulo.dotnet.debugger.nodes.logicView.enumerator.CantCreateException;
import consulo.dotnet.debugger.nodes.logicView.enumerator.IEnumeratorAsIterator;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
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
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull DotNetTypeProxy typeMirror)
	{
		return DotNetDebuggerSearchUtil.isInImplementList(typeMirror, "System.Collections.IEnumerable");
	}

	@Override
	public void computeChildrenImpl(@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableMirrorNode parentNode,
			@NotNull DotNetThreadProxy threadMirror,
			@Nullable DotNetValueProxy value,
			@NotNull XValueChildrenList childrenList)
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
			getEnumeratorValue = getEnumerator.invoke(threadMirror, value);
		}
		catch(Exception e)
		{
			DotNetDebuggerSearchUtil.rethrow(threadMirror, e);
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
			IEnumeratorAsIterator iterator = new IEnumeratorAsIterator(threadMirror, getEnumeratorValue);

			int i = 0;
			while(iterator.hasNext())
			{
				DotNetValueProxy next = iterator.next();

				if(next == null)
				{
					continue;
				}
				childrenList.add(new DotNetSimpleValueMirrorNode(debugContext, String.valueOf(i++), threadMirror, next));
			}
		}
		catch(CantCreateException e)
		{
			e.printStackTrace();
		}
	}
}
