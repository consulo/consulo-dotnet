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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.logging.Logger;
import consulo.util.dataholder.UserDataHolderBase;

/**
 * @author VISTALL
 * @since 21.11.2015
 */
public abstract class BaseDotNetLogicView implements DotNetLogicValueView
{
	private static final Logger LOG = Logger.getInstance(BaseDotNetLogicView.class);

	@Override
	public boolean canHandle(@Nonnull DotNetDebugContext debugContext, @Nonnull DotNetTypeProxy typeMirror)
	{
		return false;
	}

	public abstract void computeChildrenImpl(@Nonnull DotNetDebugContext debugContext,
			@Nonnull XNamedValue parentNode,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@Nonnull XValueChildrenList childrenList);

	@Override
	public void computeChildren(@Nonnull UserDataHolderBase dataHolder,
			@Nonnull DotNetDebugContext debugContext,
			@Nonnull XNamedValue parentNode,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@Nonnull XCompositeNode node)
	{

		try
		{
			XValueChildrenList childrenList = new XValueChildrenList();
			computeChildrenImpl(debugContext, parentNode, frameProxy, value, childrenList);
			node.addChildren(childrenList, true);
		}
		catch(Throwable e)
		{
			node.setErrorMessage(e.toString());

			LOG.warn(e);
		}
	}
}
