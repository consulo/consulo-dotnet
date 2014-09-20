package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator.CantCreateException;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator.DictionaryEnumeratorAsIterator;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.nodes.DotNetPairValueMirrorNode;
import com.intellij.openapi.util.Pair;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.InvokeFlags;
import mono.debugger.MethodMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DictionaryDotNetLogicValueView implements DotNetLogicValueView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull TypeMirror typeMirror)
	{
		return isInImplementList(typeMirror, "System.Collections.IDictionary");
	}

	private static boolean isInImplementList(TypeMirror typeMirror, String qName)
	{
		for(TypeMirror mirror : typeMirror.getInterfaces())
		{
			if(mirror.qualifiedName().equals(qName))
			{
				return true;
			}
		}

		TypeMirror b = typeMirror.baseType();
		return b != null && isInImplementList(b, qName);
	}

	@Override
	public void computeChildren(@NotNull DotNetDebugContext debugContext, @NotNull ThreadMirror threadMirror, @Nullable Value<?> value,
			@NotNull XValueChildrenList childrenList)
	{
		if(!(value instanceof ObjectValueMirror))
		{
			return;
		}

		TypeMirror type = value.type();

		assert type != null;

		MethodMirror getEnumerator = type.findMethodByName("System.Collections.IDictionary.GetEnumerator", true);

		if(getEnumerator == null)
		{
			return;
		}

		Value<?> getEnumeratorValue = getEnumerator.invoke(threadMirror, InvokeFlags.DISABLE_BREAKPOINTS, value);

		try
		{
			DictionaryEnumeratorAsIterator iterator = new DictionaryEnumeratorAsIterator(threadMirror, getEnumeratorValue, debugContext);

			while(iterator.hasNext())
			{
				Pair<Value<?>, Value<?>> next = iterator.next();

				Value<?> first = next.getFirst();
				Value<?> second = next.getSecond();
				if(first == null || second == null)
				{
					continue;
				}
				childrenList.add(new DotNetPairValueMirrorNode(debugContext, "", threadMirror, first, second));
			}
		}
		catch(CantCreateException e)
		{
			e.printStackTrace();
		}
	}
}
