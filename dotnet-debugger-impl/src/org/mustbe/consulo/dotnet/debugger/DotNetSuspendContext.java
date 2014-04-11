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

import java.util.List;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import mono.debugger.ThreadMirror;
import mono.debugger.VirtualMachine;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetSuspendContext extends XSuspendContext
{
	private final Project myProject;
	private XExecutionStack[] myXExecutionStacks;
	private XExecutionStack myActivateStack;

	public DotNetSuspendContext(VirtualMachine virtualMachine, Project project, ThreadMirror active)
	{
		myProject = project;
		List<ThreadMirror> threadMirrors = virtualMachine.allThreads();
		myXExecutionStacks = new XExecutionStack[threadMirrors.size()];
		for(int i = 0; i < myXExecutionStacks.length; i++)
		{
			ThreadMirror threadMirror = threadMirrors.get(i);
			myXExecutionStacks[i] = new DotNetExecutionStack(threadMirror, myProject);
			if(active != null && threadMirror.id() == active.id())
			{
				myActivateStack = myXExecutionStacks[i];
			}
		}
		if(myActivateStack == null)
		{
			myActivateStack = myXExecutionStacks[0];
		}
	}

	@Nullable
	@Override
	public XExecutionStack getActiveExecutionStack()
	{
		return myActivateStack;
	}

	@Override
	public XExecutionStack[] getExecutionStacks()
	{
		return myXExecutionStacks;
	}

}
