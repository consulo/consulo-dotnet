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

package consulo.dotnet.debugger.nodes.logicView;

import consulo.debugger.frame.XCompositeNode;
import consulo.debugger.frame.XNamedValue;
import consulo.debugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.util.dataholder.Key;
import consulo.util.dataholder.UserDataHolderBase;
import consulo.util.lang.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 21.11.2015
 */
public abstract class LimitableDotNetLogicValueView<T extends DotNetValueProxy> implements DotNetLogicValueView
{
	private static final Key<Integer> ourLastIndex = Key.create("dotnet-limit-last-index");

	public abstract int getSize(@Nonnull T value);

	public abstract boolean isMyValue(@Nonnull DotNetValueProxy value);

	@Nonnull
	public abstract XNamedValue createChildValue(int index, @Nonnull DotNetDebugContext context, @Nonnull DotNetStackFrameProxy frameProxy, @Nonnull T value);

	@Override
	@SuppressWarnings("unchecked")
	public void computeChildren(@Nonnull UserDataHolderBase dataHolder,
			@Nonnull DotNetDebugContext debugContext,
			@Nonnull XNamedValue parentNode,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy oldValue,
			@Nonnull XCompositeNode node)
	{
		if(oldValue == null || !isMyValue(oldValue))
		{
			node.setErrorMessage("No value");
			return;
		}

		T value = (T) oldValue;

		final int length = getSize(value);
		final int startIndex = ObjectUtil.notNull(dataHolder.getUserData(ourLastIndex), 0);

		XValueChildrenList childrenList = new XValueChildrenList();
		int max = Math.min(startIndex + XCompositeNode.MAX_CHILDREN_TO_SHOW, length);
		for(int i = startIndex; i < max; i++)
		{
			childrenList.add(createChildValue(i, debugContext, frameProxy, value));
		}

		dataHolder.putUserData(ourLastIndex, max);

		node.addChildren(childrenList, true);

		if(length > max)
		{
			node.tooManyChildren(length - max);
		}
	}
}
