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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableValueNode;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 21.11.2015
 */
public abstract class BaseDotNetLogicView implements DotNetLogicValueView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull DotNetTypeProxy typeMirror)
	{
		return false;
	}

	public abstract void computeChildrenImpl(@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableValueNode parentNode,
			@NotNull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@NotNull XValueChildrenList childrenList);

	@Override
	public void computeChildren(@NotNull UserDataHolderBase dataHolder,
			@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableValueNode parentNode,
			@NotNull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@NotNull XCompositeNode node)
	{
		XValueChildrenList childrenList = new XValueChildrenList();

		computeChildrenImpl(debugContext, parentNode, frameProxy, value, childrenList);

		node.addChildren(childrenList, true);
	}
}
