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
import org.mustbe.consulo.csharp.lang.CSharpFileType;
import org.mustbe.consulo.csharp.lang.psi.CSharpExpressionFragmentFactory;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetAbstractBreakpointType;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.run.DotNetRunProfileState;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.EvaluationMode;
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
	private ExecutionResult myResult;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private DotNetDebugThread myDebugThread;

	private EventSet myPausedEventSet;

	public DotNetDebugProcess(XDebugSession session, DotNetRunProfileState state)
	{
		super(session);
		session.setPauseActionSupported(true);
		myDebugConnectionInfo = state.getDebugConnectionInfo();
		myDebugThread = new DotNetDebugThread(session, this, myDebugConnectionInfo);
		myDebugThread.start();
	}

	public void setExecutionResult(ExecutionResult executionResult)
	{
		myResult = executionResult;
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

	@Override
	public XBreakpointHandler<?>[] getBreakpointHandlers()
	{
		return new XBreakpointHandler[]{new XBreakpointHandler<XLineBreakpoint<XBreakpointProperties>>(DotNetAbstractBreakpointType.class)
		{

			@Override
			public void registerBreakpoint(@NotNull final XLineBreakpoint<XBreakpointProperties> breakpoint)
			{
				val project = getSession().getProject();
				val debuggerManager = XDebuggerManager.getInstance(project);
				val breakpointManager = debuggerManager.getBreakpointManager();

				myDebugThread.addCommand(new Processor<VirtualMachine>()
				{
					@Override
					public boolean process(VirtualMachine virtualMachine)
					{
						val type = (DotNetAbstractBreakpointType) breakpoint.getType();

						EventRequest eventRequest = type.createEventRequest(project, virtualMachine, breakpoint);
						if(eventRequest == null)
						{
							breakpointManager.updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_invalid_breakpoint, null);
							return false;
						}
						breakpointManager.updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_verified_breakpoint, null);
						eventRequest.enable();
						breakpoint.putUserData(DotNetDebugThread.EVENT_REQUEST, eventRequest);

						return false;
					}
				});
			}

			@Override
			public void unregisterBreakpoint(@NotNull XLineBreakpoint<XBreakpointProperties> breakpoint, boolean temporary)
			{
				EventRequest eventRequest = breakpoint.getUserData(DotNetDebugThread.EVENT_REQUEST);
				if(eventRequest != null)
				{
					eventRequest.disable();
				}
			}
		}};
	}

	@NotNull
	@Override
	public XDebuggerEditorsProvider getEditorsProvider()
	{
		return new XDebuggerEditorsProvider()
		{
			@NotNull
			@Override
			public FileType getFileType()
			{
				return CSharpFileType.INSTANCE;
			}

			@NotNull
			@Override
			public Document createDocument(
					@NotNull Project project, @NotNull String text, @Nullable XSourcePosition sourcePosition, @NotNull EvaluationMode mode)
			{
				PsiFile expressionFragment = CSharpExpressionFragmentFactory.createExpressionFragment(project, text);
				return PsiDocumentManager.getInstance(project).getDocument(expressionFragment);
			}
		};
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
				getSession().positionReached(new DotNetSuspendContext(virtualMachine, getSession().getProject(), null));
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
