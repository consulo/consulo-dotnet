/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.microsoft.debugger;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
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
import consulo.dotnet.microsoft.debugger.breakpoint.MicrosoftBreakpointUtil;
import consulo.dotnet.microsoft.debugger.proxy.MicrosoftVirtualMachineProxy;
import mssdw.DebugInformationResult;
import mssdw.ThreadMirror;
import mssdw.event.EventSet;
import mssdw.request.BreakpointRequest;
import mssdw.request.EventRequestManager;
import mssdw.request.StepRequest;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftDebugProcess extends DotNetDebugProcessBase
{
	private class MyXBreakpointListener implements XBreakpointListener<XBreakpoint<?>>
	{
		@Override
		public void breakpointAdded(@NotNull final XBreakpoint<?> breakpoint)
		{
			myDebugThread.processAnyway(new Processor<MicrosoftVirtualMachineProxy>()
			{
				@Override
				@SuppressWarnings("unchecked")
				public boolean process(final MicrosoftVirtualMachineProxy virtualMachine)
				{
					XBreakpointType<?, ?> type = breakpoint.getType();
					if(type == DotNetLineBreakpointType.getInstance())
					{
						MicrosoftBreakpointUtil.createBreakpointRequest(getSession(), virtualMachine, (XLineBreakpoint) breakpoint);
					}
					else if(type == DotNetExceptionBreakpointType.getInstance())
					{
						MicrosoftBreakpointUtil.createExceptionRequest(virtualMachine, (XBreakpoint<DotNetExceptionBreakpointProperties>) breakpoint);
					}

					return false;
				}
			});
		}

		@Override
		public void breakpointRemoved(@NotNull final XBreakpoint<?> breakpoint)
		{
			myDebugThread.processAnyway(new Processor<MicrosoftVirtualMachineProxy>()
			{
				@Override
				public boolean process(MicrosoftVirtualMachineProxy virtualMachine)
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
	private final MicrosoftDebugThread myDebugThread;

	private EventSet myPausedEventSet;
	private XBreakpointManager myBreakpointManager;
	private final XBreakpointListener<XBreakpoint<?>> myBreakpointListener = new MyXBreakpointListener();

	public MicrosoftDebugProcess(XDebugSession session, RunProfile runProfile, DebugConnectionInfo debugConnectionInfo)
	{
		super(session, runProfile);
		session.setPauseActionSupported(true);
		myDebugConnectionInfo = debugConnectionInfo;
		myDebugThread = new MicrosoftDebugThread(session, this, myDebugConnectionInfo);

		myBreakpointManager = XDebuggerManager.getInstance(session.getProject()).getBreakpointManager();
		myBreakpointManager.addBreakpointListener(myBreakpointListener);
	}

	@NotNull
	public MicrosoftDebugThread getDebugThread()
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
		myDebugThread.addCommand(new Processor<MicrosoftVirtualMachineProxy>()
		{
			@Override
			public boolean process(MicrosoftVirtualMachineProxy virtualMachine)
			{
				virtualMachine.suspend();
				getSession().positionReached(new DotNetSuspendContext(createDebugContext(virtualMachine, null), -1));
				return false;
			}
		});
	}

	@Override
	public void runToPosition(@NotNull final XSourcePosition position)
	{
		if(myPausedEventSet == null)
		{
			return;
		}

		myDebugThread.addCommand(new Processor<MicrosoftVirtualMachineProxy>()
		{
			@Override
			public boolean process(MicrosoftVirtualMachineProxy virtualMachine)
			{
				virtualMachine.stopStepRequests();

				DebugInformationResult debugOffset = virtualMachine.getDelegate().findDebugOffset(position.getFile().getPath(), position.getLine() + 1, -1);
				if(debugOffset != null)
				{
					EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();

					BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(debugOffset);
					breakpointRequest.putProperty(RUN_TO_CURSOR, Boolean.TRUE);
					breakpointRequest.enable();
				}
				return true;
			}
		});
	}

	@Override
	public void resume()
	{
		myPausedEventSet = null;
		myDebugThread.addCommand(new Processor<MicrosoftVirtualMachineProxy>()
		{
			@Override
			public boolean process(MicrosoftVirtualMachineProxy virtualMachine)
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
		stepRequest(StepRequest.StepDepth.Over);
	}

	@Override
	public void startStepInto()
	{
		stepRequest(StepRequest.StepDepth.Into);
	}

	@Override
	public void startStepOut()
	{
		stepRequest(StepRequest.StepDepth.Out);
	}

	private void stepRequest(final StepRequest.StepDepth stepDepth)
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

		myDebugThread.addCommand(new Processor<MicrosoftVirtualMachineProxy>()
		{
			@Override
			public boolean process(MicrosoftVirtualMachineProxy virtualMachine)
			{
				EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
				StepRequest stepRequest = eventRequestManager.createStepRequest(threadMirror, stepDepth);
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
