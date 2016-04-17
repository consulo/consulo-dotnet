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

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetLineBreakpointType;
import org.mustbe.consulo.dotnet.debugger.linebreakType.properties.DotNetLineBreakpointProperties;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import consulo.dotnet.debugger.impl.DotNetDebugProcessBase;
import lombok.val;
import mono.debugger.ThreadMirror;
import mono.debugger.event.EventSet;
import mono.debugger.request.StepRequest;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class MonoDebugProcess extends DotNetDebugProcessBase
{
	private class MyXBreakpointListener implements XBreakpointListener<XLineBreakpoint<DotNetLineBreakpointProperties>>
	{
		@Override
		public void breakpointAdded(@NotNull final XLineBreakpoint<DotNetLineBreakpointProperties> breakpoint)
		{
			myDebugThread.processAnyway(new Processor<DotNetVirtualMachine>()
			{
				@Override
				public boolean process(final DotNetVirtualMachine virtualMachine)
				{
					DotNetLineBreakpointType type = (DotNetLineBreakpointType) breakpoint.getType();

					type.createRequest(getSession(), virtualMachine, breakpoint, null);

					return false;
				}
			});
		}

		@Override
		public void breakpointRemoved(@NotNull final XLineBreakpoint<DotNetLineBreakpointProperties> breakpoint)
		{
			myDebugThread.processAnyway(new Processor<DotNetVirtualMachine>()
			{
				@Override
				public boolean process(DotNetVirtualMachine virtualMachine)
				{
					virtualMachine.stopBreakpointRequests(breakpoint);
					return false;
				}
			});
		}

		@Override
		public void breakpointChanged(@NotNull XLineBreakpoint<DotNetLineBreakpointProperties> breakpoint)
		{
			if(breakpoint.isEnabled())
			{
				breakpointAdded(breakpoint);
			}
			else
			{
				breakpointRemoved(breakpoint);
			}
		}
	}

	private final DebugConnectionInfo myDebugConnectionInfo;
	private final DotNetDebugThread myDebugThread;

	private EventSet myPausedEventSet;
	private XBreakpointManager myBreakpointManager;
	private final XBreakpointListener myBreakpointListener = new MyXBreakpointListener();

	public MonoDebugProcess(XDebugSession session, DebugConnectionInfo debugConnectionInfo, RunProfile runProfile)
	{
		super(session);
		session.setPauseActionSupported(true);
		myDebugConnectionInfo = debugConnectionInfo;
		myDebugThread = new DotNetDebugThread(session, this, myDebugConnectionInfo, runProfile);

		myBreakpointManager = XDebuggerManager.getInstance(session.getProject()).getBreakpointManager();

		myBreakpointManager.addBreakpointListener(DotNetLineBreakpointType.getInstance(), myBreakpointListener);
	}

	@NotNull
	public DotNetDebugThread getDebugThread()
	{
		return myDebugThread;
	}

	@Override
	public void start()
	{
		myDebugThread.start();
	}

	@Override
	public void startPausing()
	{
		myDebugThread.addCommand(new Processor<DotNetVirtualMachine>()
		{
			@Override
			public boolean process(DotNetVirtualMachine virtualMachine)
			{
				virtualMachine.suspend();
				getSession().positionReached(new MonoSuspendContext(myDebugThread.createDebugContext(null), null));
				return false;
			}
		});
	}

	@Override
	public void resume()
	{
		myPausedEventSet = null;
		myDebugThread.addCommand(new Processor<DotNetVirtualMachine>()
		{
			@Override
			public boolean process(DotNetVirtualMachine virtualMachine)
			{
				virtualMachine.stopStepRequests();

				return true;
			}
		});
	}

	@Override
	public String getCurrentStateMessage()
	{
		if(myDebugThread.isConnected())
		{
			return "Connected to " + myDebugConnectionInfo.getHost() + ":" + myDebugConnectionInfo.getPort();
		}
		return XDebuggerBundle.message("debugger.state.message.disconnected");
	}

	@Override
	public void startStepOver()
	{
		stepRequest(StepRequest.StepDepth.Over, StepRequest.StepSize.Line);
	}

	@Override
	public void startStepInto()
	{
		stepRequest(StepRequest.StepDepth.Into, StepRequest.StepSize.Line);
	}

	@Override
	public void startStepOut()
	{
		stepRequest(StepRequest.StepDepth.Out, StepRequest.StepSize.Line);
	}

	private void stepRequest(final StepRequest.StepDepth stepDepth, final StepRequest.StepSize stepSize)
	{
		if(myPausedEventSet == null)
		{
			return;
		}
		final ThreadMirror threadMirror = myPausedEventSet.eventThread();
		if(threadMirror == null)
		{
			return;
		}

		myDebugThread.addCommand(new Processor<DotNetVirtualMachine>()
		{
			@Override
			public boolean process(DotNetVirtualMachine virtualMachine)
			{
				val eventRequestManager = virtualMachine.eventRequestManager();
				val stepRequest = eventRequestManager.createStepRequest(threadMirror, stepSize, stepDepth);
				stepRequest.enable();

				virtualMachine.addStepRequest(stepRequest);
				return true;
			}
		});
	}

	@Override
	public void stop()
	{
		myPausedEventSet = null;
		myDebugThread.setStop();
		normalizeBreakpoints();
		myBreakpointManager.removeBreakpointListener(myBreakpointListener);
	}

	public void setPausedEventSet(EventSet pausedEventSet)
	{
		myPausedEventSet = pausedEventSet;
	}
}
