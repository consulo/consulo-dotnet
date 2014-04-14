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
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.run.DotNetRunProfileState;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;
import mono.debugger.VirtualMachine;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetDebugProcess extends XDebugProcess
{
	private final ExecutionResult myResult;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private DotNetDebugThread myDebugThread;

	public DotNetDebugProcess(XDebugSession session, ExecutionResult result, DotNetRunProfileState state)
	{
		super(session);
		myResult = result;
		session.setPauseActionSupported(true);
		myDebugConnectionInfo = state.getDebugConnectionInfo();
		myDebugThread = new DotNetDebugThread(session, myDebugConnectionInfo);
		myDebugThread.start();
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
		return new XBreakpointHandler[]{};
	}

	@NotNull
	@Override
	public XDebuggerEditorsProvider getEditorsProvider()
	{
		return new XDebuggerEditorsProviderBase()
		{
			@Override
			protected PsiFile createExpressionCodeFragment(
					@NotNull Project project, @NotNull String s, @Nullable PsiElement element, boolean b)
			{
				return null;
			}

			@NotNull
			@Override
			public FileType getFileType()
			{
				return PlainTextFileType.INSTANCE;
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
		myDebugThread.addCommand(new Processor<VirtualMachine>()
		{
			@Override
			public boolean process(VirtualMachine virtualMachine)
			{
				virtualMachine.resume();
				return false;
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

	}

	@Override
	public void startStepInto()
	{

	}

	@Override
	public void startStepOut()
	{

	}

	@Override
	public void stop()
	{
		myDebugThread.setStop();
		myDebugThread.normalizeBreakpoints();
	}

	@Override
	public void runToPosition(@NotNull XSourcePosition xSourcePosition)
	{

	}
}
