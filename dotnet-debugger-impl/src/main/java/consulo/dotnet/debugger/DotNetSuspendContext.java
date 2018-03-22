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

package consulo.dotnet.debugger;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetSuspendContext extends XSuspendContext
{
	private final long myActiveThreadId;

	private final DotNetExecutionStack[] myExecutionStacks;

	public DotNetSuspendContext(@Nonnull DotNetDebugContext debuggerContext, long activeThreadId)
	{
		myActiveThreadId = activeThreadId;

		List<DotNetThreadProxy> threadProxies = debuggerContext.getVirtualMachine().getThreads();
		List<DotNetExecutionStack> list = new ArrayList<>(threadProxies.size());
		for(DotNetThreadProxy mirror : threadProxies)
		{
			list.add(new DotNetExecutionStack(debuggerContext, mirror));
		}
		myExecutionStacks = ContainerUtil.toArray(list, DotNetExecutionStack.ARRAY_FACTORY);
	}

	@Override
	public XExecutionStack[] getExecutionStacks()
	{
		return myExecutionStacks;
	}

	@Nullable
	@Override
	public XExecutionStack getActiveExecutionStack()
	{
		if(myActiveThreadId == -1)
		{
			return null;
		}
		for(DotNetExecutionStack executionStack : myExecutionStacks)
		{
			if(executionStack.getThreadProxy().getId() == myActiveThreadId)
			{
				return executionStack;
			}
		}
		return null;
	}
}
