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
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetLineBreakpointType;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import lombok.val;
import mono.debugger.ThreadMirror;
import mono.debugger.VirtualMachine;
import mono.debugger.event.EventSet;
import mono.debugger.request.EventRequest;
import mono.debugger.request.StepRequest;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetDebugProcess extends XDebugProcess
{
	private class MyXBreakpointListener implements XBreakpointListener<XLineBreakpoint<XBreakpointProperties>>
	{
		@Override
		public void breakpointAdded(@NotNull final XLineBreakpoint<XBreakpointProperties> breakpoint)
		{
			val project = getSession().getProject();

			myDebugThread.processAnyway(new Processor<VirtualMachine>()
			{
				@Override
				public boolean process(final VirtualMachine virtualMachine)
				{
					new ReadAction<Object>()
					{
						@Override
						protected void run(Result<Object> objectResult) throws Throwable
						{
							val type = (DotNetLineBreakpointType) breakpoint.getType();

							type.createRequest(project, virtualMachine, breakpoint, null);
						}
					}.execute();

					return false;
				}
			});
		}

		@Override
		public void breakpointRemoved(@NotNull final XLineBreakpoint<XBreakpointProperties> breakpoint)
		{
			myDebugThread.processAnyway(new Processor<VirtualMachine>()
			{
				@Override
				public boolean process(VirtualMachine virtualMachine)
				{
					EventRequest eventRequest = breakpoint.getUserData(DotNetDebugThread.EVENT_REQUEST);
					if(eventRequest != null)
					{
						eventRequest.disable();
					}

					if(eventRequest != null)
					{
						virtualMachine.eventRequestManager().deleteEventRequest(eventRequest);
					}
					return false;
				}
			});
		}

		@Override
		public void breakpointChanged(@NotNull XLineBreakpoint<XBreakpointProperties> breakpoint)
		{

		}
	}

	private ExecutionResult myResult;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private final DotNetDebugThread myDebugThread;

	private EventSet myPausedEventSet;
	private XBreakpointManager myBreakpointManager;
	private final XBreakpointListener myBreakpointListener = new MyXBreakpointListener();

	public DotNetDebugProcess(XDebugSession session, DebugConnectionInfo debugConnectionInfo, RunProfile runProfile)
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

	public void start()
	{
		myDebugThread.start();
	}

	public void setExecutionResult(ExecutionResult executionResult)
	{
		myResult = executionResult;
	}

	@Override
	public boolean checkCanInitBreakpoints()
	{
		return false;
	}

	@Nullable
	@Override
	protected ProcessHandler doGetProcessHandler()
	{
		return myResult.getProcessHandler();
	}

	@NotNull
	@Override
	public ExecutionConsole createConsole()
	{
		return myResult.getExecutionConsole();
	}

	@NotNull
	@Override
	public XDebuggerEditorsProvider getEditorsProvider()
	{
		return new DotNetEditorsProvider();
	}

	@Override
	public void startPausing()
	{
		myDebugThread.addCommand(new Processor<VirtualMachine>()
		{
			@Override
			public boolean process(VirtualMachine virtualMachine)
			{
				virtualMachine.suspend();
				getSession().positionReached(new DotNetSuspendContext(myDebugThread.createDebugContext(), null));
				return false;
			}
		});
	}

	@Override
	public void resume()
	{
		myPausedEventSet = null;
		myDebugThread.addCommand(new Processor<VirtualMachine>()
		{
			@Override
			public boolean process(VirtualMachine virtualMachine)
			{
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

		myDebugThread.addCommand(new Processor<VirtualMachine>()
		{
			@Override
			public boolean process(VirtualMachine virtualMachine)
			{
				val eventRequestManager = virtualMachine.eventRequestManager();
				val stepRequest = eventRequestManager.createStepRequest(threadMirror, stepSize, stepDepth);
				stepRequest.enable();
				return true;
			}
		});
	}

	@Override
	public void stop()
	{
		myPausedEventSet = null;
		myDebugThread.setStop();
		myDebugThread.normalizeBreakpoints();
		myBreakpointManager.removeBreakpointListener(myBreakpointListener);
	}

	@Override
	public void runToPosition(@NotNull XSourcePosition xSourcePosition)
	{

	}

	public void setPausedEventSet(EventSet pausedEventSet)
	{
		myPausedEventSet = pausedEventSet;
	}
}
