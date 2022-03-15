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

import consulo.application.AllIcons;
import consulo.debugger.frame.XExecutionStack;
import consulo.debugger.frame.XStackFrame;
import consulo.dotnet.debugger.proxy.DotNetNotSuspendedException;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.ui.image.Image;
import consulo.util.collection.ArrayFactory;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetExecutionStack extends XExecutionStack
{
	public static final DotNetExecutionStack[] EMPTY_ARRAY = new DotNetExecutionStack[0];

	public static ArrayFactory<DotNetExecutionStack> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new DotNetExecutionStack[count];

	private DotNetStackFrame myTopFrame;
	private boolean myTopFrameCalculated;

	private DotNetDebugContext myDebuggerContext;
	private DotNetThreadProxy myThreadProxy;

	public DotNetExecutionStack(@Nonnull DotNetDebugContext debuggerContext, @Nonnull DotNetThreadProxy threadProxy)
	{
		super(calcName(threadProxy), getIcon(threadProxy));

		DotNetVirtualMachineUtil.checkCallForUIThread();

		myDebuggerContext = debuggerContext;
		myThreadProxy = threadProxy;

		calcTopFrame(); // calc top frame
	}

	@Nonnull
	private static String calcName(DotNetThreadProxy threadMirror)
	{
		return "[" + threadMirror.getId() + "] " + StringUtil.defaultIfEmpty(threadMirror.getName(), "Unnamed");
	}

	private static Image getIcon(DotNetThreadProxy threadProxy)
	{
		if(threadProxy.isSuspended())
		{
			return AllIcons.Debugger.ThreadSuspended;
		}
		if(threadProxy.isRunning())
		{
			return AllIcons.Debugger.ThreadRunning;
		}
		return AllIcons.Debugger.ThreadFrozen;
	}

	public DotNetThreadProxy getThreadProxy()
	{
		return myThreadProxy;
	}

	@Nullable
	private XStackFrame calcTopFrame()
	{
		try
		{
			DotNetVirtualMachineUtil.checkCallForUIThread();

			DotNetStackFrameProxy frame = myThreadProxy.getFrame(0);
			if(frame == null)
			{
				return null;
			}
			return myTopFrame = new DotNetStackFrame(myDebuggerContext, frame);
		}
		catch(DotNetNotSuspendedException ignored)
		{
			return null;
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
		myDebuggerContext.invoke(() ->
		{
			List<DotNetStackFrameProxy> frames = Collections.emptyList();
			try
			{
				frames = myThreadProxy.getFrames();
			}
			catch(DotNetNotSuspendedException ignored)
			{
			}

			List<DotNetStackFrame> stackFrames = new ArrayList<>();
			for(int j = 0; j < frames.size(); j++)
			{
				DotNetStackFrameProxy frameProxy = frames.get(j);

				DotNetStackFrame stackFrame = new DotNetStackFrame(myDebuggerContext, frameProxy);

				if(j == 0)
				{
					myTopFrameCalculated = true;
					myTopFrame = stackFrame;
				}

				stackFrames.add(stackFrame);
			}

			frameContainer.addStackFrames(stackFrames, true);
		});
	}
}
