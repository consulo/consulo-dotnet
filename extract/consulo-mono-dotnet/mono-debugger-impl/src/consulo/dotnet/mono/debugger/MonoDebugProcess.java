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

package consulo.dotnet.mono.debugger;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import consulo.dotnet.debugger.DotNetDebugProcessBase;
import consulo.dotnet.debugger.DotNetSuspendContext;
import consulo.dotnet.debugger.breakpoint.DotNetExceptionBreakpointType;
import consulo.dotnet.debugger.breakpoint.DotNetLineBreakpointType;
import consulo.dotnet.debugger.breakpoint.properties.DotNetExceptionBreakpointProperties;
import consulo.dotnet.mono.debugger.breakpoint.MonoBreakpointUtil;
import consulo.dotnet.mono.debugger.proxy.MonoVirtualMachineProxy;
import mono.debugger.ThreadMirror;
import mono.debugger.event.EventSet;
import mono.debugger.request.EventRequestManager;
import mono.debugger.request.StepRequest;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class MonoDebugProcess extends DotNetDebugProcessBase
{
	private class MyXBreakpointListener implements XBreakpointListener<XBreakpoint<?>>
	{
		@Override
		public void breakpointAdded(@NotNull final XBreakpoint<?> breakpoint)
		{
			myDebugThread.processAnyway(new Processor<MonoVirtualMachineProxy>()
			{
				@Override
				@SuppressWarnings("unchecked")
				public boolean process(final MonoVirtualMachineProxy virtualMachine)
				{
					XBreakpointType<?, ?> type = breakpoint.getType();
					if(type == DotNetLineBreakpointType.getInstance())
					{
						MonoBreakpointUtil.createBreakpointRequest(getSession(), virtualMachine, (XLineBreakpoint) breakpoint, null);
					}
					else if(type == DotNetExceptionBreakpointType.getInstance())
					{
						MonoBreakpointUtil.createExceptionRequest(virtualMachine, (XBreakpoint<DotNetExceptionBreakpointProperties>) breakpoint, null);
					}

					return false;
				}
			});
		}

		@Override
		public void breakpointRemoved(@NotNull final XBreakpoint<?> breakpoint)
		{
			myDebugThread.processAnyway(new Processor<MonoVirtualMachineProxy>()
			{
				@Override
				public boolean process(MonoVirtualMachineProxy virtualMachine)
				{
					virtualMachine.stopBreakpointRequests(breakpoint);
					return false;
				}
			});
		}

		@Override
		public void breakpointChanged(@NotNull XBreakpoint<?> breakpoint)
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
	private final MonoDebugThread myDebugThread;

	private EventSet myPausedEventSet;
	private XBreakpointManager myBreakpointManager;
	private final XBreakpointListener<XBreakpoint<?>> myBreakpointListener = new MyXBreakpointListener();

	public MonoDebugProcess(XDebugSession session, RunProfile runProfile, DebugConnectionInfo debugConnectionInfo)
	{
		super(session, runProfile);
		session.setPauseActionSupported(true);
		myDebugConnectionInfo = debugConnectionInfo;
		myDebugThread = new MonoDebugThread(session, this, myDebugConnectionInfo);

		myBreakpointManager = XDebuggerManager.getInstance(session.getProject()).getBreakpointManager();
		myBreakpointManager.addBreakpointListener(myBreakpointListener);
	}

	@NotNull
	public MonoDebugThread getDebugThread()
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
		myDebugThread.addCommand(new Processor<MonoVirtualMachineProxy>()
		{
			@Override
			public boolean process(MonoVirtualMachineProxy virtualMachine)
			{
				virtualMachine.suspend();
				getSession().positionReached(new DotNetSuspendContext(createDebugContext(virtualMachine, null), -1));
				return false;
			}
		});
	}

	@Override
	public void resume()
	{
		myPausedEventSet = null;
		myDebugThread.addCommand(new Processor<MonoVirtualMachineProxy>()
		{
			@Override
			public boolean process(MonoVirtualMachineProxy virtualMachine)
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

		myDebugThread.addCommand(new Processor<MonoVirtualMachineProxy>()
		{
			@Override
			public boolean process(MonoVirtualMachineProxy virtualMachine)
			{
				EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
				StepRequest stepRequest = eventRequestManager.createStepRequest(threadMirror, stepSize, stepDepth);
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
