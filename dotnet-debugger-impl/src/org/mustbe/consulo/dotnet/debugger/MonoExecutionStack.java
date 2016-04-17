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

import javax.swing.Icon;

import org.consulo.lombok.annotations.ArrayFactoryFields;
import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.proxy.DotNetStackFrameMirrorProxyImpl;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.BitUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import mono.debugger.StackFrameMirror;
import mono.debugger.ThreadMirror;

/**
 * @author VISTALL
 * @since 10.04.14
 */
@ArrayFactoryFields
@Logger
public class MonoExecutionStack extends XExecutionStack
{
	private MonoStackFrame myTopFrame;
	private boolean myTopFrameCalculated;

	private DotNetDebugContext myDebuggerContext;
	private ThreadMirror myThreadMirror;

	public MonoExecutionStack(DotNetDebugContext debuggerContext, ThreadMirror threadMirror)
	{
		super(calcName(debuggerContext, threadMirror), getIcon(threadMirror));
		myDebuggerContext = debuggerContext;
		myThreadMirror = threadMirror;
	}

	@NotNull
	private static String calcName(DotNetDebugContext debuggerContext, ThreadMirror threadMirror)
	{
		String name = threadMirror.name();
		if(StringUtil.isEmpty(name))
		{
			return "[" + MonoSuspendContext.getThreadId(debuggerContext, threadMirror) + "] Unnamed";
		}
		return name;
	}

	private static Icon getIcon(ThreadMirror threadMirror)
	{
		int state = threadMirror.state();
		if(BitUtil.isSet(state, ThreadMirror.ThreadState.Running))
		{
			return AllIcons.Debugger.ThreadRunning;
		}
		return AllIcons.Debugger.ThreadFrozen;
	}

	public ThreadMirror getThreadMirror()
	{
		return myThreadMirror;
	}

	@Nullable
	private XStackFrame calcTopFrame()
	{
		try
		{
			List<StackFrameMirror> frames = myThreadMirror.frames();
			StackFrameMirror frame = ArrayUtil2.safeGet(frames, 0);
			if(frame == null)
			{
				return null;
			}
			return myTopFrame = new MonoStackFrame(myDebuggerContext, new DotNetStackFrameMirrorProxyImpl(frame, 0));
		}
		finally
		{
			myTopFrameCalculated = true;
		}
	}

	@Nullable
	@Override
	public XStackFrame getTopFrame()
	{
		if(myTopFrameCalculated)
		{
			return myTopFrame;
		}
		return calcTopFrame();
	}

	@Override
	public void computeStackFrames(XStackFrameContainer frameContainer)
	{
		List<StackFrameMirror> frames = myThreadMirror.frames();

		List<MonoStackFrame> stackFrames = new ArrayList<MonoStackFrame>();
		for(int j = 0; j < frames.size(); j++)
		{
			StackFrameMirror stackFrameMirror = frames.get(j);

			MonoStackFrame stackFrame = new MonoStackFrame(myDebuggerContext, new DotNetStackFrameMirrorProxyImpl(stackFrameMirror, j));

			if(j == 0)
			{
				myTopFrameCalculated = true;
				myTopFrame = stackFrame;
			}

			stackFrames.add(stackFrame);
		}

		frameContainer.addStackFrames(stackFrames, true);
	}

	@Override
	public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container)
	{
		throw new IllegalArgumentException();
	}
}
