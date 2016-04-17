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

package org.mustbe.consulo.dotnet.debugger;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import mono.debugger.ThreadMirror;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class MonoSuspendContext extends XSuspendContext
{
	private final long mySystemThreadId;

	private final MonoExecutionStack[] myExecutionStacks;
	private DotNetDebugContext myDebuggerContext;

	public MonoSuspendContext(@NotNull DotNetDebugContext debuggerContext, @Nullable ThreadMirror threadMirror)
	{
		myDebuggerContext = debuggerContext;
		mySystemThreadId = threadMirror == null ? -1 : getThreadId(debuggerContext, threadMirror);

		List<ThreadMirror> threadMirrors = debuggerContext.getVirtualMachine().allThreads();
		List<MonoExecutionStack> list = new ArrayList<MonoExecutionStack>(threadMirrors.size());
		for(ThreadMirror mirror : threadMirrors)
		{
			list.add(new MonoExecutionStack(debuggerContext, mirror));
		}
		myExecutionStacks = ContainerUtil.toArray(list, MonoExecutionStack.ARRAY_FACTORY);
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
		if(mySystemThreadId == -1)
		{
			return null;
		}
		for(MonoExecutionStack executionStack : myExecutionStacks)
		{
			if(getThreadId(myDebuggerContext, executionStack.getThreadMirror()) == mySystemThreadId)
			{
				return executionStack;
			}
		}
		return null;
	}

	public static long getThreadId(DotNetDebugContext context, ThreadMirror threadMirror)
	{
		if(context.getVirtualMachine().isSupportSystemThreadId())
		{
			return threadMirror.systemThreadId();
		}
		else
		{
			return threadMirror.id();
		}
	}
}
