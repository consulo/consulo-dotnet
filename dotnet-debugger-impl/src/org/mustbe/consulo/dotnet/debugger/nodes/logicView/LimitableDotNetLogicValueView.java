/*
 * Copyright 2013-2015 must-be.org
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
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.util.ObjectUtil;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.ThreadMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 21.11.2015
 */
public abstract class LimitableDotNetLogicValueView<T extends Value<?>> implements DotNetLogicValueView
{
	private static final Key<Integer> LAST_INDEX = Key.create("dotnet-limit-last-index");

	public abstract int getSize(@NotNull T value);

	public abstract boolean isMyValue(@NotNull Value<?> value);

	@NotNull
	public abstract XNamedValue createChildValue(int index, @NotNull DotNetDebugContext context, @NotNull ThreadMirror threadMirror, @NotNull T value);

	@Override
	@SuppressWarnings("unchecked")
	public void computeChildren(@NotNull UserDataHolderBase dataHolder,
			@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableMirrorNode parentNode,
			@NotNull ThreadMirror threadMirror,
			@Nullable Value<?> oldValue,
			@NotNull XCompositeNode node)
	{
		if(oldValue == null || !isMyValue(oldValue))
		{
			node.setErrorMessage("No value");
			return;
		}

		T value = (T) oldValue;

		final int length = getSize(value);
		final int startIndex = ObjectUtil.notNull(dataHolder.getUserData(LAST_INDEX), 0);

		XValueChildrenList childrenList = new XValueChildrenList();
		int max = Math.min(startIndex + XCompositeNode.MAX_CHILDREN_TO_SHOW, length);
		for(int i = startIndex; i < max; i++)
		{
			childrenList.add(createChildValue(i, debugContext, threadMirror, value));
		}

		dataHolder.putUserData(LAST_INDEX, max);

		node.addChildren(childrenList, true);

		if(length > max)
		{
			node.tooManyChildren(length - max);
		}
	}
}
