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

import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import mono.debugger.ThreadMirror;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetSuspendContext extends XSuspendContext
{
	private final DotNetDebugContext myDebuggerContext;
	private final ThreadMirror myThreadMirror;

	private XExecutionStack myActiveExecutionStack;

	public DotNetSuspendContext(DotNetDebugContext debuggerContext, ThreadMirror threadMirror)
	{
		myDebuggerContext = debuggerContext;
		myThreadMirror = threadMirror;

		myActiveExecutionStack = new DotNetExecutionStack(debuggerContext, threadMirror);
	}

	@Override
	public void computeExecutionStacks(XExecutionStackContainer container)
	{
		List<ThreadMirror> threadMirrors = myDebuggerContext.getVirtualMachine().allThreads();

		List<XExecutionStack> executionStacks = new ArrayList<XExecutionStack>();

		for(ThreadMirror threadMirror : threadMirrors)
		{
			if(threadMirror.id() == myThreadMirror.id())
			{
				executionStacks.add(myActiveExecutionStack);
			}
			else
			{
				executionStacks.add(new DotNetExecutionStack(myDebuggerContext, threadMirror));
			}
		}

		if(!executionStacks.contains(myActiveExecutionStack))
		{
			executionStacks.add(myActiveExecutionStack);
		}
		container.addExecutionStack(executionStacks, true);
	}

	@Nullable
	@Override
	public XExecutionStack getActiveExecutionStack()
	{
		return myActiveExecutionStack;
	}
}
