package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetSimpleValueMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator.CantCreateException;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator.IEnumeratorAsIterator;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.InvokeFlags;
import mono.debugger.MethodMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.StringValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
@Deprecated
public class EnumerableDotNetLogicValueView extends BaseDotNetLogicView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull TypeMirror typeMirror)
	{
		return MdbDebuggerUtil.isInImplementList(typeMirror, "System.Collections.IEnumerable");
	}

	@Override
	public void computeChildrenImpl(@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableMirrorNode parentNode,
			@NotNull ThreadMirror threadMirror,
			@Nullable Value<?> value,
			@NotNull XValueChildrenList childrenList)
	{
		if(!(value instanceof ObjectValueMirror) && !(value instanceof StringValueMirror))
		{
			return;
		}

		TypeMirror type = value.type();

		assert type != null;

		MethodMirror getEnumerator = MdbDebuggerUtil.findMethod("System.Collections.IEnumerable.GetEnumerator", type);

		if(getEnumerator == null)
		{
			return;
		}

		Value<?> getEnumeratorValue = null;
		try
		{
			getEnumeratorValue = getEnumerator.invoke(threadMirror, InvokeFlags.DISABLE_BREAKPOINTS, value);
		}
		catch(Exception e)
		{
			MdbDebuggerUtil.rethrow(threadMirror, e);
		}

		// need test returned object
		try
		{
			if(getEnumeratorValue == null)
			{
				return;
			}
			getEnumeratorValue.type();
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
				Value<?> next = iterator.next();

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
