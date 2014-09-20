/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator.CantCreateException;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator.CollectionEnumeratorAsIterator;
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
public class CollectionDotNetLogicValueView implements DotNetLogicValueView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull TypeMirror typeMirror)
	{
		return MdbDebuggerUtil.isInImplementList(typeMirror, "System.Collections.ICollection");
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

		MethodMirror getEnumerator = type.findMethodByName("System.Collections.IEnumerable.GetEnumerator", true);

		if(getEnumerator == null)
		{
			return;
		}

		Value<?> getEnumeratorValue = getEnumerator.invoke(threadMirror, InvokeFlags.DISABLE_BREAKPOINTS, value);

		try
		{
			CollectionEnumeratorAsIterator iterator = new CollectionEnumeratorAsIterator(threadMirror, getEnumeratorValue, debugContext);

			while(iterator.hasNext())
			{
				Value<?> next = iterator.next();


				System.out.println(next);
				//childrenList.add(new DotNetPairValueMirrorNode(debugContext, "", threadMirror, first, second));
			}
		}
		catch(CantCreateException e)
		{
			e.printStackTrace();
		}
	}
}
