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
import com.intellij.xdebugger.frame.XStackFrame;
import mono.debugger.IncompatibleThreadStateException;
import mono.debugger.StackFrameMirror;
import mono.debugger.ThreadMirror;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetExecutionStack extends XExecutionStack
{
	private DotNetStackFrame myTopFrame;
	private List<DotNetStackFrame> stackFrames = new ArrayList<DotNetStackFrame>();

	public DotNetExecutionStack(DotNetDebugContext debuggerContext, ThreadMirror threadMirror)
	{
		super(threadMirror.name());

		try
		{
			List<StackFrameMirror> frames = threadMirror.frames();

			for(StackFrameMirror frame : frames)
			{
				if(myTopFrame == null)
				{
					myTopFrame = new DotNetStackFrame(debuggerContext, frame);
					continue;
				}

				stackFrames.add(new DotNetStackFrame(debuggerContext, frame));
			}
		}
		catch(IncompatibleThreadStateException e)
		{
			e.printStackTrace();
		}
	}

	@Nullable
	@Override
	public XStackFrame getTopFrame()
	{
		return myTopFrame;
	}

	@Override
	public void computeStackFrames(int i, XStackFrameContainer xStackFrameContainer)
	{
		xStackFrameContainer.addStackFrames(stackFrames, true);
	}
}
